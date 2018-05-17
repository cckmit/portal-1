package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_ReportStatus;

import java.util.Date;
import java.util.List;

public class ReportQuery extends BaseQuery {

    private List<En_ReportStatus> statuses;

    private String locale;

    private Date fromDate;

    private Date toDate;

    public ReportQuery() {}

    public ReportQuery(List<En_ReportStatus> statuses, String name, String locale, Date fromDate, Date toDate) {
        this(statuses, name, locale, fromDate, toDate, 0, -1);
    }

    public ReportQuery(List<En_ReportStatus> statuses, String name, String locale, Date fromDate, Date toDate, int offset, int limit) {
        super(name, null, null);
        this.statuses = statuses;
        this.locale = locale;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.offset = offset;
        this.limit = limit;
    }

    public List<En_ReportStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<En_ReportStatus> statuses) {
        this.statuses = statuses;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    @Override
    public String toString () {
        return "ReportQuery{" +
                "statuses=" + statuses +
                ", name=" + searchString +
                ", locale=" + locale +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", offset=" + offset +
                ", limit=" + limit +
                '}';
    }
}
