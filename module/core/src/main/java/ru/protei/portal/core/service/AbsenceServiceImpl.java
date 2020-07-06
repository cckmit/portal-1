package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.AbsenceNotificationEvent;
import ru.protei.portal.core.event.EventAction;
import ru.protei.portal.core.event.AbsenceReportEvent;
import ru.protei.portal.core.model.dao.PersonAbsenceDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.PersonNotifierDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.report.absence.ReportAbsence;
import ru.protei.portal.core.service.events.EventPublisherService;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

public class AbsenceServiceImpl implements AbsenceService {

    @Autowired
    PersonAbsenceDAO personAbsenceDAO;

    @Autowired
    PersonDAO personDAO;

    @Autowired
    PersonNotifierDAO personNotifierDAO;

    @Autowired
    ReportAbsence reportAbsence;

    @Autowired
    HttpServletResponse response;

    @Autowired
    EventPublisherService publisherService;

    private final static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    private static Logger log = LoggerFactory.getLogger(AbsenceServiceImpl.class);

    @Override
    public Result<List<PersonAbsence>> getAbsences(AuthToken token, AbsenceQuery query) {
        List<PersonAbsence> result = personAbsenceDAO.listByQuery(query);
        return ok(result);
    }

    @Override
    public Result<PersonAbsence> getAbsence(AuthToken token, Long id) {
        PersonAbsence absence = personAbsenceDAO.get(id);

        if (absence == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        return ok(absence);
    }

    @Override
    public Result<Long> createAbsence(AuthToken token, PersonAbsence absence) {

        if (!validateFields(absence)) {
            return error( En_ResultStatus.INCORRECT_PARAMS);
        }

        if (hasAbsenceIntersections(absence.getPersonId(), absence.getFromTime(), absence.getTillTime(), absence.getId())) {
            return error(En_ResultStatus.ABSENCE_HAS_INTERSECTIONS);
        }

        absence.setCreated(new Date());
        absence.setCreatorId(token.getPersonId());

        Long absenceId = personAbsenceDAO.persist(absence);

        if (absenceId == null) {
            return error(En_ResultStatus.NOT_CREATED);
        }

        Person initiator = personDAO.get(token.getPersonId());
        PersonAbsence newState = personAbsenceDAO.get(absenceId);

        return ok(absenceId)
                .publishEvent(new AbsenceNotificationEvent(
                        this,
                        EventAction.CREATED,
                        initiator,
                        null,
                        newState,
                        getAbsenceNotifiers(newState)));
    }

    @Override
    public Result<Long> updateAbsence(AuthToken token, PersonAbsence absence) {

        if (!validateFields(absence) || absence.getId() == null) {
            return error( En_ResultStatus.INCORRECT_PARAMS);
        }

        PersonAbsence oldState = personAbsenceDAO.get(absence.getId());
        if (oldState == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (hasAbsenceIntersections(absence.getPersonId(), absence.getFromTime(), absence.getTillTime(), absence.getId())) {
            return error(En_ResultStatus.ALREADY_EXIST);
        }

        if (!personAbsenceDAO.merge(absence)) {
            return error(En_ResultStatus.NOT_UPDATED);
        }

        Person initiator = personDAO.get(token.getPersonId());
        PersonAbsence newState = personAbsenceDAO.get(absence.getId());

        return ok(absence.getId())
                .publishEvent(new AbsenceNotificationEvent(
                        this,
                        EventAction.UPDATED,
                        initiator,
                        oldState,
                        newState,
                        getAbsenceNotifiers(newState)));
    }

    @Override
    public Result<Boolean> removeAbsence(AuthToken token, Long absenceId) {

        if (absenceId == null) {
            return error( En_ResultStatus.INCORRECT_PARAMS);
        }

        PersonAbsence absence = personAbsenceDAO.get(absenceId);
        if (absence == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (!personAbsenceDAO.removeByKey(absenceId)) {
            error(En_ResultStatus.NOT_REMOVED);
        }

        Person initiator = personDAO.get(token.getPersonId());

        return ok(true).publishEvent(new AbsenceNotificationEvent(
                this,
                EventAction.REMOVED,
                initiator,
                null,
                absence,
                getAbsenceNotifiers(absence)));
    }

    @Override
    public Result<Boolean> completeAbsence(AuthToken token, Long absenceId) {

        if (absenceId == null) {
            return error( En_ResultStatus.INCORRECT_PARAMS);
        }

        PersonAbsence oldState = personAbsenceDAO.get(absenceId);
        if (oldState == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        PersonAbsence newState = new PersonAbsence(oldState);
        newState.setTillTime(new Date());
        if (!personAbsenceDAO.partialMerge(newState, "till_time")) {
            error(En_ResultStatus.NOT_UPDATED);
        }

        Person initiator = personDAO.get(token.getPersonId());

        return ok(true)
                .publishEvent(new AbsenceNotificationEvent(
                        this,
                        EventAction.UPDATED,
                        initiator,
                        oldState,
                        newState,
                        getAbsenceNotifiers(newState)));
    }


    @Override
    public Result createReport(AuthToken token, String name, AbsenceQuery query) {

        if (HelperFunc.isEmpty(name) || query == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        processReport(token, name, query);

        return ok();
    }

    @Async(BACKGROUND_TASKS)
    protected void processReport(AuthToken token, String name, AbsenceQuery query) {

        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            if (reportAbsence.writeReport(buffer, query, dateFormat)) {

                Person initiator = personDAO.get(token.getPersonId());
                publisherService.publishEvent(new AbsenceReportEvent(
                        this,
                        initiator,
                        name,
                        new ByteArrayInputStream(buffer.toByteArray())));
            }
        } catch (Exception e) {
            log.error("createReport(): uncaught exception", e);
        }
    }

    private boolean validateFields(PersonAbsence absence) {
        if (absence == null) {
            return false;
        }
        if (absence.getReason() == null) {
            return false;
        }
        if (absence.getPersonId() == null) {
            return false;
        }
        if (absence.getFromTime() == null) {
            return false;
        }
        if (absence.getTillTime() == null) {
            return false;
        }
        if (absence.getFromTime().after(absence.getTillTime())) {
            return false;
        }
        if (absence.getFromTime().equals(absence.getTillTime())) {
            return false;
        }

        return true;
    }

    private boolean hasAbsenceIntersections(Long employeeId, Date dateFrom, Date dateTill, Long excludeId) {
        return stream(personAbsenceDAO.listByEmployeeAndDateBounds(
                employeeId,
                dateFrom,
                dateTill
        )).anyMatch(r -> excludeId != r.getId());
    }

    private Set<Person> getAbsenceNotifiers(PersonAbsence absence) {
        return personNotifierDAO.getByPersonId(absence.getPersonId()).stream().map(PersonNotifier::getNotifier).collect(Collectors.toSet());
    }
}
