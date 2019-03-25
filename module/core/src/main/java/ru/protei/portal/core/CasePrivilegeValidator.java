package ru.protei.portal.core;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.exception.InsufficientPrivilegesException;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.service.PolicyService;
import ru.protei.portal.core.service.user.AuthService;

/**
 * Ручной валидатор привилегий case-объектов, если данная задача не положена на аспект и аннотации
 */
public class CasePrivilegeValidator {

    public void checkPrivilegesRead(AuthToken token, En_CaseType caseType) throws InsufficientPrivilegesException {
        if (caseType == null) {
            throw new InsufficientPrivilegesException("Provided En_CaseType is null");
        }
        switch (caseType) {
            case CRM_SUPPORT: checkRequireAnyPrivileges(token, En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT); break;
            case OFFICIAL: checkRequireAnyPrivileges(token, En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT); break;
            case PROJECT: checkRequireAnyPrivileges(token, En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT); break;
            case EMPLOYEE_REGISTRATION: checkRequireAnyPrivileges(token, En_Privilege.EMPLOYEE_REGISTRATION_VIEW); break;
            case SF_PLATFORM: checkRequireAnyPrivileges(token, En_Privilege.SITE_FOLDER_VIEW, En_Privilege.SITE_FOLDER_EDIT); break;
            default: throw new InsufficientPrivilegesException("Provided En_CaseType='" + caseType + "' not matched supported types");
        }
    }

    public void checkPrivilegesModify(AuthToken token, En_CaseType caseType) throws InsufficientPrivilegesException {
        if (caseType == null) {
            throw new InsufficientPrivilegesException("Provided En_CaseType is null");
        }
        switch (caseType) {
            case CRM_SUPPORT: checkRequireAllPrivileges(token, En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT); break;
            case OFFICIAL: checkRequireAllPrivileges(token, En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT); break;
            case PROJECT: checkRequireAllPrivileges(token, En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT); break;
            case EMPLOYEE_REGISTRATION: checkRequireAllPrivileges(token, En_Privilege.EMPLOYEE_REGISTRATION_VIEW); break;
            case SF_PLATFORM: checkRequireAllPrivileges(token, En_Privilege.SITE_FOLDER_VIEW, En_Privilege.SITE_FOLDER_EDIT); break;
            default: throw new InsufficientPrivilegesException("Provided En_CaseType='" + caseType + "' not matched supported types");
        }
    }

    private void checkRequireAllPrivileges(AuthToken token, En_Privilege... privileges) {
        if (token == null) {
            return;
        }
        UserSessionDescriptor descriptor = authService.findSession(token);
        if (!policyService.hasEveryPrivilegeOf(descriptor.getLogin().getRoles(), privileges)) {
            throw new InsufficientPrivilegesException();
        }
    }

    private void checkRequireAnyPrivileges(AuthToken token, En_Privilege... privileges) {
        if (token == null) {
            return;
        }
        UserSessionDescriptor descriptor = authService.findSession(token);
        if (!policyService.hasAnyPrivilegeOf(descriptor.getLogin().getRoles(), privileges)) {
            throw new InsufficientPrivilegesException();
        }
    }

    @Autowired
    AuthService authService;
    @Autowired
    PolicyService policyService;
}
