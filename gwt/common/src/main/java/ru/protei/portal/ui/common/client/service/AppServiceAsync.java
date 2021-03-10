package ru.protei.portal.ui.common.client.service;


import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.ui.common.shared.model.ClientConfigData;

/**
 * Сервис приложения
 */
public interface AppServiceAsync {
    /**
     * Получение параметров приложения
     */
    void getClientConfig(AsyncCallback<ClientConfigData> sessionCallback);

    void getExternalLinksHtml(AsyncCallback<String> sessionCallback);

    void getAddingIssueCommentHelpText(String localeName, AsyncCallback<String> async);
}
