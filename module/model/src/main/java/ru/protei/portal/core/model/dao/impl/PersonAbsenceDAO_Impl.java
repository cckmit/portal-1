package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.PersonAbsenceDAO;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.struct.Interval;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.DateRangeUtils.makeInterval;
import static ru.protei.portal.core.model.util.DateUtils.resetSeconds;

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
    public PersonAbsence currentAbsence(Long employeeId) {
        return getByCondition("person_absence.person_id = ? and person_absence.from_time <= ? and person_absence.till_time >= ?" +
                        " and person_absence.reason_id in " + HelperFunc.makeInArg(actualReasons(), false),
                employeeId, resetSeconds(new Date()), resetSeconds(new Date()));
    }

    @Override
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(AbsenceQuery query) {
        return new SqlCondition().build((condition, args) -> {

            condition.append("1=1");

            if (query == null) {
                return;
            }

            if (CollectionUtils.isNotEmpty(query.getEmployeeIds())) {
                condition.append(" and person_absence.person_id in " + HelperFunc.makeInArg(query.getEmployeeIds(), false));
            }

            Interval dateRange = makeInterval(query.getDateRange());
            if ( dateRange != null ) {
                if (dateRange.from != null) {
                    condition.append(" and (person_absence.from_time >= ? or person_absence.till_time >= ?)");
                    args.add(dateRange.from);
                    args.add(dateRange.from);
                }
                if (dateRange.to != null) {
                    condition.append(" and (person_absence.till_time < ? or person_absence.from_time < ?)");
                    args.add(dateRange.to);
                    args.add(dateRange.to);
                }
            }

            if (CollectionUtils.isNotEmpty(query.getReasons())) {
                Set<Integer> reasons = query.getReasons().stream()
                        .map(En_AbsenceReason::getId)
                        .collect(Collectors.toSet());
                condition.append(" and person_absence.reason_id in " + HelperFunc.makeInArg(reasons, false));
            }
        });
    }

    private Set<Integer> actualReasons() {
        return Arrays.stream(En_AbsenceReason.values())
                .filter(En_AbsenceReason::isActual)
                .map(En_AbsenceReason::getId)
                .collect(Collectors.toSet());
    }
}
