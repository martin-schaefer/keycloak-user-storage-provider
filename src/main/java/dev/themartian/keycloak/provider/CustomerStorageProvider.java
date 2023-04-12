package dev.themartian.keycloak.provider;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.collections4.MapUtils;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static dev.themartian.keycloak.provider.CustomerModel.*;
import static org.keycloak.utils.StringUtil.isNotBlank;

public class CustomerStorageProvider implements UserStorageProvider, UserLookupProvider, UserQueryProvider, UserRegistrationProvider {

    private final KeycloakSession session;
    private final ComponentModel storageProviderModel;
    private final Jdbi jdbi;
    private static final String CUSTOMER_BY_EMAIL = String.format("select * from customer where %1$s = :%1$s", EMAIL);
    private static final String INSERT_CUSTOMER =
            String.format("insert into customer (%s, %s, %s, %s) values(?, ?, ?, ?)", CUSTOMER_ID, EMAIL, EMAIL_VERIFIED, ENABLED);
    private static final String DELETE_CUSTOMER =
            String.format("delete from customer where %s = ?", CUSTOMER_ID);

    public CustomerStorageProvider(@NonNull KeycloakSession session, @NonNull ComponentModel storageProviderModel, @NonNull ConnectionProperties connectionProperties) {
        this.session = session;
        this.storageProviderModel = storageProviderModel;
        jdbi = openJdbi(connectionProperties);
    }

    private RowMapper<UserModel> userModelMapper(RealmModel realmModel) {
        return (rs, ctx) -> new CustomerModel.Builder(session, realmModel, storageProviderModel)
                .customerId(rs.getString(CUSTOMER_ID))
                .email(rs.getString(EMAIL))
                .emailVerified(rs.getBoolean(EMAIL_VERIFIED))
                .firstName(rs.getString(FIRST_NAME))
                .lastName(rs.getString(LAST_NAME))
                .enabled(rs.getBoolean(ENABLED))
                .build();
    }

    @SneakyThrows
    private Jdbi openJdbi(ConnectionProperties connectionProperties) {
        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", connectionProperties.host(), connectionProperties.port(), connectionProperties.db());
        return Jdbi.create(
                jdbcUrl,
                connectionProperties.user(),
                connectionProperties.password());
    }

    @Override
    @SneakyThrows
    public void close() {
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        String email = StorageId.externalId(id);
        return jdbi.withHandle(handle -> handle.createQuery(CUSTOMER_BY_EMAIL)
                .bind(EMAIL, email).map(userModelMapper(realm)).one());
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        return getUserByEmail(realm, username);
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        return jdbi.withHandle(handle -> handle.createQuery(CUSTOMER_BY_EMAIL)
                .bind(EMAIL, email).map(userModelMapper(realm)).one());
    }

    /**
     * @param realm       a reference to the realm.
     * @param search      case insensitive list of string separated by whitespaces.
     * @param firstResult first result to return. Ignored if negative, zero, or {@code null}.
     * @param maxResults  maximum number of results to return. Ignored if negative or {@code null}.
     * @return
     */
    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, String search, Integer firstResult, Integer maxResults) {
        StringBuilder query = new StringBuilder("select * from customer");
        final Map<String, Object> arguments = new HashMap<>();
        if (isNotBlank(search)) {
            query.append(String.format(" where %s ilike :search or %s ilike :search or %s ilike :search",
                    EMAIL, LAST_NAME, FIRST_NAME));
            arguments.put("search", "%" + search.trim() + "%");
        }
        completeQuery(firstResult, maxResults, query, arguments);
        return jdbi.withHandle(handle ->
                handle.createQuery(query)
                        .bindMap(arguments)
                        .map(userModelMapper(realm)).stream());
    }

    /**
     * @param realm       a reference to the realm.
     * @param params      a map containing the search parameters.
     * @param firstResult first result to return. Ignored if negative, zero, or {@code null}.
     * @param maxResults  maximum number of results to return. Ignored if negative or {@code null}.
     * @return
     */
    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult, Integer maxResults) {
        StringBuilder query = new StringBuilder("select * from customer");
        final Map<String, Object> arguments = copyArguments(params);
        if (MapUtils.isNotEmpty(arguments)) {
            query.append(" where");
            int count = 0;
            for (Map.Entry entry : arguments.entrySet()) {
                if (count > 0) {
                    query.append(" and");
                }
                query.append(String.format(" %1$s = :%1$s", entry.getKey()));
                count++;
            }
        }
        completeQuery(firstResult, maxResults, query, arguments);
        return jdbi.withHandle(handle ->
                handle.createQuery(query)
                        .bindMap(arguments)
                        .map(userModelMapper(realm)).stream());
    }

    private Map<String, Object> copyArguments(Map<String, String> params) {
        final Map<String, Object> arguments = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if ((entry.getKey().equals(ENABLED) || entry.getKey().equals(EMAIL_VERIFIED))
                && entry.getValue() != null) {
                arguments.put(entry.getKey(), Boolean.valueOf(entry.getValue().toString()));
            } else {
                arguments.put(entry.getKey(), entry.getValue());
            }
        }
        return arguments;
    }

    private void completeQuery(Integer firstResult, Integer maxResults, StringBuilder query, Map<String, Object> arguments) {
        query.append(String.format(" order by %s, %s",
                LAST_NAME, FIRST_NAME));
        if (firstResult != null && firstResult > 0) {
            query.append(" offset :offset");
            arguments.put("offset", firstResult);
        }
        if (maxResults != null && maxResults > -1) {
            query.append(" limit :limit");
            arguments.put("limit", maxResults);
        }
    }

    /**
     * @param realm       a reference to the realm.
     * @param group       a reference to the group.
     * @param firstResult first result to return. Ignored if negative, zero, or {@code null}.
     * @param maxResults  maximum number of results to return. Ignored if negative or {@code null}.
     * @return
     */
    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group, Integer firstResult, Integer maxResults) {
        return Stream.empty();
    }

    /**
     * @param realm     a reference to the realm.
     * @param attrName  the attribute name.
     * @param attrValue the attribute value.
     * @return
     */
    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realm, String attrName, String attrValue) {
        return searchForUserStream(realm, Map.of(attrName, attrValue), null, null);
    }

    /**
     * @param realm    a reference to the realm
     * @param username a username the created user will be assigned
     * @return
     */
    @Override
    public UserModel addUser(RealmModel realm, @NonNull String username) {
        String customerId = UUID.randomUUID().toString();
        jdbi.withHandle(h -> h.execute(INSERT_CUSTOMER,
                customerId, username, false, true));
        return new CustomerModel.Builder(session, realm, storageProviderModel)
                .customerId(customerId)
                .email(username)
                .enabled(true)
                .build();
    }

    /**
     * @param realm a reference to the realm
     * @param user  a reference to the user that is removed
     * @return
     */
    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        try (Handle handle = jdbi.open()) {
            return handle.execute(DELETE_CUSTOMER, user.getId()) == 1;
        }
    }
}
