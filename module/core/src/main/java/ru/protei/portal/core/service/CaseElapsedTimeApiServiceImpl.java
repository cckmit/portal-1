package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CaseElapsedTimeApiDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseElapsedTimeApi;
import ru.protei.portal.core.model.query.CaseElapsedTimeApiQuery;

import java.util.List;

public class CaseElapsedTimeApiServiceImpl implements CaseElapsedTimeApiService {
    @Autowired
    CaseElapsedTimeApiDAO caseElapsedTimeApiDAO;

    @Override
    public Result<List<CaseElapsedTimeApi>> getByQuery(AuthToken authToken, CaseElapsedTimeApiQuery query) {
        if (query == null) {
            return Result.error(En_ResultStatus.INCORRECT_PARAMS);
        }
        return Result.ok(caseElapsedTimeApiDAO.listByQuery(query));
    }
}
