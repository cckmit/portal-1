package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.Date;
import java.util.List;

/**
 * Created by michael on 05.07.16.
 */
public interface PersonAbsenceDAO extends PortalBaseDAO<PersonAbsence> {

    List<PersonAbsence> listByEmployeeAndDateBounds(Long absenceId, Date from, Date till);

    PersonAbsence currentAbsence(Long employeeId);

    @SqlConditionBuilder
    SqlCondition createSqlCondition(AbsenceQuery query);
}
