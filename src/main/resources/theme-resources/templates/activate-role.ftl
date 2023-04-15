<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('activationCode'); section>
    <#if section = "header">
        ${msg("activateRoleTitle")}
    <#elseif section = "form">
        <h2>${msg("activateRoleHello",(username!''))}</h2>
        <p>${msg("activateRoleText")}</p>
        <form id="kc-activation-code-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
            <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="activationCode"class="${properties.kcLabelClass!}">${msg("activateRoleFieldLabel")}</label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input type="text" id="activationCode" name="activationCode" class="${properties.kcInputClass!}"
                           value="${activationCode}" required aria-invalid="<#if messagesPerField.existsError('activationCode')>true</#if>"/>
                    <#if messagesPerField.existsError('activationCode')>
                        <span id="input-error-activation-code" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
										${kcSanitize(messagesPerField.get('activationCode'))?no_esc}
								</span>
                    </#if>
                </div>
            </div>
            <div class="${properties.kcFormGroupClass!}">
                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                    <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" type="submit" value="${msg("doSubmit")}"/>
                </div>
            </div>
        </form>
    </#if>
</@layout.registrationLayout>
