package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.query.UserRoleQuery;

import java.util.List;

/**
 * Created by michael on 16.06.16.
 */
public interface UserRoleDAO extends PortalBaseDAO<UserRole> {

    @SqlConditionBuilder
    SqlCondition createSqlCondition( UserRoleQuery query);
}
