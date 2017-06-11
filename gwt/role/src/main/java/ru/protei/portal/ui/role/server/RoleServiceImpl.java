package ru.protei.portal.ui.role.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.UserRoleQuery;
import ru.protei.portal.core.service.UserRoleService;
import ru.protei.portal.ui.common.client.service.RoleService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

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

        CoreResponse<List<UserRole>> response = roleService.userRoleList( query );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public UserRole getRole(Long id) throws RequestFailedException {
        log.debug("get role, id: {}", id);

        CoreResponse<UserRole> response = roleService.getUserRole(id);

        log.debug("get role, id: {} -> {} ", id, response.isError() ? "error" : response.getData());

        return response.getData();
    }

    @Override
    public UserRole saveRole(UserRole role) throws RequestFailedException {
        if (role == null) {
            log.warn("null person in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        log.debug("store role, id: {} ", HelperFunc.nvl(role.getId(), "new"));

        CoreResponse<UserRole> response = roleService.saveUserRole(role);

        log.debug("store role, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.debug("store role, applied id: {}", response.getData().getId());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Autowired
    UserRoleService roleService;

    private static final Logger log = LoggerFactory.getLogger( "web" );
}
