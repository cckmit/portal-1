package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.query.DocumentQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/DocumentController")
public interface DocumentController extends RemoteService {

    List<Document> getDocuments(DocumentQuery query) throws RequestFailedException;

    Integer getDocumentCount(DocumentQuery query) throws RequestFailedException;

    Document getDocument(Long id) throws RequestFailedException;

    Document saveDocument(Document document) throws RequestFailedException;
}
