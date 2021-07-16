package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

import java.util.Date;
import java.util.Objects;

public class ReportYtWorkQuery extends BaseQuery {
    private Date from;

    private Date to;

    public ReportYtWorkQuery() {}

    public ReportYtWorkQuery(String searchString, En_SortField sortField, En_SortDir sortDir ) {
        super(searchString, sortField, sortDir);
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }


    @Override
    public boolean isParamsPresent() {
        return super.isParamsPresent() ||
                from != null ||
                to != null;
    }

    @Override
    public String toString() {
        return "ReportYtWorkQuery{" +
                ", createdFrom=" + from +
                ", createdTo=" + to +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReportYtWorkQuery)) return false;
        ReportYtWorkQuery that = (ReportYtWorkQuery) o;
        return
                Objects.equals(from, that.from) &&
                Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
