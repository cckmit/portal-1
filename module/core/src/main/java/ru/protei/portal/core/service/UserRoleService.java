package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.UserRoleQuery;

import java.util.List;
import java.util.Set;

/**
 * Сервис управления ролями
 */
public interface UserRoleService {
    CoreResponse<List<UserRole>> userRoleList( UserRoleQuery query, Set< UserRole > roles );
    CoreResponse<UserRole> getUserRole( Long id, Set< UserRole > roles );
    CoreResponse<UserRole> saveUserRole( UserRole userRole, Set< UserRole > roles );
}
