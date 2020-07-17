package ru.protei.portal.ui.education.client.util;

import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;

public class AccessUtil {

    public static boolean hasAccess(PolicyService policyService) {
        boolean isWorker = isWorker(policyService);
        boolean isAdmin = isAdmin(policyService);
        return isWorker || isAdmin;
    }

    public static boolean isWorkerCanRequest(PolicyService policyService) {
        return policyService.hasPrivilegeFor(En_Privilege.EDUCATION_CREATE);
    }

    public static boolean isWorker(PolicyService policyService) {
        return policyService.hasPrivilegeFor(En_Privilege.EDUCATION_VIEW);
    }

    public static boolean isAdmin(PolicyService policyService) {
        return policyService.hasSystemScopeForPrivilege(En_Privilege.EDUCATION_VIEW);
    }
}
