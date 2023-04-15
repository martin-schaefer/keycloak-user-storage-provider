package dev.themartian.keycloak.action;/*
My File Header
*/

import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class ActivateRoleActionFactory implements RequiredActionFactory {

    @Override
    public RequiredActionProvider create(KeycloakSession keycloakSession) {
        return new ActivateRoleAction();
    }

    @Override
    public String getDisplayText() {
        return "UCL - Activate Role";
    }

    @Override
    public void init(Config.Scope scope) {
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return ActivateRoleAction.PROVIDER_ID;
    }
}
