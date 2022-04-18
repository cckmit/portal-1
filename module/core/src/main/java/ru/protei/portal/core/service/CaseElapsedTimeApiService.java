package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseElapsedTimeApi;
import ru.protei.portal.core.model.query.CaseElapsedTimeApiQuery;

import java.util.List;

public interface CaseElapsedTimeApiService {
    Result<List<CaseElapsedTimeApi>> getByQuery(AuthToken authToken, CaseElapsedTimeApiQuery query );
}
