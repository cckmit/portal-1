package ru.protei.portal.ui.documenttype.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.ui.common.client.service.DocumentTypeService;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service("DocumentTypeService")
public class DocumentTypeServiceImpl implements DocumentTypeService {

    private static final Logger log = LoggerFactory.getLogger("web");

    @Override
    public List<DocumentType> getDocumentTypes() throws RequestFailedException {
        log.debug("get document type list");

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<List<DocumentType>> response = documentTypeService.documentTypeList(descriptor.makeAuthToken());

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
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
    ru.protei.portal.core.service.DocumentTypeService documentTypeService;


    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpRequest;

}
