package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

/**
 * Пингер
 */
@RemoteServiceRelativePath( "springGwtServices/PingService" )
public interface PingService extends RemoteService {

    void ping() throws RequestFailedException;
}
