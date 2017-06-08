package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.RoleQuery;

import java.util.List;

/**
 * Асинхронный сервис управления ролями
 */
public interface RoleServiceAsync {

    void getRoles( RoleQuery query, AsyncCallback< List< UserRole > > async );

    void getRole( long id, AsyncCallback< UserRole > async );

    void saveRole( UserRole role, AsyncCallback< UserRole > async );
}
