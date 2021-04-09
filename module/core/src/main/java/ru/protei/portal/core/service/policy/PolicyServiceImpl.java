package ru.protei.portal.core.service.policy;

import org.apache.commons.collections4.CollectionUtils;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.UserRole;

import java.util.*;

/**
 * Сервис для работы с привилегиями
 */
public class PolicyServiceImpl implements PolicyService {

    @Override
    public boolean hasAccessForCaseObject( AuthToken token, En_Privilege privilege, CaseObject caseObject ) {
        Set<UserRole> roles = token.getRoles();
        if (!hasGrantAccessFor( roles, privilege ) && hasScopeForPrivilege( roles, privilege, En_Scope.COMPANY )) {
            if (caseObject == null) {
                return false;
            }

            Collection<Long> companyIds = token.getCompanyAndChildIds();
            if (!companyIds.contains( caseObject.getInitiatorCompanyId() ) && !companyIds.contains( caseObject.getManagerCompanyId() )) {
                return false;
            }

            if (caseObject.isPrivateCase()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean hasPrivilegeFor( En_Privilege privilege, Set< UserRole > roles ) {
        if ( roles == null ) {
            return false;
        }
        for ( UserRole role : roles ) {
            if ( role.hasPrivilege( privilege ) ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasEveryPrivilegeOf( Set< UserRole > roles, En_Privilege... privileges ) {
        if ( roles == null ) {
            return false;
        }
        for ( En_Privilege privilege : privileges ) {
            if ( !getAllPrivileges(roles).contains( privilege ) ) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean hasAnyPrivilegeOf( Set< UserRole > roles, En_Privilege... privileges ) {
        if ( roles == null ) {
            return false;
        }
        for ( En_Privilege privilege : privileges ) {
            if ( getAllPrivileges(roles).contains( privilege ) ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasGrantAccessFor( Set< UserRole > roles, En_Privilege privilege ) {
        if ( privilege == null ) {
            return false;
        }

        Set<En_Scope> privilegeScopes = collectPrivilegeToScopeMap( roles ).get( privilege );
        if ( CollectionUtils.isEmpty( privilegeScopes ) ) {
            return false;
        }

        // В случае, если для привилегии установлена системная область видимости – предоставляем доступ без ограничений
        // Иначе если назначен scope в зависимости от entity или нет scope вообще – считаем что область видимости ограничена.
        return privilegeScopes.contains( En_Scope.SYSTEM );
    }

    @Override
    public boolean hasSystemScope(Set< UserRole > roles) {
        if (roles == null) {
            return false;
        }
        return roles.stream().map(UserRole::getScope).anyMatch(scope -> En_Scope.SYSTEM.equals(scope));
    }

    @Override
    public boolean hasScopeForPrivilege( Set< UserRole > roles, En_Privilege privilege, En_Scope scope ) {
        Map< En_Privilege, Set< En_Scope > > privilegeToScope = collectPrivilegeToScopeMap( roles );
        Set< En_Scope > privilegeScopes = privilegeToScope.get( privilege );

        return !CollectionUtils.isEmpty( privilegeScopes ) && privilegeScopes.contains( scope );
    }

    private Set< En_Privilege > getAllPrivileges( Set< UserRole > roles ) {
        Set< En_Privilege > privileges = new HashSet<>();
        for ( UserRole role : roles ) {
            if ( role.getPrivileges() == null ) {
                continue;
            }

            privileges.addAll( role.getPrivileges() );
        }
        return privileges;
    }

    private Map<En_Privilege, Set<En_Scope>> collectPrivilegeToScopeMap( Set<UserRole> roles ) {
        if ( roles == null ) {
            return Collections.emptyMap();
        }

        Map<En_Privilege, Set<En_Scope>> privilegeToScope = new HashMap<>();
        for ( UserRole role : roles ) {
            if ( role.getPrivileges() == null || role.getScope() == null ) {
                continue;
            }

            for ( En_Privilege privilege : role.getPrivileges() ) {
                Set<En_Scope> scopes = privilegeToScope.computeIfAbsent(privilege, k -> new HashSet<>());
                scopes.add( role.getScope() );
            }
        }

        return privilegeToScope;
    }
}
