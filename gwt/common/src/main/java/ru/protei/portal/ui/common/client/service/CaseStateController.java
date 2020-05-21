package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Сервис управления Статусами обращений
 */
@RemoteServiceRelativePath("springGwtServices/CaseStateController")
public interface CaseStateController extends RemoteService {

    List<CaseState> getCaseStates(En_CaseType type) throws RequestFailedException;

    List<CaseState> getCaseStatesOmitPrivileges() throws RequestFailedException;

    CaseState getCaseState(Long id) throws RequestFailedException;

    CaseState saveCaseState(CaseState state) throws RequestFailedException;
}
