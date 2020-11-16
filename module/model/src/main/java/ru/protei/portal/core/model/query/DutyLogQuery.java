package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_DutyType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.core.model.view.filterwidget.FilterQuery;

import java.util.Set;

public class DutyLogQuery extends BaseQuery implements FilterQuery {

    private Set<Long> personIds;
    private Set<En_DutyType> types;
    private DateRange dateRange;

    public DutyLogQuery() {
    }

    public DutyLogQuery(DateRange dateRange, Set<Long> personIds, Set<En_DutyType> types, En_SortField sortField, En_SortDir sortDir) {
        super(null, sortField, sortDir);
        this.personIds = personIds;
        this.types = types;
        this.dateRange = dateRange;
    }

    public DutyLogQuery(DateRange dateRange) {
        super (null, En_SortField.duty_log_date_from, En_SortDir.ASC);
        this.dateRange = dateRange;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }

    public Set<Long> getPersonIds() {
        return personIds;
    }

    public void setPersonIds(Set<Long> personIds) {
        this.personIds = personIds;
    }

    public Set<En_DutyType> getTypes() {
        return types;
    }

    public void setTypes(Set<En_DutyType> types) {
        this.types = types;
    }

    @Override
    public String toString() {
        return "DutyLogQuery{" +
                "personIds=" + personIds +
                ", types=" + types +
                ", dateRange=" + dateRange +
                '}';
    }
}
