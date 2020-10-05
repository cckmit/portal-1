package ru.protei.portal.ui.project.client.util;

import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ProjectAccessType;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;

import java.util.List;
import java.util.Objects;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

public class AccessUtil {

    public static boolean canAccessProjectPrivateElements(PolicyService policyService, En_Privilege privilege, List<PersonProjectMemberView> team) {
        En_ProjectAccessType accessType = getAccessType(policyService, privilege);
        if (accessType == En_ProjectAccessType.NONE) {
            return false;
        }
        boolean isTeamMember = stream(team)
                .map(PersonShortView::getId)
                .anyMatch(id -> Objects.equals(id, policyService.getProfileId()));
        return isTeamMember;
    }

    public static boolean canAccessProject(PolicyService policyService, En_Privilege privilege, List<PersonProjectMemberView> team) {
        En_ProjectAccessType accessType = getAccessType(policyService, privilege);
        switch (accessType) {
            case ALL_PROJECTS: return true;
            case NONE: return false;
            case SELF_PROJECTS: {
                boolean isTeamMember = stream(team)
                        .map(PersonShortView::getId)
                        .anyMatch(id -> Objects.equals(id, policyService.getProfileId()));
                return isTeamMember;
            }
        }
        return false;
    }

    public static En_ProjectAccessType getAccessType(PolicyService policyService, En_Privilege privilege) {
        boolean hasSystemScope = policyService.hasScopeForPrivilege(privilege, En_Scope.SYSTEM);
        if (hasSystemScope) {
            return En_ProjectAccessType.ALL_PROJECTS;
        }
        boolean hasUserScope = policyService.hasScopeForPrivilege(privilege, En_Scope.USER);
        if (hasUserScope) {
            return En_ProjectAccessType.SELF_PROJECTS;
        }
        return En_ProjectAccessType.NONE;
    }
}
