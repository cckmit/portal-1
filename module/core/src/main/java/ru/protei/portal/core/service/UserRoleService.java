package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.UserRoleQuery;

import java.util.List;

/**
 * Сервис управления ролями
 */
public interface UserRoleService {
    Result<List<UserRole>> userRoleList( AuthToken authToken, UserRoleQuery query );

    @Privileged( En_Privilege.ROLE_VIEW )
    Result<UserRole> getUserRole( AuthToken authToken, Long id );

    @Privileged( requireAny = { En_Privilege.ROLE_EDIT, En_Privilege.ROLE_CREATE } )
    @Auditable( En_AuditType.ROLE_MODIFY )
    Result<UserRole> saveUserRole( AuthToken authToken, UserRole userRole );

    @Privileged( En_Privilege.ROLE_REMOVE )
    @Auditable( En_AuditType.ROLE_REMOVE )
    Result<Long> removeRole( AuthToken authToken, Long id );
}
