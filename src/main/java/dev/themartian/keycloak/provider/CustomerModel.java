package dev.themartian.keycloak.provider;

import lombok.Getter;
import lombok.NonNull;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.SubjectCredentialManager;
import org.keycloak.models.UserModel;
import org.keycloak.storage.adapter.AbstractUserAdapter;
import org.keycloak.common.util.MultivaluedHashMap;

import java.util.List;
import java.util.Map;

@Getter
public class CustomerModel extends AbstractUserAdapter {

    public static final String ID = "id";
    public static final String USERNAME = UserModel.USERNAME;
    public static final String EMAIL = UserModel.EMAIL;
    public static final String FIRST_NAME = UserModel.FIRST_NAME;
    public static final String LAST_NAME = UserModel.LAST_NAME;
    public static final String ENABLED = "enabled";
    public static final String EMAIL_VERIFIED = "emailVerified";

    private final String id;
    private final String email;
    private final boolean emailVerified;
    private final String firstName;
    private final String lastName;
    private final boolean enabled;

    private CustomerModel(@NonNull KeycloakSession session,
                          @NonNull RealmModel realm,
                          @NonNull ComponentModel storageProviderModel,
                          @NonNull String id,
                          @NonNull String email,
                          boolean emailVerified,
                          String firstName,
                          String lastName,
                          boolean enabled) {
        super(session, realm, storageProviderModel);
        this.id = id;
        this.email = email;
        this.emailVerified = emailVerified;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = enabled;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        MultivaluedHashMap<String, String> attributes = new MultivaluedHashMap<>();
        attributes.add(USERNAME, getUsername());
        attributes.add(EMAIL, getEmail());
        attributes.add(EMAIL_VERIFIED, String.valueOf(isEmailVerified()));
        attributes.add(FIRST_NAME, getFirstName());
        attributes.add(LAST_NAME, getLastName());
        attributes.add(ENABLED, String.valueOf(isEnabled()));
        return attributes;
    }

    @Override
    public SubjectCredentialManager credentialManager() {
        // todo
        return null;
    }

    static class Builder {
        private final KeycloakSession session;
        private final RealmModel realm;
        private final ComponentModel storageProviderModel;
        private String id;
        private String email;
        private boolean emailVerified;
        private String firstName;
        private String lastName;
        private boolean enabled;

        Builder(@NonNull KeycloakSession session, @NonNull RealmModel realm, @NonNull ComponentModel storageProviderModel) {
            this.session = session;
            this.realm = realm;
            this.storageProviderModel = storageProviderModel;
        }

        CustomerModel.Builder id(@NonNull String id) {
            this.id = id;
            return this;
        }

        CustomerModel.Builder email(@NonNull String email) {
            this.email = email;
            return this;
        }

        CustomerModel.Builder emailVerified(boolean emailVerified) {
            this.emailVerified = emailVerified;
            return this;
        }

        CustomerModel.Builder firstName(@NonNull String firstName) {
            this.firstName = firstName;
            return this;
        }

        CustomerModel.Builder lastName(@NonNull String lastName) {
            this.lastName = lastName;
            return this;
        }

        CustomerModel.Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        CustomerModel build() {
            return new CustomerModel(
                    session,
                    realm,
                    storageProviderModel,
                    id,
                    email,
                    emailVerified,
                    firstName,
                    lastName,
                    enabled);
        }
    }
}
