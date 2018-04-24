package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DocumentType;

import java.util.List;

public interface DocumentTypeService {
    CoreResponse<List<DocumentType>> documentTypeList(AuthToken token);
}
