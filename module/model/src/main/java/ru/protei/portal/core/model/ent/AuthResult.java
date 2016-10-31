package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_ResultStatus;

/**
 * Created by michael on 29.06.16.
 */
public class AuthResult {

    En_ResultStatus result;

    UserSessionDescriptor descriptor;


    public AuthResult(En_ResultStatus result) {
        this.result = result;
    }

    public AuthResult(En_ResultStatus result, UserSessionDescriptor descriptor) {
        this.result = result;
        this.descriptor = descriptor;
    }

    public En_ResultStatus getResult() {
        return result;
    }

    public UserSessionDescriptor getDescriptor() {
        return descriptor;
    }

    public boolean isOk () {
        return this.result == En_ResultStatus.OK;
    }
}
