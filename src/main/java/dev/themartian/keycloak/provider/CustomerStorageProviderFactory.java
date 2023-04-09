package dev.themartian.keycloak.provider;/*
My File Header
*/

import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.util.List;

public class CustomerStorageProviderFactory implements UserStorageProviderFactory<CustomerStorageProvider> {

    private static final List<ProviderConfigProperty> configMetadata;

    private static final String HOST = "host";

    private static final String PORT = "port";

    private static final String DB = "db";

    private static final String USERNAME = "username";

    private static final String PASSWORD = "password";

    static {
        configMetadata = ProviderConfigurationBuilder.create()
                .property().name(HOST)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("Database Hostname")
                .defaultValue("localhost")
                .helpText("Hostname of the PostgreSQL database server")
                .add()
                .property().name(PORT)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("Database Port")
                .defaultValue("5432")
                .helpText("Port of the PostgreSQL database server")
                .add()
                .property().name(DB)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("Database Name")
                .defaultValue("db")
                .helpText("Name of the PostgreSQL database")
                .add()
                .property().name(USERNAME)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("Username")
                .defaultValue("")
                .helpText("Username for the PostgreSQL connection")
                .add()
                .property().name(PASSWORD)
                .type(ProviderConfigProperty.PASSWORD)
                .label("Password")
                .defaultValue("")
                .helpText("Password for the PostgreSQL connection")
                .add()
                .build();
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configMetadata;
    }

    public String getId() {
        return "customer";
    }

    public CustomerStorageProvider create(KeycloakSession session, ComponentModel componentModel) {
        return new CustomerStorageProvider(session, componentModel, new ConnectionProperties(
                componentModel.getConfig().getFirst(HOST),
                Integer.valueOf(componentModel.getConfig().getFirst(PORT)),
                componentModel.getConfig().getFirst(DB),
                componentModel.getConfig().getFirst(USERNAME),
                componentModel.getConfig().getFirst(PASSWORD)));
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) throws ComponentValidationException {

    }
}
