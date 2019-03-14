package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseObjectTag;
import ru.protei.portal.core.model.ent.CaseTag;

import java.util.List;

public interface CaseTagService {

    CoreResponse createTag(AuthToken token, CaseTag caseTag);

    CoreResponse<List<CaseObjectTag>> getTagsForCase(AuthToken token, long caseId);

    CoreResponse<List<CaseTag>> getTagList(AuthToken token, En_CaseType caseType);
}
