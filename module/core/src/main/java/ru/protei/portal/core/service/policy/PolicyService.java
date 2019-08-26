package ru.protei.portal.core.service.policy;

import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;

import java.util.Set;

/**
 * Абстракция сервиса для работы с привилегиями
 */
public interface PolicyService {

    boolean hasPrivilegeFor( En_Privilege privilege, Set< UserRole > roles );

    boolean hasEveryPrivilegeOf( Set< UserRole > roles, En_Privilege... privileges );

    boolean hasAccessForCaseObject( UserSessionDescriptor descriptor, En_Privilege privilege, CaseObject caseObject );

    boolean hasAnyPrivilegeOf( Set< UserRole > roles, En_Privilege... privileges );

    boolean hasGrantAccessFor( Set< UserRole > roles, En_Privilege privilege );

    boolean hasScopeForPrivilege( Set<UserRole> roles, En_Privilege privilege, En_Scope scope );
}
