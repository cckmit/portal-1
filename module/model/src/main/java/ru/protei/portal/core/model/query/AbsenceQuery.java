package ru.protei.portal.core.model.query;


import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.Date;
import java.util.Set;

public class AbsenceQuery extends BaseQuery {

    private Set<Long> employeeIds;
    private Date fromTime;
    private Date tillTime;
    private Set<En_AbsenceReason> reasons;

    public AbsenceQuery() {
        super (null, En_SortField.from_time, En_SortDir.ASC);
    }

    public AbsenceQuery(Set<Long> employeeIds) {
        super (null, En_SortField.from_time, En_SortDir.ASC);
        this.employeeIds = employeeIds;
    }

    public AbsenceQuery(Set<Long> employeeIds, Date fromTime, Date tillTime, Set<En_AbsenceReason> reasons) {
        super (null, En_SortField.from_time, En_SortDir.ASC);
        this.employeeIds = employeeIds;
        this.fromTime = fromTime;
        this.tillTime = tillTime;
        this.reasons = reasons;
    }

    public Set<Long> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(Set<Long> employeeIds) {
        this.employeeIds = employeeIds;
    }

    public Date getFromTime() {
        return fromTime;
    }

    public void setFromTime(Date fromTime) {
        this.fromTime = fromTime;
    }

    public Date getTillTime() {
        return tillTime;
    }

    public void setTillTime(Date tillTime) {
        this.tillTime = tillTime;
    }

    public Set<En_AbsenceReason> getReasons() {
        return reasons;
    }

    public void setReasons(Set<En_AbsenceReason> reasons) {
        this.reasons = reasons;
    }

    @Override
    public String toString() {
        return "AbsenceQuery{" +
                "employeeIds=" + employeeIds +
                ", fromTime=" + fromTime +
                ", tillTime=" + tillTime +
                ", reasons=" + reasons +
                ", searchString='" + searchString + '\'' +
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}
