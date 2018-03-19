package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.ent.Documentation;
import ru.protei.portal.core.model.query.DocumentationQuery;

import java.util.List;

public interface DocumentationService {

    @Privileged(En_Privilege.DOCUMENTATION_VIEW)
    CoreResponse<Long> count(AuthToken token, DocumentationQuery query);

    @Privileged(En_Privilege.DOCUMENTATION_VIEW)
    CoreResponse<List<Documentation>> documentationList(AuthToken token, DocumentationQuery query);

    @Privileged(En_Privilege.DOCUMENTATION_VIEW)
    CoreResponse<List<DocumentType>> documentTypeList(AuthToken token);

    @Privileged(En_Privilege.DOCUMENTATION_VIEW)
    CoreResponse<Documentation> getDocumentation(AuthToken token, Long id);

    @Privileged(requireAny = {En_Privilege.DOCUMENTATION_EDIT, En_Privilege.DOCUMENTATION_CREATE})
    @Auditable(En_AuditType.DOCUMENTATION_MODIFY)
    CoreResponse<Documentation> saveDocumentation(AuthToken token, Documentation documentation);
}
