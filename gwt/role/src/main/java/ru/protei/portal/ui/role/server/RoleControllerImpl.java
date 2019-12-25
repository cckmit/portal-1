package ru.protei.portal.ui.role.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.UserRoleQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.service.UserRoleService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.RoleController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Реализация сервиса по работе с ролями
 */
@Service( "RoleController" )
public class RoleControllerImpl implements RoleController {

    @Override
    public List<UserRole> getRoles( UserRoleQuery query ) throws RequestFailedException {
        log.info( "getRoles(): searchPattern={} ",
                query.getSearchString() );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<List<UserRole>> response = roleService.userRoleList( token, query );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public UserRole getRole(Long id) throws RequestFailedException {
        log.info("get role, id: {}", id);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<UserRole> response = roleService.getUserRole( token, id );

        log.info("get role, id: {} -> {} ", id, response.isError() ? "error" : response.getData());

        return response.getData();
    }

    @Override
    public UserRole saveRole(UserRole role) throws RequestFailedException {
        if (role == null) {
            log.warn("null person in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        log.info("store role, id: {} ", HelperFunc.nvl(role.getId(), "new"));

        Result<UserRole> response = roleService.saveUserRole( token, role );

        log.info("store role, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.info("store role, applied id: {}", response.getData().getId());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public List< EntityOption > getRolesOptionList( UserRoleQuery query ) throws RequestFailedException {
        log.info( "getRolesOptionList(): searchPattern={} ",
                query.getSearchString() );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<List<EntityOption>> response = roleService.userRoleOptionList( token, query );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public boolean removeRole( Long id ) throws RequestFailedException {
        log.info( "removeRole(): id={}", id );

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result< Boolean > response = roleService.removeRole( token, id );
        log.info( "removeRole(): result={}", response.isOk() ? "ok" : response.getStatus() );

        if (response.isOk()) {
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Autowired
    UserRoleService roleService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger(RoleControllerImpl.class);
}
