package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.DutyLogDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DutyLog;
import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Date;
import java.util.Objects;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

/**
 * Реализация сервиса журналов дежурств
 */
public class DutyLogServiceImpl implements DutyLogService {

    private static Logger log = LoggerFactory.getLogger(DutyLogServiceImpl.class);

    @Autowired
    DutyLogDAO dutyLogDAO;

    @Autowired
    PolicyService policyService;

    @Autowired
    AuthService authService;

    @Override
    public Result<SearchResult<DutyLog>> getDutyLogs(AuthToken authToken, DutyLogQuery query) {
        SearchResult<DutyLog> result = dutyLogDAO.getSearchResultByQuery(query);
        return ok(result);
    }

    @Override
    public Result<DutyLog> getDutyLog(AuthToken authToken, Long id) {
        if (id == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        DutyLog result = dutyLogDAO.get(id);
        return result == null ? error(En_ResultStatus.NOT_FOUND) : ok(result);
    }

    @Override
    public Result<Long> updateDutyLog(AuthToken authToken, DutyLog value) {
        if (value.getId() == null || !value.isValid()) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (dutyLogDAO.checkExists(value)) {
            return error(En_ResultStatus.ALREADY_EXIST);
        }

        boolean result = dutyLogDAO.merge(value);
        return result ? ok(value.getId()) : error(En_ResultStatus.INTERNAL_ERROR);
    }

    @Override
    public Result<Long> createDutyLog(AuthToken authToken, DutyLog value) {
        if (value.getId() != null || !value.isValid()) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (dutyLogDAO.checkExists(value)) {
            return error(En_ResultStatus.ALREADY_EXIST);
        }

        return ok(dutyLogDAO.persist(value));
    }
}
