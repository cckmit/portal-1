package ru.protei.portal.ui.document.server;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_DocumentState;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.DocumentQuery;
import ru.protei.portal.core.service.DocumentService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.DocumentController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;

@Service("DocumentController")
public class DocumentControllerImpl implements DocumentController {

    @Override
    public SearchResult<Document> getDocuments(DocumentQuery query) throws RequestFailedException {
        log.info("get documents: offset={} | limit={}", query.getOffset(), query.getLimit());
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        return ServiceUtils.checkResultAndGetData(documentService.getDocuments(token, query));
    }

    @Override
    public Document getDocument(Long id) throws RequestFailedException {
        log.info("get document, id: {}", id);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);

        Result<Document> response = documentService.getDocument(token, id);
        log.info("get document, id: {} -> {} ", id, response.isError() ? "error" : response.getData());

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

        log.info("save document, id: {}", HelperFunc.nvlt(document.getId(), "new"));

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        Result<Document> response;

        FileItem pdfFile = sessionService.getFilePdf(httpRequest);
        FileItem docFile = sessionService.getFileDoc(httpRequest);
        sessionService.setFilePdf(httpRequest, null);
        sessionService.setFileDoc(httpRequest, null);

        if (document.getId() == null) {
            response = documentService.createDocument(token, document, docFile, pdfFile, token.getPersonDisplayShortName());
        } else {
            response = documentService.updateDocument(token, document, docFile, pdfFile, token.getPersonDisplayShortName());
        }

        log.info("save document, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.info("save role, applied id: {}", response.getData().getId());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public Document updateDocumentDocFileByMember(Long documentId, String comment) throws RequestFailedException {
        log.info("updateDocumentDocFileByMember(): documentId={}, comment={}", documentId, comment);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        FileItem docFile = sessionService.getFileDoc(httpRequest);
        sessionService.setFileDoc(httpRequest, null);
        Result<Document> response = documentService.updateDocumentDocFileByMember(token, documentId, docFile, comment, token.getPersonDisplayShortName());
        return ServiceUtils.checkResultAndGetData(response);
    }

    @Override
    public Long removeDocument(Document document) throws RequestFailedException {
        if (document == null) {
            log.info("removeDocument(): null document in request");
            throw new RequestFailedException(En_ResultStatus.INCORRECT_PARAMS);
        }
        log.info("removeDocument(): id = {}", document.getId());
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        Result<Long> result = documentService.removeDocument(token, document.getId(), document.getProjectId(), token.getPersonDisplayShortName());
        log.info("removeDocument(): id = {}, status = {}", document.getId(), result.getStatus());
        return ServiceUtils.checkResultAndGetData(result);
    }

    @Override
    public Boolean updateState(Long documentId, En_DocumentState state) throws RequestFailedException {
        log.info("change state document, id: {} | state: {}", documentId, state);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        Result response = documentService.updateState(token, documentId, state);

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }

        log.info("change state document, result: {}", response.isOk() ? "ok" : response.getStatus());

        return response.getData() != null;
    }

    @Override
    public SearchResult<Document> getProjectDocuments(Long projectId) throws RequestFailedException {
        log.info("get projectDocuments, id: {}", projectId);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        Result<SearchResult<Document>> response = documentService.getProjectDocuments(token, projectId);
        log.info("get ProjectDocuments, id: {} -> {} ", projectId, response.isError() ? "error" : response.getData());
        return ServiceUtils.checkResultAndGetData(response);
    }

    @Autowired
    private DocumentService documentService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpRequest;

    private static final Logger log = LoggerFactory.getLogger(DocumentControllerImpl.class);
}
