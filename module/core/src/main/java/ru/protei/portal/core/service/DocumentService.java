package ru.protei.portal.core.service;

import org.apache.commons.fileupload.FileItem;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Document;
import ru.protei.portal.core.model.query.DocumentQuery;

import java.util.List;

public interface DocumentService {

    @Privileged(En_Privilege.DOCUMENT_VIEW)
    CoreResponse<Integer> count(AuthToken token, DocumentQuery query);

    @Privileged(En_Privilege.DOCUMENT_VIEW)
    CoreResponse<List<Document>> documentList(AuthToken token, DocumentQuery query);

    @Privileged(En_Privilege.DOCUMENT_VIEW)
    CoreResponse<Document> getDocument(AuthToken token, Long id);

    @Privileged(requireAny = En_Privilege.DOCUMENT_EDIT)
    @Auditable(En_AuditType.DOCUMENT_MODIFY)
    CoreResponse<Document> updateDocument(AuthToken token, Document document);

    @Privileged(requireAny = En_Privilege.DOCUMENT_CREATE)
    CoreResponse<Document> createDocument(AuthToken token, Document document, FileItem fileItem);
}
