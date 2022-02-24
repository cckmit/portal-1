package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CaseTimeElapsedApiSumDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseTimeElapsedApiSum;
import ru.protei.portal.core.model.query.CaseTimeElapsedApiQuery;

import java.util.List;

public class CaseTimeElapsedApiServiceImpl implements CaseTimeElapsedApiService {
    @Autowired
    CaseTimeElapsedApiSumDAO caseTimeElapsedApiSumDAO;

    @Override
    public Result<List<CaseTimeElapsedApiSum>> getByQuery(AuthToken authToken, CaseTimeElapsedApiQuery query) {
        if (query == null) {
            return Result.error(En_ResultStatus.INCORRECT_PARAMS);
        }
        return Result.ok(caseTimeElapsedApiSumDAO.listByQuery(query));
    }
}
