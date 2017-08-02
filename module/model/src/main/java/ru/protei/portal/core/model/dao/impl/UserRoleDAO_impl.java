package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.UserRoleDAO;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.query.UserRoleQuery;

import java.util.List;

/**
 * Created by michael on 16.06.16.
 */
public class UserRoleDAO_impl extends PortalBaseJdbcDAO<UserRole> implements UserRoleDAO {

    @SqlConditionBuilder
    public SqlCondition createSqlCondition( UserRoleQuery query ) {
        return new SqlCondition().build((condition, args) -> {
            condition.append( "1=1" );

            if (HelperFunc.isLikeRequired(query.getSearchString())) {
                condition.append(" and role_code like ?");
                args.add(HelperFunc.makeLikeArg(query.getSearchString(), true));
            }
        });
    }
}
