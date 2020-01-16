package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_DocumentState;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.query.DocumentQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

@RemoteServiceRelativePath("springGwtServices/DocumentController")
public interface DocumentController extends RemoteService {

    SearchResult<Document> getDocuments(DocumentQuery query) throws RequestFailedException;

    Document getDocument(Long id) throws RequestFailedException;

    Document saveDocument(Document document) throws RequestFailedException;

    Document updateDocumentDocFileByMember(Long documentId, String comment) throws RequestFailedException;

    Long removeDocument(Document document) throws RequestFailedException;

    SearchResult<Document> getProjectDocuments(Long projectId) throws RequestFailedException;

    Boolean updateState(Long documentId, En_DocumentState state) throws RequestFailedException;
}
