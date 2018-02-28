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
    public UserRole ensureExists( String code, En_Scope scope, En_Privilege... privileges) {
        UserRole role = getByCondition("role_code=?", code);
        if (role == null) {
            role = new UserRole();
            role.setCode(code);
            role.setInfo("auto-created");
            role.setPrivileges(new HashSet<>(Arrays.asList(privileges)));
            role.setScope( scope );
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

            if (role.getScope() != scope) {
                role.setScope(scope);
                changes = true;
            }

            if (changes)
                merge(role);
        }

        return role;
    }

    @Override
    public void trimScopeToSingleValue() {
        jdbcTemplate.batchUpdate( "UPDATE user_role ur SET ur.scopes = LEFT(scopes, (LOCATE(',', scopes) - 1)) WHERE scopes LIKE '%,%'" );
    }


    @Override
    public Set<UserRole> getDefaultEmployeeRoles() {
        UserRole employeeRole  = ensureExists(EMPLOYEE_ROLE_CODE, DEF_EMPLOYEE_SCOPE, DEF_EMPLOYEE_PRIV_SET);
        Set<UserRole> employeeRoleSet = new HashSet<>();
        employeeRoleSet.add(employeeRole);
        return employeeRoleSet;
    }

    @Override
    public Set<UserRole> getDefaultCustomerRoles() {
        UserRole crmClientRole = ensureExists(DEF_CLIENT_ROLE_CODE, DEF_COMPANY_CLIENT_SCOPE, DEF_COMPANY_CLIENT_PRIV);

        Set<UserRole> roles = new HashSet<>();
        roles.add(crmClientRole);
        return roles;
    }



    public static final String EMPLOYEE_ROLE_CODE = "Сотрудник";

    public final static En_Privilege[] DEF_EMPLOYEE_PRIV_SET = {
            En_Privilege.ISSUE_CREATE,
            En_Privilege.ISSUE_EDIT,
            En_Privilege.ISSUE_EXPORT,
            En_Privilege.ISSUE_VIEW,
            En_Privilege.ISSUE_REPORT,
            En_Privilege.DASHBOARD_VIEW,
            En_Privilege.CONTACT_VIEW,
            En_Privilege.COMMON_PROFILE_VIEW,
            En_Privilege.COMPANY_VIEW
    };

    public final static En_Scope DEF_EMPLOYEE_SCOPE = En_Scope.SYSTEM;


    private final static String DEF_CLIENT_ROLE_CODE="ТПиМ : Заказчик";

    private final static En_Privilege [] DEF_COMPANY_CLIENT_PRIV = {
            En_Privilege.ISSUE_CREATE,
            En_Privilege.ISSUE_EDIT,
            En_Privilege.ISSUE_VIEW,
            En_Privilege.ISSUE_EXPORT,
            En_Privilege.ISSUE_REPORT,
            En_Privilege.COMMON_PROFILE_EDIT,
            En_Privilege.COMMON_PROFILE_VIEW
    };

    private final static En_Scope DEF_COMPANY_CLIENT_SCOPE = En_Scope.COMPANY;
}
