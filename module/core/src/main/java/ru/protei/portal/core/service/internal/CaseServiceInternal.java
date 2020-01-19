package ru.protei.portal.core.service.internal;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.CaseObjectCreateRequest;

/**
 * Сервис внутренний для управления обращениями
 * Сервис фасада {@link ru.protei.portal.core.service.CaseService}
 */
public interface CaseServiceInternal {

    Result<CaseObject> createCaseObjectInternal( AuthToken token, CaseObjectCreateRequest caseObjectCreateRequest );

}
