package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.UserRoleDAO;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.query.UserRoleQuery;

import java.util.Arrays;
import java.util.HashSet;
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


    @Override
    public UserRole ensureExists( String code, En_Scope scope, En_Privilege... privileges) {
        UserRole role = getByCondition("role_code=?", code);
        if (role == null) {
            role = new UserRole();
            role.setCode(code);
            role.setInfo("auto-created");
            role.setPrivileges(new HashSet<>(Arrays.asList(privileges)));
            persist(role);
        }
        else {
            boolean changes = false;
            for (En_Privilege priv : privileges) {
                if (!role.hasPrivilege(priv)) {
                    changes = true;
                    role.addPrivilege(priv);
                }
            }

            if ( scope != null && !scope.equals( role.getScope() )) {
                changes = true;
                role.setScope( scope );
            }

            if (changes)
                merge(role);
        }

        return role;
    }
}
