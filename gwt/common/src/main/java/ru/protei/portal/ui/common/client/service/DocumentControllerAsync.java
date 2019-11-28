package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_DocumentState;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.query.DocumentQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

public interface DocumentControllerAsync {

    void getDocuments(DocumentQuery query, AsyncCallback<SearchResult<Document>> callback);

    void getDocument(Long id, AsyncCallback<Document> callback);

    void updateState(Long documentId, En_DocumentState state, AsyncCallback<Boolean> callback);

    void saveDocument(Document document, AsyncCallback<Document> callback);

    void removeDocument(Document document, AsyncCallback<Long> async);

    void getProjectDocuments(Long projectId, AsyncCallback<SearchResult<Document>> callback);
}
