package dev.themartian.keycloak.provider;/*
My File Header
*/

import lombok.NonNull;
import lombok.SneakyThrows;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;

public class CustomerStorageProvider implements UserStorageProvider, UserLookupProvider {

    private final KeycloakSession session;
    private final ComponentModel storageProviderModel;
    private final Jdbi jdbi;

    private static final String CUSTOMER_BY_FIELD = "select * from customer where %1$s = :%1$s";
    private static final String CUSTOMER_BY_ID = String.format(CUSTOMER_BY_FIELD, CustomerModel.ID);
    private static final String CUSTOMER_BY_EMAIL = String.format(CUSTOMER_BY_FIELD, CustomerModel.EMAIL);

    public CustomerStorageProvider(@NonNull KeycloakSession session, @NonNull ComponentModel storageProviderModel, @NonNull ConnectionProperties connectionProperties) {
        this.session = session;
        this.storageProviderModel = storageProviderModel;
        jdbi = openJdbi(connectionProperties);
    }

    private RowMapper<UserModel> userModelMapper(RealmModel realmModel) {
        return (rs, ctx) -> new CustomerModel.Builder(session, realmModel, storageProviderModel)
                .id(rs.getString(CustomerModel.ID))
                .email(rs.getString(CustomerModel.EMAIL))
                .emailVerified(rs.getBoolean(CustomerModel.EMAIL_VERIFIED))
                .firstName(rs.getString(CustomerModel.FIRST_NAME))
                .lastName(rs.getString(CustomerModel.LAST_NAME))
                .enabled(rs.getBoolean(CustomerModel.ENABLED))
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
        return jdbi.withHandle(handle -> handle.createQuery(CUSTOMER_BY_ID)
                .bind(CustomerModel.ID, id).map(userModelMapper(realm)).one());
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        return getUserByEmail(realm, username);
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        return jdbi.withHandle(handle -> handle.createQuery(CUSTOMER_BY_EMAIL)
                .bind(CustomerModel.EMAIL, email).map(userModelMapper(realm)).one());
    }

}
