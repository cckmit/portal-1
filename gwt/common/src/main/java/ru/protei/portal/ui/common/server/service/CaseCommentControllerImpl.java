package ru.protei.portal.ui.common.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.service.CaseCommentService;
import ru.protei.portal.ui.common.client.service.CaseCommentController;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service("CaseCommentController")
public class CaseCommentControllerImpl implements CaseCommentController {

    @Override
    public List<CaseComment> getCaseComments(En_CaseType caseType, Long caseId) throws RequestFailedException {
        log.debug("getCaseComments(): caseType={}, issueId={}", caseType, caseId);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<List<CaseComment>> response = caseCommentService.getCaseCommentList(descriptor.makeAuthToken(), caseType, caseId);
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }

        return response.getData();
    }

    @Override
    public CaseComment saveCaseComment(En_CaseType caseType, CaseComment comment) throws RequestFailedException {
        log.debug("saveCaseComment(): caseType={}, comment={}", caseType, comment);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<CaseComment> response;
        if (comment.getId() == null) {
            response = caseCommentService.addCaseComment(descriptor.makeAuthToken(), caseType, comment, descriptor.getPerson());
        } else {
            response = caseCommentService.updateCaseComment(descriptor.makeAuthToken(), caseType, comment, descriptor.getPerson());
        }
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }

        return response.getData();
    }

    @Override
    public void removeCaseComment(En_CaseType caseType, CaseComment comment) throws RequestFailedException {
        log.debug("removeCaseComment(): caseType={}, comment={}", caseType, comment);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<Boolean> response = caseCommentService.removeCaseComment(descriptor.makeAuthToken(), caseType, comment, descriptor.getPerson().getId());
        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
    }

    private UserSessionDescriptor getDescriptorAndCheckSession() throws RequestFailedException {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor(httpServletRequest);
        if (descriptor == null) {
            throw new RequestFailedException(En_ResultStatus.SESSION_NOT_FOUND);
        }
        return descriptor;
    }

    @Autowired
    CaseCommentService caseCommentService;
    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger(CaseCommentControllerImpl.class);
}