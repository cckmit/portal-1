package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.query.CaseTagQuery;

import java.util.List;

public interface CaseTagService {

    Result<Long> saveTag( AuthToken authToken, CaseTag caseTag);

    Result<Long> removeTag( AuthToken authToken, CaseTag caseTag);

    Result<List<CaseTag>> getTags(AuthToken token, CaseTagQuery query);

    Result attachTag(AuthToken authToken, Long caseId, Long tagId);

    Result detachTag(AuthToken authToken, Long caseId, Long tagId);
}
