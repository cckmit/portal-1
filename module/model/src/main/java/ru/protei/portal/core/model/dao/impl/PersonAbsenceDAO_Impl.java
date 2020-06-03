package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.PersonAbsenceDAO;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static ru.protei.winter.jdbc.JdbcHelper.makeSqlStringCollection;

/**
 * Created by michael on 05.07.16.
 */
public class PersonAbsenceDAO_Impl extends PortalBaseJdbcDAO<PersonAbsence> implements PersonAbsenceDAO {

    @Override
    public List<PersonAbsence> listByEmployeeAndDateBounds(Long employeeId, Date from, Date till) {
        return getListByCondition("person_absence.person_id = ? AND (" +
                "(? < person_absence.from_time AND person_absence.from_time < ?) OR " +
                "(? < person_absence.till_time AND person_absence.till_time < ?) OR " +
                "(person_absence.from_time < ? AND ? < person_absence.till_time) OR " +
                "(person_absence.from_time < ? AND ? < person_absence.till_time) OR " +
                "(person_absence.from_time = ? AND person_absence.till_time = ?)" +
                ")", employeeId, from, till, from, till, from, from, till, till, from, till);
    }

    @Override
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(AbsenceQuery query) {
        return new SqlCondition().build((condition, args) -> {

            condition.append("1=1");

            if (query == null) {
                return;
            }

            if (query.getEmployeeIds() != null) {
                condition.append(" and person_absence.person_id in " + HelperFunc.makeInArg(query.getEmployeeIds(), false));
            }

            if (query.getFromTime() != null) {
                condition.append(" and (person_absence.from_time >= ? or person_absence.till_time >= ?)");
                args.add(query.getFromTime());
                args.add(query.getFromTime());
            }

            if (query.getTillTime() != null) {
                condition.append(" and (person_absence.till_time <= ? or person_absence.from_time <= ?)");
                args.add(query.getTillTime());
                args.add(query.getTillTime());
            }
        });
    }

}
