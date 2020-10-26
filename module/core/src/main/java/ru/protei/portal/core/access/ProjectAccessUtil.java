package ru.protei.portal.core.access;

import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ProjectAccessType;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.policy.PolicyService;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

public class ProjectAccessUtil {

    public static boolean canAccessProjectPrivateElements(PolicyService policyService, AuthToken token, En_Privilege privilege, List<PersonProjectMemberView> team) {
        En_ProjectAccessType accessType = getProjectAccessType(policyService, token, privilege);
        if (accessType == En_ProjectAccessType.NONE) {
            return false;
        }
        boolean isTeamMember = stream(team)
                .map(PersonShortView::getId)
                .anyMatch(id -> Objects.equals(id, token.getPersonId()));
        return isTeamMember;
    }

    public static boolean canAccessProject(PolicyService policyService, AuthToken token, En_Privilege privilege, List<PersonProjectMemberView> team) {
        En_ProjectAccessType accessType = getProjectAccessType(policyService, token, privilege);
        switch (accessType) {
            case ALL_PROJECTS: return true;
            case NONE: return false;
            case SELF_PROJECTS: {
                boolean isTeamMember = stream(team)
                        .map(PersonShortView::getId)
                        .anyMatch(id -> Objects.equals(id, token.getPersonId()));
                return isTeamMember;
            }
        }
        return false;
    }

    public static En_ProjectAccessType getProjectAccessType(PolicyService policyService, AuthToken token, En_Privilege privilege) {
        Set<UserRole> roles = token.getRoles();
        boolean hasSystemScope = policyService.hasScopeForPrivilege(roles, privilege, En_Scope.SYSTEM);
        if (hasSystemScope) {
            return En_ProjectAccessType.ALL_PROJECTS;
        }
        boolean hasUserScope = policyService.hasScopeForPrivilege(roles, privilege, En_Scope.USER);
        if (hasUserScope) {
            return En_ProjectAccessType.SELF_PROJECTS;
        }
        return En_ProjectAccessType.NONE;
    }
}
