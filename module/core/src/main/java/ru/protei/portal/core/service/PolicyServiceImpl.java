package ru.protei.portal.core.service;

import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.UserRole;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Сервис для работы с привилегиями
 */
public class PolicyServiceImpl implements PolicyService {

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
    public boolean hasScopeFor(Set<UserRole> roles, En_Scope scope) {
        return getAllScopes( roles ).contains( scope );
    }

    @Override
    public boolean isGrantAccess(Set<UserRole> roles) {
        return getAllScopes(roles).contains( En_Scope.SYSTEM );
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

    private Set< En_Scope > getAllScopes( Set<UserRole> roles ) {
        return Optional.ofNullable( roles )
                .orElse( Collections.emptySet() )
                .stream()
                .flatMap( role -> role.getScopes().stream() )
                .collect( toSet() );
    }
}
