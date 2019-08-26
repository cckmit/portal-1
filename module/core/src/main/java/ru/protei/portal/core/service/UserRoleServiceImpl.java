package ru.protei.portal.core.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.UserRoleDAO;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.UserRoleQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.portal.core.service.auth.AuthService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
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
        List<UserRole> list = userRoleDAO.listByQuery(query);

        if (list == null)
            error( En_ResultStatus.GET_DATA_ERROR);

        return ok(list);
    }

    @Override
    public Result<UserRole> getUserRole( AuthToken authToken, Long id ) {

        UserRole person = userRoleDAO.get(id);

        return person != null ? Result.ok( person)
                : error( En_ResultStatus.NOT_FOUND);
    }


    @Override
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
    public Result< List< EntityOption > > userRoleOptionList( AuthToken token, UserRoleQuery query ) {
        applyFilterByScope(token, query);
        List<UserRole> list = userRoleDAO.listByQuery(query);

        if (list == null)
            error( En_ResultStatus.GET_DATA_ERROR);

        List<EntityOption> result = list.stream().map( UserRole::toEntityOption).collect( Collectors.toList());
        return ok(result);
    }

    @Override
    public Result<Boolean> removeRole( AuthToken authToken, Long id ) {
        if ( userRoleDAO.removeByKey( id ) ) {
            return ok(true );
        }

        return error(En_ResultStatus.INTERNAL_ERROR );
    }

    private void applyFilterByScope( AuthToken token, UserRoleQuery query ) {
        UserSessionDescriptor descriptor = authService.findSession( token );
        if ( !policyService.hasGrantAccessFor( descriptor.getLogin().getRoles(), En_Privilege.ROLE_VIEW ) ) {
            query.setRoleIds(
                            Optional.ofNullable( descriptor.getLogin().getRoles())
                                    .orElse( Collections.emptySet() )
                                    .stream()
                                    .map( UserRole::getId )
                                    .collect( Collectors.toList()) );
        }
    }

}
