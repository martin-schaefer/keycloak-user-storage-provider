package dev.themartian.keycloak.provider;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.DefaultKeycloakSession;
import org.keycloak.services.DefaultKeycloakSessionFactory;
import org.mockito.Mockito;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static dev.themartian.keycloak.provider.CustomerModel.*;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class CustomerStorageProviderTest {

    @Container
    private static final JdbcDatabaseContainer CONTAINER = new PostgreSQLContainer().withInitScript("init.sql");


    private CustomerStorageProvider givenCustomerStorageProvider() {
        DefaultKeycloakSession session = new DefaultKeycloakSession(new DefaultKeycloakSessionFactory());
        ComponentModel storageProviderModel = Mockito.mock(ComponentModel.class);
        ConnectionProperties connectionProperties = new ConnectionProperties(
                CONTAINER.getHost(), CONTAINER.getFirstMappedPort(), CONTAINER.getDatabaseName(), CONTAINER.getUsername(), CONTAINER.getPassword());
        CustomerStorageProvider customerStorageProvider = new CustomerStorageProvider(session, storageProviderModel, connectionProperties);
        return customerStorageProvider;
    }

    /**
     * Method under test: {@link CustomerStorageProvider#getUserById(RealmModel, String)}
     */
    @Test
    @DisplayName("Get customer by id")
    public void getUserById() {

        // given
        CustomerStorageProvider customerStorageProvider = givenCustomerStorageProvider();
        RealmModel realmModel = Mockito.mock(RealmModel.class);

        // when
        UserModel userModel = customerStorageProvider.getUserById(realmModel, "03");

        // then
        assertThat(userModel.getId()).isEqualTo("03");
        assertThat(userModel.getEmail()).isEqualTo("sabrina-km@mail.org");
        assertThat(userModel.isEmailVerified()).isTrue();
        assertThat(userModel.getFirstName()).isEqualTo("Sabrina");
        assertThat(userModel.getLastName()).isEqualTo("Kühlemann");
        assertThat(userModel.isEnabled()).isFalse();

    }

    /**
     * Method under test: {@link CustomerStorageProvider#searchForUserStream(RealmModel, String, Integer, Integer)}
     */
    @ParameterizedTest(name = "Search with {0} (first: {1}, max: {2}) should return customer {3}")
    @MethodSource("searchValues")
    void searchForUserStream(String search, Integer firstResult, Integer maxResults, String... expectedIds) {

        // given
        CustomerStorageProvider customerStorageProvider = givenCustomerStorageProvider();
        RealmModel realmModel = Mockito.mock(RealmModel.class);

        // then
        Stream<UserModel> userModelStream = customerStorageProvider.searchForUserStream(realmModel, search, firstResult, maxResults);

        // then
        List<UserModel> userModels = userModelStream.toList();
        assertThat(userModels).hasSize(expectedIds.length);
        assertThat(userModels.stream().map(UserModel::getId)).containsExactly(expectedIds);
    }

    static Stream<Arguments> searchValues() {
        return Stream.of(
                Arguments.arguments("", null, null, new String[]{"04", "05", "03", "02", "01"}),
                Arguments.arguments("mAx", null, null, new String[]{"05", "02", "01"}),
                Arguments.arguments("mAx", null, 2, new String[]{"05", "02"}),
                Arguments.arguments("mAx", 1, null, new String[]{"02", "01"}),
                Arguments.arguments(".com", null, null, new String[]{"04", "02"}),
                Arguments.arguments(".com", 0, 1, new String[]{"04"}),
                Arguments.arguments("MAN", null, null, new String[]{"04", "03"}),
                Arguments.arguments("STei", null, null, new String[]{"05"}),
                Arguments.arguments("NixDa", null, null, new String[0])
        );
    }

    @ParameterizedTest(name = "Search with params {0} (first: {1}, max: {2}) should return customer {3}")
    @MethodSource("searchParams")
    void searchForUserStream(Map<String, String> params, Integer firstResult, Integer maxResults, String... expectedIds) {

        // given
        CustomerStorageProvider customerStorageProvider = givenCustomerStorageProvider();
        RealmModel realmModel = Mockito.mock(RealmModel.class);

        // then
        Stream<UserModel> userModelStream = customerStorageProvider.searchForUserStream(realmModel, params, firstResult, maxResults);

        // then
        List<UserModel> userModels = userModelStream.toList();
        assertThat(userModels).hasSize(expectedIds.length);
        assertThat(userModels.stream().map(UserModel::getId)).containsExactly(expectedIds);
    }

    public static Stream<Arguments> searchParams() {
        return Stream.of(
                Arguments.arguments(Map.of(EMAIL, "maximillian@nowhere.org"), null, null, new String[]{"01"}),
                Arguments.arguments(Map.of(LAST_NAME, "van der Schelde"), null, null, new String[]{"01"}),
                Arguments.arguments(Map.of(FIRST_NAME, "Maximillian"), null, null, new String[]{"01"}),
                Arguments.arguments(Map.of(FIRST_NAME, "Maximillian", LAST_NAME, "van der Schelde"), null, null, new String[]{"01"}),
                Arguments.arguments(Map.of(FIRST_NAME, "Maximillian", ENABLED, "true", LAST_NAME, "van der Schelde"), null, null, new String[]{"01"}),
                Arguments.arguments(Map.of(ENABLED, "true"), null, null, new String[]{"04", "05", "02", "01"})
        );
    }


}
