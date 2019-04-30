package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.query.DocumentQuery;

import java.util.List;

public interface DocumentControllerAsync {

    void getDocuments(DocumentQuery query, AsyncCallback<List<Document>> callback);

    void getDocumentCount(DocumentQuery query, AsyncCallback<Integer> callback);

    void getDocument(Long id, AsyncCallback<Document> callback);

    void saveDocument(Document document, AsyncCallback<Document> callback);

    void getProjectDocuments(Long projectId, AsyncCallback<List<Document>> callback);
}
