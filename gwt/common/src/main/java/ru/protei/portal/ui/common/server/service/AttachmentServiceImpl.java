package ru.protei.portal.ui.common.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.AttachmentService;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by bondarenko on 13.01.17.
 */

@Service( "AttachmentController" )
public class AttachmentServiceImpl implements AttachmentService{

    @Override
    public List<Attachment> getAttachmentsByCaseId(En_CaseType caseType, Long caseId) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<List<Attachment>> response =  attachmentService.getAttachmentsByCaseId( token, caseType, caseId);

        if(response.isError())
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public List<Attachment> getAttachments(En_CaseType caseType, List<Long> attachmentIds) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<List<Attachment>> response =  attachmentService.getAttachments( token, caseType, attachmentIds);

        if(response.isError())
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public boolean removeAttachmentEverywhere(En_CaseType caseType, Long attachmentId) throws RequestFailedException{
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<Boolean> response =  attachmentService.removeAttachmentEverywhere( token, caseType, attachmentId);

        if(response.isError())
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public void clearUploadedAttachmentsCache() {
        sessionService.clearFileItem(httpServletRequest);
    }

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    ru.protei.portal.core.service.AttachmentService attachmentService;

}
