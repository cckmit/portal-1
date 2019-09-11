package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseTag;

import java.util.List;

public interface CaseTagService {

    Result saveTag( AuthToken authToken, CaseTag caseTag);

    Result removeTag( AuthToken authToken, CaseTag caseTag);

    Result<List<CaseTag>> getTagsByCaseId( AuthToken token, long caseId);

    Result<List<CaseTag>> getTagsByCaseType( AuthToken token, En_CaseType caseType);

    Result<List<CaseTag>> getTagsByCompanyId( AuthToken token, long companyId);
}
