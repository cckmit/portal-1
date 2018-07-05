package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.query.DocumentTypeQuery;

import java.util.List;

public interface DocumentTypeService {
    CoreResponse<List<DocumentType>> documentTypeList(AuthToken token, DocumentTypeQuery query);

    @Privileged(requireAny = {En_Privilege.DOCUMENT_TYPE_CREATE, En_Privilege.DOCUMENT_TYPE_EDIT})
    CoreResponse<DocumentType> saveDocumentType(AuthToken token, DocumentType documentType);
}
