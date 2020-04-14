package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.PersonAbsenceDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.PersonAbsence;

import java.util.Date;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class AbsenceServiceImpl implements AbsenceService {

    @Autowired
    PersonAbsenceDAO personAbsenceDAO;
    
    @Override
    public Result<Long> createAbsence(AuthToken token, PersonAbsence absence) {

        if (!validateFields(absence)) {
            return error( En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!checkExists(absence))
            return error(En_ResultStatus.ALREADY_EXIST);

        absence.setCreated(new Date());
        absence.setCreatorId(token.getPersonId());

        Long absenceId = personAbsenceDAO.persist(absence);

        if (absenceId == null)
            return error(En_ResultStatus.NOT_CREATED);

        return ok(absenceId);
    }

    private boolean validateFields(PersonAbsence absence) {
        return true;
    }

    private boolean checkExists(PersonAbsence absence) {
        return true;
    }
}
