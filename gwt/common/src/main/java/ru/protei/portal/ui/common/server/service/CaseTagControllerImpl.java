package ru.protei.portal.ui.common.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.query.CaseTagQuery;
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
        caseTag.setPersonId( authToken.getPersonId() );
        ServiceUtils.checkResult(caseTagService.saveTag(authToken, caseTag));
    }

    @Override
    public void removeTag(CaseTag caseTag) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        ServiceUtils.checkResult(caseTagService.removeTag(authToken, caseTag));
    }

    @Override
    public List<CaseTag> getTags(CaseTagQuery query) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(caseTagService.getTags(authToken, query));
    }

    @Override
    public void attachTag(Long caseId, Long tagId) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        ServiceUtils.checkResult(caseTagService.attachTag(authToken, caseId, tagId));
    }

    @Override
    public void detachTag(Long caseId, Long tagId) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        ServiceUtils.checkResult(caseTagService.detachTag(authToken, caseId, tagId));
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    CaseTagService caseTagService;
}
