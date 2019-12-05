package ru.protei.portal.ui.common.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.service.CaseTagService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.CaseTagController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service("CaseTagController")
public class CaseTagControllerImpl implements CaseTagController {

    @Override
    public void saveTag(CaseTag caseTag) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        ServiceUtils.checkResult(caseTagService.saveTag(authToken, caseTag));
    }

    @Override
    public void removeTag(CaseTag caseTag) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        ServiceUtils.checkResult(caseTagService.removeTag(authToken, caseTag));
    }

    @Override
    public List<CaseTag> getCaseTagsForCaseType(En_CaseType caseType) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(caseTagService.getTagsByCaseType(authToken, caseType));
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    CaseTagService caseTagService;
}
