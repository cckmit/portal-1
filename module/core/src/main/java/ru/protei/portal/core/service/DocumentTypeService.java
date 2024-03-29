package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.query.DocumentTypeQuery;

import java.util.List;

public interface DocumentTypeService {
    Result<List<DocumentType>> documentTypeList( AuthToken token, DocumentTypeQuery query);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_TYPE_CREATE, En_Privilege.DOCUMENT_TYPE_EDIT})
    @Auditable(En_AuditType.DOCUMENT_TYPE_CREATE)
    Result<DocumentType> saveDocumentType( AuthToken token, DocumentType documentType);

    @Privileged(En_Privilege.DOCUMENT_TYPE_REMOVE)
    @Auditable(En_AuditType.DOCUMENT_TYPE_REMOVE)
    Result<Long> removeDocumentType(AuthToken authToken, DocumentType documentType);
}
