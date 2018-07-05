package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.query.DocumentTypeQuery;

import java.util.List;

public interface DocumentTypeControllerAsync {

    void getDocumentTypes(DocumentTypeQuery query, AsyncCallback<List<DocumentType>> async);

    void saveDocumentType(DocumentType type, AsyncCallback<DocumentType> async);
}
