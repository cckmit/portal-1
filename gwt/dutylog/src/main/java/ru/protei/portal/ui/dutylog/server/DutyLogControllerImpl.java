package ru.protei.portal.ui.dutylog.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DutyLog;
import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.portal.core.service.DutyLogService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.DutyLogController;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static ru.protei.portal.ui.common.server.ServiceUtils.*;

/**
 * Реализация сервиса по работе с журналом дежурств
 */
@Service( "DutyLogController" )
public class DutyLogControllerImpl implements DutyLogController {

    @Override
    public SearchResult<DutyLog> getDutyLogs(DutyLogQuery query) throws RequestFailedException {
        log.info("getDutyLogs(): query={}", query);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result<SearchResult<DutyLog>> result = service.getDutyLogs(token, query);
        log.info("getDutyLog(): result={}", result.isOk() ? "ok" : result.getStatus());
        return checkResultAndGetData(result);
    }

    @Override
    public DutyLog getDutyLog(Long id) throws RequestFailedException {
        log.info("getDutyLog(): id={}", id);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result<DutyLog> result = service.getDutyLog(token, id);
        log.info("getDutyLog(): result={}", result.isOk() ? "ok" : result.getStatus());
        return checkResultAndGetData(result);
    }

    @Override
    public Long saveDutyLog(DutyLog value) throws RequestFailedException {
        log.info("saveDutyLog(): dutyLog {}", value);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result<Long> result = null;
        if (value.getId() == null) {
            value.setCreated(new Date());
            value.setCreatorId(token.getPersonId());
            result = service.createDutyLog(token, value);
        } else {
            result = service.updateDutyLog(token, value);
        }
        log.info("saveDutyLog(): result={}", result.isOk() ? "ok" : result.getStatus());
        return checkResultAndGetData(result);
    }

    @Override
    public void createReport(String name, DutyLogQuery query) throws RequestFailedException {
        log.info("createReport(): name={}, query={}", name, query);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result<Void> result = service.createReport(token, name, query);
        log.info("createReport(): result={}", result.isOk() ? "ok" : result.getStatus());
        checkResult(result);
    }

    @Autowired
    DutyLogService service;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger(DutyLogControllerImpl.class);
}
