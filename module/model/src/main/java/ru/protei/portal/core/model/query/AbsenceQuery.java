package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.core.model.view.filterwidget.FilterQuery;

import java.util.Objects;
import java.util.Set;

public class AbsenceQuery extends BaseQuery implements FilterQuery {

    private DateRange dateRange;
    private Set<Long> employeeIds;
    private Set<Integer> reasonIds;

    public AbsenceQuery() {
        super (null, En_SortField.absence_date, En_SortDir.ASC);
    }

    public AbsenceQuery(Set<Long> employeeIds) {
        super (null, En_SortField.absence_date, En_SortDir.ASC);
        this.employeeIds = employeeIds;
    }

    public AbsenceQuery(DateRange dateRange) {
        super (null, En_SortField.absence_date, En_SortDir.ASC);
        this.dateRange = dateRange;
    }

    public AbsenceQuery(DateRange dateRange, Set<Long> employeeIds, Set<Integer> reasonIds) {
        super (null, En_SortField.absence_date, En_SortDir.ASC);
        this.dateRange = dateRange;
        this.employeeIds = employeeIds;
        this.reasonIds = reasonIds;
    }

    public AbsenceQuery(DateRange dateRange, Set<Long> employeeIds, Set<Integer> reasonIds, En_SortField sortField, En_SortDir sortDir) {
        super (null, sortField, sortDir);
        this.dateRange = dateRange;
        this.employeeIds = employeeIds;
        this.reasonIds = reasonIds;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
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
                ", dateRange=" + dateRange +
                ", reasons=" + reasonIds +
                ", searchString='" + searchString + '\'' +
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbsenceQuery)) return false;
        AbsenceQuery that = (AbsenceQuery) o;
        return Objects.equals(dateRange, that.dateRange) &&
                Objects.equals(employeeIds, that.employeeIds) &&
                Objects.equals(reasonIds, that.reasonIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateRange, employeeIds, reasonIds);
    }
}
