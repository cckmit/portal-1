package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.ui.common.shared.model.ClientConfigData;

/**
 * Сервис приложения
 */
@RemoteServiceRelativePath("springGwtServices/AppService")
public interface AppService extends RemoteService {

    /**
     * Получение клиентских параметров приложения
     */
    ClientConfigData getClientConfig();

    String getExternalLinksHtml();
}
