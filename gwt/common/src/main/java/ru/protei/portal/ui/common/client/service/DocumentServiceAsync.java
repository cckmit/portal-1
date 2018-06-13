package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.query.DocumentQuery;

import java.util.List;

public interface DocumentServiceAsync {

    void getDocuments(DocumentQuery query, AsyncCallback<List<Document>> callback);

    void getDocumentCount(DocumentQuery query, AsyncCallback<Long> callback);

    void getDocument(Long id, AsyncCallback<Document> callback);

    void saveDocument(Document document, AsyncCallback<Document> callback);

    void findDecimalNumberForDocument(DecimalNumber decimalNumber, AsyncCallback<DecimalNumber> callback);
}
