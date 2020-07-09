package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.AbsenceNotificationEvent;
import ru.protei.portal.core.event.EventAction;
import ru.protei.portal.core.model.dao.PersonAbsenceDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.PersonNotifierDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.AbsenceQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

public class AbsenceServiceImpl implements AbsenceService {

    @Autowired
    PersonAbsenceDAO personAbsenceDAO;

    @Autowired
    PersonDAO personDAO;

    @Autowired
    PersonNotifierDAO personNotifierDAO;

    @Autowired
    ReportControlService reportControlService;

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

        if (isAbsenceFinished(absence)) {
            return error(En_ResultStatus.ABSENCE_FINISHED);
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

        if (isAbsenceFinished(absence)) {
            return error(En_ResultStatus.ABSENCE_FINISHED);
        }

        if (hasAbsenceIntersections(absence.getPersonId(), absence.getFromTime(), absence.getTillTime(), absence.getId())) {
            return error(En_ResultStatus.ABSENCE_HAS_INTERSECTIONS);
        }

        if (!personAbsenceDAO.partialMerge(absence, "from_time", "till_time", "user_comment")) {
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
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        PersonAbsence oldState = personAbsenceDAO.get(absenceId);
        if (oldState == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (!isCurrentAbsence(oldState)) {
            return error(En_ResultStatus.NOT_CURRENT_ABSENCE);
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

        if (query == null || query.getFromTime() == null || query.getTillTime() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        String title = HelperFunc.isEmpty(name) ? ("Отсутствия от " + dateFormat.format(new Date())) : name.trim();
        Person initiator = personDAO.get(token.getPersonId());

        reportControlService.processAbsenceReport(initiator, title, query);

        return ok();
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

    private boolean isAbsenceFinished(PersonAbsence absence) {
        Date now = new Date();
        return now.after(absence.getTillTime());
    }

    private boolean isCurrentAbsence(PersonAbsence absence) {
        Date now = new Date();
        return now.after(absence.getFromTime()) && now.before(absence.getTillTime());
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
