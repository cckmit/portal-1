package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.PersonAbsenceDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.query.AbsenceQuery;

import java.util.Date;
import java.util.List;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

public class AbsenceServiceImpl implements AbsenceService {

    @Autowired
    PersonAbsenceDAO personAbsenceDAO;

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

        if (checkExists(absence.getPersonId(), absence.getFromTime(), absence.getTillTime(), absence.getId())) {
            return error(En_ResultStatus.ALREADY_EXIST);
        }

        absence.setCreated(new Date());
        absence.setCreatorId(token.getPersonId());

        Long absenceId = personAbsenceDAO.persist(absence);

        if (absenceId == null)
            return error(En_ResultStatus.NOT_CREATED);

        return ok(absenceId);
    }

    @Override
    public Result<Long> updateAbsence(AuthToken token, PersonAbsence absence) {

        if (!validateFields(absence)) {
            return error( En_ResultStatus.INCORRECT_PARAMS);
        }

        if (checkExists(absence.getPersonId(), absence.getFromTime(), absence.getTillTime(), absence.getId())) {
            return error(En_ResultStatus.ALREADY_EXIST);
        }

        if (!personAbsenceDAO.merge(absence))
            return error(En_ResultStatus.NOT_UPDATED);

        return ok(absence.getId());
    }

    @Override
    public Result<Boolean> removeAbsence(AuthToken token, Long absenceId) {

        if (personAbsenceDAO.removeByKey(absenceId)) {
            return ok(true);
        }

        return error(En_ResultStatus.NOT_REMOVED);
    }

    private boolean validateFields(PersonAbsence absence) {
        return true;
    }

    private boolean checkExists(Long employeeId, Date dateFrom, Date dateTill, Long excludeId) {
        return stream(personAbsenceDAO.listByEmployeeAndDateBounds(
                employeeId,
                dateFrom,
                dateTill
        )).anyMatch(r -> excludeId != r.getId());
    }
}
