package ru.protei.portal.core.model.query;


import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.Date;
import java.util.Set;

public class AbsenceQuery extends BaseQuery {

    private Date fromTime;
    private Date tillTime;
    private Set<Long> employeeIds;
    private Set<Integer> reasonIds;

    public AbsenceQuery() {
        super (null, En_SortField.from_time, En_SortDir.ASC);
    }

    public AbsenceQuery(Set<Long> employeeIds) {
        super (null, En_SortField.from_time, En_SortDir.ASC);
        this.employeeIds = employeeIds;
    }

    public AbsenceQuery(Date fromTime, Date tillTime) {
        super (null, En_SortField.from_time, En_SortDir.ASC);
        this.fromTime = fromTime;
        this.tillTime = tillTime;
    }

    public AbsenceQuery(Date fromTime, Date tillTime, Set<Long> employeeIds, Set<Integer> reasonIds) {
        super (null, En_SortField.from_time, En_SortDir.ASC);
        this.fromTime = fromTime;
        this.tillTime = tillTime;
        this.employeeIds = employeeIds;
        this.reasonIds = reasonIds;
    }

    public AbsenceQuery(Date fromTime, Date tillTime, Set<Long> employeeIds, Set<Integer> reasonIds, En_SortField sortField, En_SortDir sortDir) {
        super (null, sortField, sortDir);
        this.fromTime = fromTime;
        this.tillTime = tillTime;
        this.employeeIds = employeeIds;
        this.reasonIds = reasonIds;
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

    public Set<Long> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(Set<Long> employeeIds) {
        this.employeeIds = employeeIds;
    }

    public Set<Integer> getReasonIds() {
        return reasonIds;
    }

    public void setReasonIds(Set<Integer> reasonIds) {
        this.reasonIds = reasonIds;
    }

    @Override
    public String toString() {
        return "AbsenceQuery{" +
                "employeeIds=" + employeeIds +
                ", fromTime=" + fromTime +
                ", tillTime=" + tillTime +
                ", reasons=" + reasonIds +
                ", searchString='" + searchString + '\'' +
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}
