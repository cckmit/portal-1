package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.UserRoleDAO;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.UserRoleQuery;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.policy.PolicyService;

import java.util.List;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.toList;

/**
 * Реализация сервиса управления ролями
 */
public class UserRoleServiceImpl implements UserRoleService {

    private static Logger log = LoggerFactory.getLogger(UserRoleServiceImpl.class);

    @Autowired
    UserRoleDAO userRoleDAO;

    @Autowired
    PolicyService policyService;

    @Autowired
    AuthService authService;

    @Override
    public Result<List<UserRole>> userRoleList( AuthToken token, UserRoleQuery query ) {
        applyFilterByScope(token, query);
        List<UserRole> userRoles = userRoleDAO.listByQuery(query);

        if (userRoles == null)
            return error( En_ResultStatus.GET_DATA_ERROR);

        return ok(userRoles);
    }

    @Override
    public Result<UserRole> getUserRole( AuthToken authToken, Long id ) {

        UserRole person = userRoleDAO.get(id);

        return person != null ? Result.ok( person)
                : error( En_ResultStatus.NOT_FOUND);
    }


    @Override
    @Transactional
    public Result<UserRole> saveUserRole( AuthToken token, UserRole role ) {

        if (HelperFunc.isEmpty(role.getCode())) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        if ( userRoleDAO.saveOrUpdate(role)) {
            return ok(role);
        }

        return error(En_ResultStatus.INTERNAL_ERROR);
    }

    @Override
    @Transactional
    public Result<Long> removeRole(AuthToken authToken, Long id ) {
        if (!userRoleDAO.removeByKey(id)) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        return ok(id);
    }

    private void applyFilterByScope( AuthToken token, UserRoleQuery query ) {
        if ( !policyService.hasGrantAccessFor( token.getRoles(), En_Privilege.ROLE_VIEW ) ) {
            query.setRoleIds(toList(token.getRoles(), UserRole::getId));
        }
    }

}
