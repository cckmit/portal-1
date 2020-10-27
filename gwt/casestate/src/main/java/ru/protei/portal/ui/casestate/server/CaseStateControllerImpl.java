package ru.protei.portal.ui.casestate.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.service.CaseStateService;
import ru.protei.portal.ui.common.client.service.CaseStateController;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;
import static ru.protei.portal.ui.common.server.ServiceUtils.getAuthToken;

/**
 * Реализация сервиса по работе с Статусами обращений
 */
@Service("CaseStateController")
public class CaseStateControllerImpl implements CaseStateController {

    private static final Logger log = LoggerFactory.getLogger(CaseStateControllerImpl.class);

    @Autowired
    private SessionService sessionService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Inject
    private CaseStateService caseStateService;

    @Override
    public List<CaseState> getCaseStates(En_CaseType type) throws RequestFailedException {
        AuthToken authToken = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(caseStateService.getCaseStates(authToken,type));
    }

    @Override
    public List<CaseState> getCaseStatesOmitPrivileges(En_CaseType type) throws RequestFailedException {
        return checkResultAndGetData(caseStateService.getCaseStatesOmitPrivileges(type));
    }

    @Override
    public CaseState getCaseState(Long id) throws RequestFailedException {
        AuthToken authToken = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(caseStateService.getCaseState(authToken, id));
    }

    @Override
    public CaseState getCaseStateWithoutCompaniesOmitPrivileges(Long id) throws RequestFailedException {
        return checkResultAndGetData(caseStateService.getCaseStateWithoutCompaniesOmitPrivileges(id));
    }

    @Override
    public CaseState saveCaseState(CaseState state) throws RequestFailedException {
        AuthToken authToken = getAuthToken(sessionService, httpServletRequest);
        if (state.getId() == null) {
            state = checkResultAndGetData(caseStateService.createCaseState(authToken, state));
        } else {
            state = checkResultAndGetData(caseStateService.updateCaseState(authToken, state));
        }
        return state;
    }
}
