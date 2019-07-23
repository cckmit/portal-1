package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseTag;

import java.util.List;

public interface CaseTagService {

    CoreResponse saveTag(AuthToken authToken, CaseTag caseTag);

    CoreResponse removeTag(AuthToken authToken, CaseTag caseTag);

    CoreResponse<List<CaseTag>> getTagsByCaseId(AuthToken token, long caseId);

    CoreResponse<List<CaseTag>> getTagsByCaseType(AuthToken token, En_CaseType caseType);

    CoreResponse<List<CaseTag>> getTagsByCompanyId(AuthToken token, long companyId);
}
