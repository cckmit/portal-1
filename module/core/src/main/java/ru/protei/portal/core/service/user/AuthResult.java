package ru.protei.portal.core.service.user;

/**
 * Created by michael on 29.06.16.
 */
public class AuthResult {

    En_AuthResult result;

    UserSessionDescriptor descriptor;


    public AuthResult(En_AuthResult result) {
        this.result = result;
    }

    public AuthResult(En_AuthResult result, UserSessionDescriptor descriptor) {
        this.result = result;
        this.descriptor = descriptor;
    }

    public En_AuthResult getResult() {
        return result;
    }

    public UserSessionDescriptor getDescriptor() {
        return descriptor;
    }

    public boolean isOk () {
        return this.result == En_AuthResult.OK;
    }
}
