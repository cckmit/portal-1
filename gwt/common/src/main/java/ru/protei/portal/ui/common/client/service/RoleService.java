package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.RoleQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис управления ролями
 */
@RemoteServiceRelativePath( "springGwtServices/RoleService" )
public interface RoleService extends RemoteService {

    List< UserRole > getRoles( RoleQuery query ) throws RequestFailedException;

    UserRole getRole( long id ) throws RequestFailedException;

    UserRole saveRole( UserRole role ) throws RequestFailedException;
}
