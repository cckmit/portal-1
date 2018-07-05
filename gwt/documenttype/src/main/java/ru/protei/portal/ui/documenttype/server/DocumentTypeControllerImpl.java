package ru.protei.portal.ui.documenttype.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.query.DocumentTypeQuery;
import ru.protei.portal.core.service.DocumentTypeService;
import ru.protei.portal.ui.common.client.service.DocumentTypeController;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service("DocumentTypeController")
public class DocumentTypeControllerImpl implements DocumentTypeController {

    private static final Logger log = LoggerFactory.getLogger("web");

    @Override
    public List<DocumentType> getDocumentTypes(DocumentTypeQuery query) throws RequestFailedException {
        log.debug("get document type list");

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<List<DocumentType>> response = documentTypeService.documentTypeList(descriptor.makeAuthToken(), query);

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public DocumentType saveDocumentType(DocumentType type) throws RequestFailedException {
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        if (type == null) {
            log.warn("null type in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        CoreResponse<DocumentType> response = documentTypeService.saveDocumentType( descriptor.makeAuthToken(), type );
        log.debug("store document type, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.debug("store document type, applied id: {}", response.getData().getId());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }


    private UserSessionDescriptor getDescriptorAndCheckSession() throws RequestFailedException {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor(httpRequest);
        log.info("userSessionDescriptor={}", descriptor);
        if (descriptor == null) {
            throw new RequestFailedException(En_ResultStatus.SESSION_NOT_FOUND);
        }

        return descriptor;
    }

    @Autowired
    private DocumentTypeService documentTypeService;


    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpRequest;

}
