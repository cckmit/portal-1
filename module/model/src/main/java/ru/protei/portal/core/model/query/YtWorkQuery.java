package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.struct.DateRange;

import java.util.Objects;

public class YtWorkQuery extends BaseQuery {
    private DateRange dateRange;

    public YtWorkQuery() {}

    public YtWorkQuery(String searchString, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }

    @Override
    public boolean isParamsPresent() {
        return super.isParamsPresent() ||
                dateRange != null;
    }

    @Override
    public String toString() {
        return "YtWorkQuery{" +
                ", dateRange=" + dateRange +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YtWorkQuery)) return false;
        YtWorkQuery that = (YtWorkQuery) o;
        return Objects.equals(dateRange, that.dateRange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateRange);
    }
}
