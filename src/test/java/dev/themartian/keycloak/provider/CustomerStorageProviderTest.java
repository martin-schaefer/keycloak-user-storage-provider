package dev.themartian.keycloak.provider;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class CustomerStorageProviderTest {

    @Container
    private static final JdbcDatabaseContainer CONTAINER = new PostgreSQLContainer().withInitScript("init.sql");

    /**
     * Method under test: {@link CustomerStorageProvider#getUserById(RealmModel, String)}
     */
    @Test
    @DisplayName("Get customer by id")
    public void getUserById() {

        // given
        DefaultKeycloakSession session = new DefaultKeycloakSession(new DefaultKeycloakSessionFactory());
        ComponentModel storageProviderModel = Mockito.mock(ComponentModel.class);
        RealmModel realmModel = Mockito.mock(RealmModel.class);
        ConnectionProperties connectionProperties = new ConnectionProperties(CONTAINER.getHost(), CONTAINER.getFirstMappedPort(), CONTAINER.getDatabaseName(), CONTAINER.getUsername(), CONTAINER.getPassword());
        CustomerStorageProvider customerStorageProvider = new CustomerStorageProvider(session, storageProviderModel, connectionProperties);

        // when
        UserModel userModel = customerStorageProvider.getUserById(realmModel, "02");

        // then
        assertThat(userModel.getId()).isEqualTo("02");
        assertThat(userModel.getEmail()).isEqualTo("max2@nowhere.org");
        assertThat(userModel.isEmailVerified()).isTrue();
        assertThat(userModel.getFirstName()).isEqualTo("Max2");
        assertThat(userModel.getLastName()).isEqualTo("Müller2");
        assertThat(userModel.isEnabled()).isTrue();

    }
}
