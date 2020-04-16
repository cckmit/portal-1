package ru.protei.portal.ui.education.client.util;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;

public class EducationUtils {

    @Inject
    private static PolicyService policyService;

    public static boolean isAdmin() {
        return policyService.hasPrivilegeFor(En_Privilege.EDUCATION_EDIT);
    }

    public static boolean isWorkerCanRequest() {
        return isWorker() && policyService.hasPrivilegeFor(En_Privilege.EDUCATION_CREATE);
    }

    public static boolean isWorker() {
        return policyService.hasPrivilegeFor(En_Privilege.EDUCATION_VIEW);
    }
 }
