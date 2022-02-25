package ru.protei.portal.ui.common.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_HistoryAction;
import ru.protei.portal.core.model.dict.En_HistoryType;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.service.HistoryService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.AttachmentController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by bondarenko on 13.01.17.
 */

@Service( "AttachmentController" )
public class AttachmentControllerImpl implements AttachmentController {

    @Override
    public List<Attachment> getAttachmentsByCaseId(En_CaseType caseType, Long caseId) throws RequestFailedException {
        log.info("getAttachmentsByCaseId(): caseType={}, caseId={}", caseType, caseId);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<List<Attachment>> response =  attachmentService.getAttachmentsByCaseId( token, caseType, caseId);

        if(response.isError())
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public List<Attachment> getAttachments(En_CaseType caseType, List<Long> attachmentIds) throws RequestFailedException {
        log.info("getAttachments(): caseType={}, attachmentIds={}", caseType, attachmentIds);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<List<Attachment>> response =  attachmentService.getAttachments( token, caseType, attachmentIds);

        if(response.isError())
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public Long removeAttachmentEverywhere(En_CaseType caseType, Long caseId, Long attachmentId) throws RequestFailedException{
        log.info("removeAttachmentEverywhere(): caseType={}, caseId={}, attachmentId={}", caseType, caseId, attachmentId);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Attachment attachment = null;
        if (En_CaseType.CRM_SUPPORT.equals(caseType)){
            Result<Attachment> result =  attachmentService.getAttachment(token, attachmentId);
            if(result.isOk())
                attachment = result.getData();
        }

        Result<Long> response =  attachmentService.removeAttachmentEverywhere( token, caseType, attachmentId);

        if(response.isError())
            throw new RequestFailedException( response.getStatus() );

        if (En_CaseType.CRM_SUPPORT.equals(caseType) && attachment != null && caseId != null){
            historyService.createHistory(token, caseId, En_HistoryAction.REMOVE, En_HistoryType.CASE_ATTACHMENT, attachmentId, attachment.getFileName(), null, null);
        }

        return response.getData();
    }

    @Override
    public void clearUploadedAttachmentsCache() {
        log.info("clearUploadedAttachmentsCache():");
        sessionService.clearAllFiles(httpServletRequest);
    }

    @Override
    public Long addCaseAttachmentHistory(Long caseObjectId, Long attachmentId, String fileName) throws RequestFailedException {
        log.info("addCaseAttachmentHistory(): caseObjectId={}, attachmentId={}, fileName={}", caseObjectId, attachmentId, fileName);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(historyService.createHistory(token, caseObjectId, En_HistoryAction.ADD, En_HistoryType.CASE_ATTACHMENT,null, null, attachmentId, fileName));
    }

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    ru.protei.portal.core.service.AttachmentService attachmentService;

    @Autowired
    HistoryService historyService;

    private static final Logger log = LoggerFactory.getLogger(AttachmentControllerImpl.class);

}
