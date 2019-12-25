package ru.protei.portal.ui.common.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.service.CaseCommentService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.CaseCommentController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service("CaseCommentController")
public class CaseCommentControllerImpl implements CaseCommentController {

    @Override
    public List<CaseComment> getCaseComments(En_CaseType caseType, Long caseId) throws RequestFailedException {
        log.info("getCaseComments(): caseType={}, issueId={}", caseType, caseId);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<List<CaseComment>> response = caseCommentService.getCaseCommentList(token, caseType, caseId);
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }

        return response.getData();
    }

    @Override
    public CaseComment saveCaseComment(En_CaseType caseType, CaseComment comment) throws RequestFailedException {
        log.info("saveCaseComment(): caseType={}, comment={}", caseType, comment);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<CaseComment> response;
        if (comment.getId() == null) {
            response = caseCommentService.addCaseComment(token, caseType, comment);
        } else {
            response = caseCommentService.updateCaseComment(token, caseType, comment);
        }
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }

        return response.getData();
    }

    @Override
    public void removeCaseComment(En_CaseType caseType, CaseComment comment) throws RequestFailedException {
        log.info("removeCaseComment(): caseType={}, comment={}", caseType, comment);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<Boolean> response = caseCommentService.removeCaseComment(token, caseType, comment);
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
    }

    @Override
    public Boolean updateCaseTimeElapsedType(Long caseCommentId, En_TimeElapsedType type) throws RequestFailedException {
        log.info("removeCaseComment(): caseCommentId={}, type={}", caseCommentId, type);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<Boolean> response = caseCommentService.updateCaseTimeElapsedType(token, caseCommentId, type);
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }

        return response.getData();
    }

    @Autowired
    CaseCommentService caseCommentService;
    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger(CaseCommentControllerImpl.class);
}
