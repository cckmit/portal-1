package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.UserRoleQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;

/**
 * Асинхронный сервис управления ролями
 */
public interface RoleControllerAsync {

    void getRoles( UserRoleQuery query, AsyncCallback< List< UserRole > > async );

    void getRole( Long id, AsyncCallback< UserRole > async );

    void saveRole( UserRole role, AsyncCallback< UserRole > async );

    void removeRole( Long id, AsyncCallback<Long> async );
}
