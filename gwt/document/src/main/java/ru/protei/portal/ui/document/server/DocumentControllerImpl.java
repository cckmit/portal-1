package ru.protei.portal.ui.document.server;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.DocumentQuery;
import ru.protei.portal.core.service.DocumentService;
import ru.protei.portal.ui.common.client.service.DocumentController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;

@Service("DocumentController")
public class DocumentControllerImpl implements DocumentController {

    @Override
    public SearchResult<Document> getDocuments(DocumentQuery query) throws RequestFailedException {
        log.debug("get documents: offset={} | limit={}", query.getOffset(), query.getLimit());
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        return ServiceUtils.checkResultAndGetData(documentService.getDocuments(token, query));
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
        CoreResponse<Document> response;
        if (document.getId() == null) {
            FileItem fileItem = sessionService.getFileItem(httpRequest);
            if (fileItem == null) {
                log.error("file item in session was null");
                throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
            }
            sessionService.setFileItem(httpRequest, null);
            response = documentService.createDocument(descriptor.makeAuthToken(), document, fileItem);
        } else {
            response = documentService.updateDocument(descriptor.makeAuthToken(), document);
        }

        log.debug("save document, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.debug("save role, applied id: {}", response.getData().getId());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public Boolean changeState (Document document) throws RequestFailedException {
        if (document == null) {
            log.error("null id in request");
            throw new RequestFailedException(En_ResultStatus.INCORRECT_PARAMS);
        }

        log.debug("change state document, id: {}", document.getId());

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();
        CoreResponse response = documentService.changeDocumentState(descriptor.makeAuthToken(), document);

        log.debug("change state document, result: {}", response.isOk() ? "ok" : response.getStatus());

        return response.getData() != null;
    }

    @Override
    public SearchResult<Document> getProjectDocuments(Long projectId) throws RequestFailedException {
        log.debug("get projectDocuments, id: {}", projectId);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        CoreResponse<SearchResult<Document>> response = documentService.getProjectDocuments(token, projectId);
        log.debug("get ProjectDocuments, id: {} -> {} ", projectId, response.isError() ? "error" : response.getData());
        return ServiceUtils.checkResultAndGetData(response);
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
    private DocumentService documentService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpRequest;

    private static final Logger log = LoggerFactory.getLogger(DocumentControllerImpl.class);
}
