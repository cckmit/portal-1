package ru.protei.portal.ui.absence.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.service.AbsenceService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.AbsenceController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Реализация сервиса по работе с отсутствиями
 */
@Service( "AbsenceController" )
public class AbsenceControllerImpl implements AbsenceController {

    @Override
    public Long createAbsence(PersonAbsence absence) throws RequestFailedException {
        log.info("createAbsence(): absence={} ", absence);

        if (absence == null) {
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        if (isExistsAbsence(absence.getPersonId(), absence.getFromTime(), absence.getTillTime(), absence.getId()))
            throw new RequestFailedException (En_ResultStatus.ALREADY_EXIST);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Long> response = absenceService.createAbsence(token, absence);
        log.info("createAbsence(): result={}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            return response.getData();
        }
        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public Boolean isExistsAbsence(Long employeeId, Date dateFrom, Date dateTill, Long excludeId) throws RequestFailedException {
        log.info("checkExistsAbsence(): employeeId={} | dateFrom={} | dateTill={} | excludeId={}", employeeId, dateFrom, dateTill, excludeId);
        Result<Boolean> response = absenceService.isExistsAbsence(employeeId, dateFrom, dateTill, excludeId);
        log.info("checkExistsAbsence(): result={}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            return response.getData();
        }
        throw new RequestFailedException(response.getStatus());
    }

    @Autowired
    AbsenceService absenceService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger(AbsenceControllerImpl.class);
}
