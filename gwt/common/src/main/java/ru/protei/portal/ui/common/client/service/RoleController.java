package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.UserRoleQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис управления ролями
 */
@RemoteServiceRelativePath( "springGwtServices/RoleController" )
public interface RoleController extends RemoteService {

    List< UserRole > getRoles( UserRoleQuery query ) throws RequestFailedException;

    UserRole getRole( Long id ) throws RequestFailedException;

    UserRole saveRole( UserRole role ) throws RequestFailedException;

    List< EntityOption > getRolesOptionList( UserRoleQuery query ) throws RequestFailedException;

    boolean removeRole( Long id ) throws RequestFailedException;
}
