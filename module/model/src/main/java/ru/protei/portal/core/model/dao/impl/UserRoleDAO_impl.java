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
    public List<UserRole> getDefaultContactRoles() {
        return getListByCondition("is_default_for_contact=true");
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


    private Set<UserRole> _getUserRoleSet (DefRoleSetup setup) {
        Set<UserRole> result = new HashSet<>();
        result.add(ensureExists(setup.codeName, setup.scope, setup.privSet));
        return result;

    }

    @Override
    public Set<UserRole> getDefaultEmployeeRoles() {
        return _getUserRoleSet(ROLE_SETUP_EMPLOYEE);
    }

    @Override
    public Set<UserRole> getDefaultCustomerRoles() {
        return _getUserRoleSet(ROLE_SETUP_CUSTOMER);
    }

    @Override
    public Set<UserRole> getDefaultManagerRoles() {
        return _getUserRoleSet(ROLE_SETUP_EMPL_MANAGER);
    }

    @Override
    public int removeByRoleCodeLike(String code) {
        return removeByCondition("role_code like ?", "%" + code + "%");
    }

    @Override
    public UserRole getByRoleCodeLike(String code) {
        return getByCondition("role_code like ?", "%" + code + "%");
    }


    static final DefRoleSetup ROLE_SETUP_EMPLOYEE = new DefRoleSetup("Сотрудник", En_Scope.USER,
            En_Privilege.COMMON_PROFILE_VIEW,
            En_Privilege.EMPLOYEE_VIEW,
            En_Privilege.DOCUMENT_VIEW,
            En_Privilege.ROOM_RESERVATION_VIEW,
            En_Privilege.ROOM_RESERVATION_CREATE,
            En_Privilege.ROOM_RESERVATION_EDIT,
            En_Privilege.ROOM_RESERVATION_REMOVE,
            En_Privilege.RESERVED_IP_VIEW,
            En_Privilege.RESERVED_IP_CREATE,
            En_Privilege.RESERVED_IP_EDIT,
            En_Privilege.RESERVED_IP_REMOVE,
            En_Privilege.SUBNET_VIEW
    );

    static final DefRoleSetup ROLE_SETUP_EMPL_MANAGER = new DefRoleSetup("ТПиМ : Менеджер", En_Scope.SYSTEM,
            En_Privilege.ISSUE_VIEW,
            En_Privilege.ISSUE_CREATE,
            En_Privilege.ISSUE_EDIT,
            En_Privilege.ISSUE_EXPORT,
            En_Privilege.ISSUE_REPORT,
            En_Privilege.COMPANY_VIEW,
            En_Privilege.COMPANY_CREATE,
            En_Privilege.COMPANY_EDIT,
            En_Privilege.CONTACT_VIEW,
            En_Privilege.CONTACT_CREATE,
            En_Privilege.CONTACT_EDIT,
            En_Privilege.PRODUCT_VIEW,
            En_Privilege.PRODUCT_CREATE,
            En_Privilege.PRODUCT_EDIT,
            En_Privilege.DASHBOARD_VIEW
            );

    static final DefRoleSetup ROLE_SETUP_CUSTOMER = new DefRoleSetup("ТПиМ : Заказчик", En_Scope.COMPANY,
            En_Privilege.ISSUE_CREATE,
            En_Privilege.ISSUE_EDIT,
            En_Privilege.ISSUE_VIEW,
            En_Privilege.ISSUE_EXPORT,
            En_Privilege.ISSUE_REPORT,
            En_Privilege.COMMON_PROFILE_EDIT,
            En_Privilege.COMMON_PROFILE_VIEW
    );

    static class DefRoleSetup {
        final String codeName;
        final En_Scope scope;
        final En_Privilege[] privSet;

        public DefRoleSetup(String codeName, En_Scope scope, En_Privilege... privSet) {
            this.codeName = codeName;
            this.privSet = privSet;
            this.scope = scope;
        }
    }
}
