package ru.protei.portal.ui.absence.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.service.AbsenceService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.AbsenceController;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;

import static ru.protei.portal.ui.common.server.ServiceUtils.*;

/**
 * Реализация сервиса по работе с отсутствиями
 */
@Service( "AbsenceController" )
public class AbsenceControllerImpl implements AbsenceController {

    @Override
    public SearchResult<PersonAbsence> getAbsences(AbsenceQuery query) throws RequestFailedException {
        log.info("getAbsences(): query={}", query);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result<SearchResult<PersonAbsence>> result = absenceService.getAbsences(token, query);
        log.info("getAbsence(): result={}", result);
        return checkResultAndGetData(result);
    }

    @Override
    public PersonAbsence getAbsence(Long id) throws RequestFailedException {
        log.info("getAbsence(): id={}", id);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result<PersonAbsence> result = absenceService.getAbsence(token, id);
        log.info("getAbsence(): result={}", result.isOk() ? "ok" : result.getStatus());
        return checkResultAndGetData(result);
    }

    @Override
    public Long saveAbsence(PersonAbsence absence) throws RequestFailedException {
        log.info("saveAbsence(): absence={}", absence);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result<Long> result = absence.getId() == null ?
                absenceService.createAbsenceFromPortal(token, absence) :
                absenceService.updateAbsence(token, absence);
        log.info("saveAbsence(): result={}", result.isOk() ? "ok" : result.getStatus());
        return checkResultAndGetData(result);
    }

    @Override
    public Long removeAbsence(PersonAbsence absence) throws RequestFailedException {
        log.info("removeAbsence(): absence={}", absence);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result<Long> result = absenceService.removeAbsence(token, absence);
        log.info("removeAbsence(): result={}", result.isOk() ? "ok" : result.getStatus());
        return checkResultAndGetData(result);
    }

    @Override
    public Boolean completeAbsence(PersonAbsence absence) throws RequestFailedException {
        log.info("completeAbsence(): absence={}", absence);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result<Boolean> result = absenceService.completeAbsence(token, absence);
        log.info("completeAbsence(): result={}", result.isOk() ? "ok" : result.getStatus());
        return checkResultAndGetData(result);
    }

    @Override
    public void createReport(String name, AbsenceQuery query) throws RequestFailedException {
        log.info("createReport(): name={}, query={}", name, query);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result<Void> result = absenceService.createReport(token, name, query);
        log.info("createReport(): result={}", result.isOk() ? "ok" : result.getStatus());
        checkResult(result);
    }

    @Autowired
    AbsenceService absenceService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger(AbsenceControllerImpl.class);
}
