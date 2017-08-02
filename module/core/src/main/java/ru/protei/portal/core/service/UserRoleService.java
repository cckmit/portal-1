package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.UserRoleQuery;

import java.util.List;
import java.util.Set;

/**
 * Сервис управления ролями
 */
public interface UserRoleService {
    @Privileged( En_Privilege.ROLE_VIEW )
    CoreResponse<List<UserRole>> userRoleList( AuthToken authToken, UserRoleQuery query );

    @Privileged( En_Privilege.ROLE_VIEW )
    CoreResponse<UserRole> getUserRole( AuthToken authToken, Long id );

    @Privileged( requireAny = { En_Privilege.ROLE_EDIT, En_Privilege.ROLE_CREATE } )
    CoreResponse<UserRole> saveUserRole( AuthToken authToken, UserRole userRole );
}
