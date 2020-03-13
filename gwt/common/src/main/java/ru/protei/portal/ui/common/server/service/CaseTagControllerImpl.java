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

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;

@Service("CaseTagController")
public class CaseTagControllerImpl implements CaseTagController {

    @Override
    public Long create( CaseTag caseTag) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(caseTagService.create(authToken, caseTag));
    }

    @Override
    public Long update( CaseTag caseTag) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(caseTagService.update(authToken, caseTag));
    }

    @Override
    public Long removeTag(Long caseTagId) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(caseTagService.removeTag(authToken, caseTagId));
    }

    @Override
    public List<CaseTag> getTags(CaseTagQuery query) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(caseTagService.getTags(authToken, query));
    }

    @Override
    public void attachTag(Long caseId, Long tagId) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        ServiceUtils.checkResult(caseTagService.attachTag(authToken, caseId, tagId));
    }

    @Override
    public Long detachTag( Long caseId, Long tagId) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(caseTagService.detachTag(authToken, caseId, tagId));
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    CaseTagService caseTagService;
}
