package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.annotations.Stored;
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
    @Privileged( En_Privilege.ROLE_VIEW )
    CoreResponse<List<UserRole>> userRoleList( AuthToken authToken, UserRoleQuery query );

    @Privileged( En_Privilege.ROLE_VIEW )
    CoreResponse<UserRole> getUserRole( AuthToken authToken, Long id );

    @Privileged( requireAny = { En_Privilege.ROLE_EDIT, En_Privilege.ROLE_CREATE } )
    @Auditable( En_AuditType.ROLE_MODIFY )
    CoreResponse<UserRole> saveUserRole( AuthToken authToken, @Stored UserRole userRole );
}
