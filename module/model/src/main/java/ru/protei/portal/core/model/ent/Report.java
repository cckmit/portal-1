package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_ReportScheduledType;
import ru.protei.portal.core.model.dict.En_ReportStatus;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.core.model.dto.ReportCaseQuery;
import ru.protei.portal.core.model.dto.ReportContractQuery;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @see ReportCaseQuery
 * @see ReportContractQuery
 */
@JdbcEntity(table = "report")
public class Report implements Serializable {

    @JdbcId(name = "id")
    private Long id;

    @JdbcColumn(name = "name")
    private String name;

    @JdbcColumn(name = "type")
    @JdbcEnumerated(EnumType.STRING)
    private En_ReportType reportType;

    @JdbcColumn(name = Columns.STATUS)
    @JdbcEnumerated(EnumType.STRING)
    private En_ReportStatus status;

    @JdbcColumn(name = "case_query")
    private String query;

    @JdbcColumn(name = "creator")
    private Long creatorId;

    @JdbcJoinedObject(localColumn = "creator", remoteColumn = "id")
    private Person creator;

    @JdbcColumn(name = "created")
    private Date created;

    @JdbcColumn(name = Columns.MODIFIED)
    private Date modified;

    @JdbcColumn(name = "locale")
    private String locale;

    @JdbcColumn(name = "is_restricted")
    private boolean isRestricted;

    @JdbcColumn(name = "scheduled_type")
    @JdbcEnumerated(EnumType.STRING)
    private En_ReportScheduledType scheduledType;

    @JdbcColumn(name = "with_description")
    private boolean withDescription;

    @JdbcColumn(name = Columns.REMOVED)
    private boolean isRemoved = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public En_ReportType getReportType() {
        return reportType;
    }

    public void setReportType(En_ReportType reportType) {
        this.reportType = reportType;
    }

    public En_ReportStatus getStatus() {
        return status;
    }

    public void setStatus(En_ReportStatus status) {
        this.status = status;
    }

//    public CaseQuery getCaseQuery() {
//        return caseQuery;
//    }
//
//    public void setCaseQuery(CaseQuery filter) {
//        this.caseQuery = filter;
//    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Person getCreator() {
        return creator;
    }

    public void setCreator(Person creator) {
        this.creator = creator;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public boolean isRestricted() {
        return isRestricted;
    }

    public void setRestricted( boolean restricted ) {
        this.isRestricted = restricted;
    }

    public En_ReportScheduledType getScheduledType() {
        return scheduledType;
    }

    public void setScheduledType(En_ReportScheduledType scheduledType) {
        this.scheduledType = scheduledType;
    }

    public boolean isWithDescription() {
        return withDescription;
    }

    public void setWithDescription(boolean withDescription) {
        this.withDescription = withDescription;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void setRemoved(boolean removed) {
        isRemoved = removed;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", reportType=" + reportType +
                ", status=" + status +
                ", query=" + query +
                ", creatorId=" + creatorId +
                ", creator=" + creator +
                ", created=" + created +
                ", modified=" + modified +
                ", locale='" + locale + '\'' +
                ", isRestricted=" + isRestricted +
                ", scheduledType=" + scheduledType +
                ", withDescription=" + withDescription +
                ", isRemoved=" + isRemoved +
                '}';
    }

    public interface Columns{
        String STATUS = "status";
        String MODIFIED = "modified";
        String REMOVED = "is_removed";
    }

}
