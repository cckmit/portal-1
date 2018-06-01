package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.query.DocumentTypeQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/DocumentTypeService")
public interface DocumentTypeService extends RemoteService {

    List<DocumentType> getDocumentTypes(DocumentTypeQuery query) throws RequestFailedException;

    DocumentType saveDocumentType(DocumentType type) throws RequestFailedException;

    boolean removeDocumentType(Long forRemoveId) throws RequestFailedException;
}
