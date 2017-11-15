package ru.protei.portal.ui.role.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.UserRoleQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.service.UserRoleService;
import ru.protei.portal.ui.common.client.service.RoleService;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Реализация сервиса по работе с ролями
 */
@Service( "RoleService" )
public class RoleServiceImpl implements RoleService {

    @Override
    public List<UserRole> getRoles( UserRoleQuery query ) throws RequestFailedException {
        log.debug( "getRoles(): searchPattern={} ",
                query.getSearchString() );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<List<UserRole>> response = roleService.userRoleList( descriptor.makeAuthToken(), query );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public UserRole getRole(Long id) throws RequestFailedException {
        log.debug("get role, id: {}", id);

        //TODO используется для отображения карточки роли, думаю проверка роли ROLE_VIEW логична
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<UserRole> response = roleService.getUserRole( descriptor.makeAuthToken(), id );

        log.debug("get role, id: {} -> {} ", id, response.isError() ? "error" : response.getData());

        return response.getData();
    }

    @Override
    public UserRole saveRole(UserRole role) throws RequestFailedException {
        if (role == null) {
            log.warn("null person in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        log.debug("store role, id: {} ", HelperFunc.nvl(role.getId(), "new"));

        CoreResponse<UserRole> response = roleService.saveUserRole( descriptor.makeAuthToken(), role );

        log.debug("store role, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.debug("store role, applied id: {}", response.getData().getId());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public List< EntityOption > getRolesOptionList( UserRoleQuery query ) throws RequestFailedException {
        log.debug( "getRolesOptionList(): searchPattern={} ",
                query.getSearchString() );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<List<EntityOption>> response = roleService.userRoleOptionList( descriptor.makeAuthToken(), query );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public boolean removeRole( Long id ) throws RequestFailedException {
        log.debug( "removeRole(): id={}", id );

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse< Boolean > response = roleService.removeRole( descriptor.makeAuthToken(), id );
        log.debug( "removeRole(): result={}", response.isOk() ? "ok" : response.getStatus() );

        if (response.isOk()) {
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    private UserSessionDescriptor getDescriptorAndCheckSession() throws RequestFailedException {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor( httpServletRequest );
        log.info( "userSessionDescriptor={}", descriptor );
        if ( descriptor == null ) {
            throw new RequestFailedException( En_ResultStatus.SESSION_NOT_FOUND );
        }

        return descriptor;
    }

    @Autowired
    UserRoleService roleService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger( "web" );
}
