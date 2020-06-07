package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseState;

import java.util.List;

/**
 * Сервис управления Статусами обращений
 */
public interface CaseStateControllerAsync {

    void getCaseStates(En_CaseType type, AsyncCallback<List<CaseState>> callback);

    void getCaseStatesOmitPrivileges(En_CaseType type, AsyncCallback<List<CaseState>> callback);

    void getCaseState(Long id, AsyncCallback<CaseState> callback);

    void getCaseStateWithoutCompaniesOmitPrivileges(Long id, AsyncCallback<CaseState> callback);

    void saveCaseState(CaseState state, AsyncCallback<CaseState> callback);
}
