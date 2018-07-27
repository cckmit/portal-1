package ru.protei.portal.ui.common.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.ui.common.client.service.AttachmentService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by bondarenko on 13.01.17.
 */

@Service( "AttachmentController" )
public class AttachmentServiceImpl implements AttachmentService{

    @Override
    public List<Attachment> getAttachmentsByCaseId(Long caseId) throws RequestFailedException {
        CoreResponse<List<Attachment>> response =  attachmentService.getAttachmentsByCaseId( getDescriptorAndCheckSession().makeAuthToken(), caseId);

        if(response.isError())
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public List<Attachment> getAttachments(List<Long> attachmentIds) throws RequestFailedException {
        CoreResponse<List<Attachment>> response =  attachmentService.getAttachments( getDescriptorAndCheckSession().makeAuthToken(), attachmentIds);

        if(response.isError())
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public boolean removeAttachmentEverywhere(Long attachmentId) throws RequestFailedException{
        CoreResponse<Boolean> response =  attachmentService.removeAttachmentEverywhere( getDescriptorAndCheckSession().makeAuthToken(), attachmentId);

        if(response.isError())
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public void clearUploadedAttachmentsCache() {
        sessionService.clearFileItem(httpServletRequest);
    }

    private UserSessionDescriptor getDescriptorAndCheckSession() throws RequestFailedException {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor( httpServletRequest );
        if ( descriptor == null ) {
            throw new RequestFailedException( En_ResultStatus.SESSION_NOT_FOUND );
        }

        return descriptor;
    }

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    ru.protei.portal.core.service.AttachmentService attachmentService;

}
