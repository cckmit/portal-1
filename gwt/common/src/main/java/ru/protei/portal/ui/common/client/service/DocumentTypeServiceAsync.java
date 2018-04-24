package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.DocumentType;

import java.util.List;

public interface DocumentTypeServiceAsync {
    void getDocumentTypes(AsyncCallback<List<DocumentType>> callback);
}
