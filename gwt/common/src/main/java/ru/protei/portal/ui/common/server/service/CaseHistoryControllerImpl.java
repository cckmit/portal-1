package ru.protei.portal.ui.common.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.History;
import ru.protei.portal.core.service.HistoryService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.CaseHistoryController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service("CaseHistoryController")
public class CaseHistoryControllerImpl implements CaseHistoryController {
    @Override
    public List<History> getHistoryListByCaseId(Long caseId) throws RequestFailedException {
        log.info("CaseHistoryControllerImpl#getHistoryListByCaseId : caseId={}", caseId);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<List<History>> historyListByCaseIdResult = historyService.getHistoryListByCaseId(token, caseId);

        if (historyListByCaseIdResult.isError()) {
            throw new RequestFailedException(historyListByCaseIdResult.getStatus());
        }

        return historyListByCaseIdResult.getData();
    }

    @Autowired
    private HistoryService historyService;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger(CaseHistoryControllerImpl.class);
}
