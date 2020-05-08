package ru.protei.portal.ui.absence.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.service.AbsenceService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.AbsenceController;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;
import static ru.protei.portal.ui.common.server.ServiceUtils.getAuthToken;

/**
 * Реализация сервиса по работе с отсутствиями
 */
@Service( "AbsenceController" )
public class AbsenceControllerImpl implements AbsenceController {

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
                absenceService.createAbsence(token, absence) :
                absenceService.updateAbsence(token, absence);
        log.info("saveAbsence(): result={}", result.isOk() ? "ok" : result.getStatus());
        return checkResultAndGetData(result);
    }

    @Autowired
    AbsenceService absenceService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger(AbsenceControllerImpl.class);
}
