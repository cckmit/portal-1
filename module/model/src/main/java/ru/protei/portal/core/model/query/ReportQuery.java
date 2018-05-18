package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_ReportStatus;

import java.util.Date;
import java.util.List;

public class ReportQuery extends BaseQuery {

    private List<En_ReportStatus> statuses;

    private String locale;

    private Date fromCreated;

    private Date toCreated;

    private Date fromModified;

    private Date toModified;

    public ReportQuery() {}

    public ReportQuery(List<En_ReportStatus> statuses, String name, String locale, Date fromCreated, Date toCreated) {
        this(statuses, name, locale, fromCreated, toCreated, 0, -1);
    }

    public ReportQuery(List<En_ReportStatus> statuses, String name, String locale, Date fromCreated, Date toCreated, int offset, int limit) {
        super(name, null, null);
        this.statuses = statuses;
        this.locale = locale;
        this.fromCreated = fromCreated;
        this.toCreated = toCreated;
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

    public Date getFromCreated() {
        return fromCreated;
    }

    public void setFromCreated(Date fromCreated) {
        this.fromCreated = fromCreated;
    }

    public Date getToCreated() {
        return toCreated;
    }

    public void setToCreated(Date toCreated) {
        this.toCreated = toCreated;
    }

    public Date getFromModified() {
        return fromModified;
    }

    public void setFromModified(Date fromModified) {
        this.fromModified = fromModified;
    }

    public Date getToModified() {
        return toModified;
    }

    public void setToModified(Date toModified) {
        this.toModified = toModified;
    }

    @Override
    public String toString () {
        return "ReportQuery{" +
                "statuses=" + statuses +
                ", name=" + searchString +
                ", locale=" + locale +
                ", fromCreated=" + fromCreated +
                ", toCreated=" + toCreated +
                ", fromModified=" + fromModified +
                ", toModified=" + toModified +
                ", offset=" + offset +
                ", limit=" + limit +
                '}';
    }
}
