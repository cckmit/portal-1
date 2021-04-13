package ru.protei.portal.ui.common.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        log.info("create(): caseTag={}", caseTag);
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(caseTagService.create(authToken, caseTag));
    }

    @Override
    public Long update( CaseTag caseTag) throws RequestFailedException {
        log.info("update(): caseTag={}", caseTag);
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(caseTagService.update(authToken, caseTag));
    }

    @Override
    public Long removeTag(Long caseTagId) throws RequestFailedException {
        log.info("removeTag(): caseTagId={}", caseTagId);
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(caseTagService.removeTag(authToken, caseTagId));
    }

    @Override
    public List<CaseTag> getTags(CaseTagQuery query) throws RequestFailedException {
        log.info("getTags(): query={}", query);
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(caseTagService.getTags(authToken, query));
    }

    @Override
    public void attachTag(Long caseId, Long tagId) throws RequestFailedException {
        log.info("attachTag(): caseId={}, tagId={}", caseId, tagId);
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        ServiceUtils.checkResult(caseTagService.attachTag(authToken, caseId, tagId));
    }

    @Override
    public Long detachTag( Long caseId, Long tagId) throws RequestFailedException {
        log.info("detachTag(): caseId={}, tagId={}", caseId, tagId);
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(caseTagService.detachTag(authToken, caseId, tagId));
    }

    @Override
    public boolean isTagNameExists(CaseTag tag) throws RequestFailedException {
        log.info("isCaseTagNameExists(): id={}, name={}, companyId={}, caseType={}",
                  tag.getId(), tag.getName(), tag.getCompanyId(), tag.getCaseType());
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(caseTagService.isTagNameExists(authToken, tag));
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    CaseTagService caseTagService;

    private static final Logger log = LoggerFactory.getLogger(CaseTagControllerImpl.class);
}
