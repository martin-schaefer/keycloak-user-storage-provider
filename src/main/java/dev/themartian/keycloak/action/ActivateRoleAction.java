package dev.themartian.keycloak.action;

import org.keycloak.authentication.InitiatedActionSupport;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.services.validation.Validation;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.function.Consumer;

public class ActivateRoleAction implements RequiredActionProvider {

    public static final String PROVIDER_ID = "ucl-activate-role";

    private static final String ACTIVATION_CODE_FIELD = "activationCode";

    @Override
    public InitiatedActionSupport initiatedActionSupport() {
        return InitiatedActionSupport.SUPPORTED;
    }

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        if (context.getUser().getFirstAttribute(ACTIVATION_CODE_FIELD) == null) {
            context.getUser().addRequiredAction(PROVIDER_ID);
        }
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        // show initial form
        context.challenge(createForm(context, null));
    }

    @Override
    public void processAction(RequiredActionContext context) {
        // submitted form

        UserModel user = context.getUser();

        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String activationCode = formData.getFirst(ACTIVATION_CODE_FIELD);

        if (Validation.isBlank(activationCode) || activationCode.length() < 5) {
            context.challenge(createForm(context, form -> form.addError(new FormMessage(ACTIVATION_CODE_FIELD, "Invalid input"))));
            return;
        }

        user.setSingleAttribute(ACTIVATION_CODE_FIELD, activationCode);
        user.removeRequiredAction(PROVIDER_ID);

        context.success();
    }

    @Override
    public void close() {
    }

    private Response createForm(RequiredActionContext context, Consumer<LoginFormsProvider> formConsumer) {
        LoginFormsProvider form = context.form();
        form.setAttribute("username", context.getUser().getUsername());

        String activationCode = context.getUser().getFirstAttribute(ACTIVATION_CODE_FIELD);
        form.setAttribute(ACTIVATION_CODE_FIELD, activationCode == null ? "" : activationCode);

        if (formConsumer != null) {
            formConsumer.accept(form);
        }

        return form.createForm("activate-role.ftl");
    }

}
