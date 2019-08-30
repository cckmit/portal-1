package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.query.UserRoleQuery;

import java.util.List;
import java.util.Set;

/**
 * Created by michael on 16.06.16.
 */
public interface UserRoleDAO extends PortalBaseDAO<UserRole> {

    @SqlConditionBuilder
    SqlCondition createSqlCondition( UserRoleQuery query);

    List<UserRole> getDefaultContactRoles();

    UserRole ensureExists ( String code, En_Scope scope, En_Privilege...privileges);

    Set<UserRole> getDefaultEmployeeRoles ();
    Set<UserRole> getDefaultManagerRoles ();
    Set<UserRole> getDefaultCustomerRoles ();

    void trimScopeToSingleValue();

    int removeByRoleCodeLike(String code);

    UserRole getByRoleCodeLike(String code);
}
