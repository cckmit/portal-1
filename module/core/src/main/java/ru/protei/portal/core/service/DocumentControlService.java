package ru.protei.portal.core.service;

import org.apache.commons.fileupload.FileItem;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.Document;

public interface DocumentControlService {

    CoreResponse<Document> getDocument(Long id);

    CoreResponse<Document> createDocument(Document document, FileItem fileItem);

    CoreResponse<Document> updateDocument(Document document);

    CoreResponse<Document> updateDocumentAndContent(Document document, FileItem fileItem);
}
