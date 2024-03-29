package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.DutyLogDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DutyLog;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

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
    PersonDAO personDAO;

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Autowired
    PolicyService policyService;
    @Autowired
    ReportControlService reportControlService;
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
    @Transactional
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
    @Transactional
    public Result<Long> createDutyLog(AuthToken authToken, DutyLog value) {
        if (value.getId() != null || !value.isValid()) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (dutyLogDAO.checkExists(value)) {
            return error(En_ResultStatus.ALREADY_EXIST);
        }

        return ok(dutyLogDAO.persist(value));
    }

    @Override
    public Result<Void> createReport(AuthToken token, String name, DutyLogQuery query) {

        if (query == null || query.getDateRange() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Person initiator = personDAO.get(token.getPersonId());
        jdbcManyRelationsHelper.fill(initiator, Person.Fields.CONTACT_ITEMS);

        reportControlService.processDutyLogReport(initiator, name, query);

        return ok();
    }
}
