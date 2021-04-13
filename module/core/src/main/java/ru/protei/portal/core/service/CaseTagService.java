package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseObjectTag;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.query.CaseTagQuery;

import java.util.List;

public interface CaseTagService {

    Result<Long> create( AuthToken authToken, CaseTag caseTag );

    Result<Long> update( AuthToken authToken, CaseTag caseTag);

    Result<Long> removeTag( AuthToken authToken, Long caseTagId);

    Result<List<CaseTag>> getTags(AuthToken token, CaseTagQuery query);

    Result<List<CaseObjectTag>> getCaseObjectTags(AuthToken token, List<Long> caseIds);

    Result<CaseTag> getTag(AuthToken token, Long tagId);

    Result attachTag(AuthToken authToken, Long caseId, Long tagId);

    Result<Long> detachTag( AuthToken authToken, Long caseId, Long tagId);

    void addItemsToHistory(AuthToken authToken, Long caseId, List<CaseTag> tagIds);

    Result<Boolean> isTagNameExists(AuthToken token, CaseTag tag);
}
