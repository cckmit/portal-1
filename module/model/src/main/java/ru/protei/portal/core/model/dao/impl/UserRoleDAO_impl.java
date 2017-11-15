package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.UserRoleDAO;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.query.UserRoleQuery;
import ru.protei.winter.core.utils.collections.CollectionUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

            if ( CollectionUtils.isNotEmpty( query.getRoleIds() )) {
                condition.append(" and id IN ( ");
                condition.append( query.getRoleIds().stream()
                                .map( String::valueOf )
                                .collect( Collectors.joining(",")));
                condition.append( " )");
            }
        });
    }


    @Override
    public UserRole ensureExists( String code, En_Scope[] scopes, En_Privilege... privileges) {
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

            for (En_Scope scope : scopes) {
                if (!role.hasScope(scope)) {
                    changes = true;
                    role.addScope(scope);
                }
            }

            if (changes)
                merge(role);
        }

        return role;
    }
}
