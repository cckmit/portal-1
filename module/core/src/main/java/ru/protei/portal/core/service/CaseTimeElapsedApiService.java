package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseTimeElapsedApiSum;
import ru.protei.portal.core.model.query.CaseTimeElapsedApiQuery;

import java.util.List;

public interface CaseTimeElapsedApiService {
    Result<List<CaseTimeElapsedApiSum>> getByQuery(AuthToken authToken, CaseTimeElapsedApiQuery query );
}
