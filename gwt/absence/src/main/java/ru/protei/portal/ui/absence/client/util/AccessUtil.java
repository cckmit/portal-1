package ru.protei.portal.ui.absence.client.util;

import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;

import java.util.Objects;

public class AccessUtil {

    public static boolean isAllowedEdit(PolicyService policyService, PersonAbsence absence) {
        return hasAccessAbsence(policyService, En_Privilege.ABSENCE_EDIT, absence);
    }

    public static boolean isAllowedRemove(PolicyService policyService, PersonAbsence absence) {
        return hasAccessAbsence(policyService, En_Privilege.ABSENCE_REMOVE, absence);
    }

    public static boolean hasAccessAbsence(PolicyService policyService, En_Privilege privilege, PersonAbsence absence) {
        Long currentPersonId = policyService.getProfile().getId();
        boolean isCreator = Objects.equals(absence.getCreatorId(), currentPersonId);
        boolean isAbsent = Objects.equals(absence.getPersonId(), currentPersonId);
        boolean isAdmin = policyService.hasSystemScopeForPrivilege(privilege);
        boolean isPrivileged = policyService.hasPrivilegeFor(privilege);
        boolean isUserWithAccess = isPrivileged && (isCreator || isAbsent);
        return isAdmin || isUserWithAccess;
    }
}
