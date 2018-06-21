package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.List;

/**
 * Сервис управления Статусами обращений
 */
public interface CaseStateControllerAsync {

    void getCaseStates(AsyncCallback<List<CaseState>> callback);
}
