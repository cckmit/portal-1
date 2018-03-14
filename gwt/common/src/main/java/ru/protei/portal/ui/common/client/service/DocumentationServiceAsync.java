package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.ent.Documentation;
import ru.protei.portal.core.model.query.DocumentationQuery;

import java.util.List;

public interface DocumentationServiceAsync {

    void getDocumentations(DocumentationQuery query, AsyncCallback<List<Documentation>> callback);

    void getDocumentationCount(DocumentationQuery query, AsyncCallback<Long> callback);

    void getDocumentTypeList(AsyncCallback<List<DocumentType>> callback);
}
