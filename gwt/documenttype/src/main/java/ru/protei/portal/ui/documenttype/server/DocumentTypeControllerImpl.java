package ru.protei.portal.ui.documenttype.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.query.DocumentTypeQuery;
import ru.protei.portal.core.service.DocumentTypeService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.DocumentTypeController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service("DocumentTypeController")
public class DocumentTypeControllerImpl implements DocumentTypeController {

    private static final Logger log = LoggerFactory.getLogger(DocumentTypeControllerImpl.class);

    @Override
    public List<DocumentType> getDocumentTypes(DocumentTypeQuery query) throws RequestFailedException {
        log.info("get document type list");

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        Result<List<DocumentType>> response = documentTypeService.documentTypeList(token, query);

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public DocumentType saveDocumentType(DocumentType type) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);

        if (type == null) {
            log.warn("null type in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        Result<DocumentType> response = documentTypeService.saveDocumentType( token, type );
        log.info("store document type, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.info("store document type, applied id: {}", response.getData().getId());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public Long removeDocumentType(DocumentType documentType) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        Result<Long> response = documentTypeService.removeDocumentType(token, documentType);

        if (response.isOk()) {
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Autowired
    private DocumentTypeService documentTypeService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpRequest;

}
