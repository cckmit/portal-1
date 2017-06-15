package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.UserRoleQuery;

import java.util.List;

/**
 * Сервис управления ролями
 */
public interface UserRoleService {
    CoreResponse<List<UserRole>> userRoleList( UserRoleQuery query );
    CoreResponse<UserRole> getUserRole( Long id );
    CoreResponse<UserRole> saveUserRole( UserRole userRole );
}
