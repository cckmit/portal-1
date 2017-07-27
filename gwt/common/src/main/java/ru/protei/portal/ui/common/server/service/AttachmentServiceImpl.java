package ru.protei.portal.ui.common.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.ui.common.client.service.AttachmentService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Created by bondarenko on 13.01.17.
 */

@Service( "AttachmentService" )
public class AttachmentServiceImpl implements AttachmentService{

    @Override
    public List<Attachment> getAttachmentsByCaseId(Long caseId) throws RequestFailedException {
        CoreResponse<List<Attachment>> response =  attachmentService.getAttachmentsByCaseId(caseId);

        if(response.isError())
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public List<Attachment> getAttachments(List<Long> attachmentIds) throws RequestFailedException {
        CoreResponse<List<Attachment>> response =  attachmentService.getAttachments(attachmentIds);

        if(response.isError())
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Override
    public boolean removeAttachmentEverywhere(Long attachmentId) throws RequestFailedException{
        CoreResponse<Boolean> response =  attachmentService.removeAttachmentEverywhere(attachmentId);

        if(response.isError())
            throw new RequestFailedException( response.getStatus() );

        return response.getData();
    }

    @Autowired
    ru.protei.portal.core.service.AttachmentService attachmentService;

}
