package ru.protei.portal.ui.document.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.DocumentQuery;
import ru.protei.portal.ui.common.client.service.DocumentService;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service("DocumentService")
public class DocumentServiceImpl implements DocumentService {
    @Override
    public List<Document> getDocuments(DocumentQuery query) throws RequestFailedException {
        log.debug("get documents: offset={} | limit={}",
                query.getOffset(), query.getLimit());

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<List<Document>> response = documentService.documentList(descriptor.makeAuthToken(), query);

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Document getDocument(Long id) throws RequestFailedException {
        log.debug("get document, id: {}", id);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<Document> response = documentService.getDocument(descriptor.makeAuthToken(), id);
        log.debug("get document, id: {} -> {} ", id, response.isError() ? "error" : response.getData());

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Document saveDocument(Document document) throws RequestFailedException {
        if (document == null) {
            log.warn("null document in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        log.debug("save document, id: {}", HelperFunc.nvlt(document.getId(), "new"));

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse<Document> response = documentService.saveDocument(descriptor.makeAuthToken(), document);

        log.debug("save document, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.debug("save role, applied id: {}", response.getData().getId());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public Long getDocumentCount(DocumentQuery query) throws RequestFailedException {
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        log.debug("get document count(): query={}", query);
        return documentService.count(descriptor.makeAuthToken(), query).getData();
    }

    @Override
    public DecimalNumber findDecimalNumberForDocument(DecimalNumber decimalNumber) throws RequestFailedException {
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        log.debug("find decimal number for document(): decimal number={}", decimalNumber);
        CoreResponse<DecimalNumber> response = documentService.findDecimalNumberForDocument(descriptor.makeAuthToken(), decimalNumber);
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
    ru.protei.portal.core.service.DocumentService documentService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpRequest;

    private static final Logger log = LoggerFactory.getLogger("web");
}
