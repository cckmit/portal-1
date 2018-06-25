package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.CaseState;

import java.util.List;

/**
 * Сервис управления Статусами обращений
 */
public interface CaseStateControllerAsync {

    void getCaseStates(AsyncCallback<List<CaseState>> callback);

    void getCaseState(Long id, AsyncCallback<CaseState> callback);

    void saveCaseState(CaseState state, AsyncCallback<CaseState> callback);

    void getCaseStatesOmitPrivileges(AsyncCallback<List<CaseState>> callback);
}
