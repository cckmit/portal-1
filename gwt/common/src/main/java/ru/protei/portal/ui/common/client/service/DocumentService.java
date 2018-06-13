package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.query.DocumentQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/DocumentService")
public interface DocumentService extends RemoteService {

    List<Document> getDocuments(DocumentQuery query) throws RequestFailedException;

    Long getDocumentCount(DocumentQuery query) throws RequestFailedException;

    Document getDocument(Long id) throws RequestFailedException;

    Document saveDocument(Document document) throws RequestFailedException;

    DecimalNumber findDecimalNumber(DecimalNumber decimalNumber) throws RequestFailedException;
}
