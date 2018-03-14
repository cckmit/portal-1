package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.ent.Documentation;
import ru.protei.portal.core.model.query.DocumentationQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/DocumentationService")
public interface DocumentationService extends RemoteService {

    List<Documentation> getDocumentations(DocumentationQuery query) throws RequestFailedException;

    Long getDocumentationCount(DocumentationQuery query) throws RequestFailedException;

    List<DocumentType> getDocumentTypeList() throws RequestFailedException;
}
