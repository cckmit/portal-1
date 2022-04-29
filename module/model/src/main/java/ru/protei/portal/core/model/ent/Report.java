package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.ReportCaseQuery;
import ru.protei.portal.core.model.dto.ReportContractQuery;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

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

    @JdbcColumn(name = "with_tags")
    private boolean withTags;

    @JdbcColumn(name = "with_linked_issues")
    private boolean withLinkedIssues;

    @JdbcColumn(name = Columns.REMOVED)
    private boolean isRemoved = false;

    @JdbcColumn(name = Columns.SYSTEM_ID)
    private String systemId;

    @JdbcColumn(name = "human_readable")
    private boolean isHumanReadable;

    @JdbcColumn(name = "with_deadline_and_work_trigger")
    private boolean withDeadlineAndWorkTrigger;

    @JdbcEnumerated(EnumType.STRING)
    @JdbcColumnCollection(name = "time_elapsed_groups", separator = ",")
    private Set<En_TimeElapsedGroup> timeElapsedGroups;

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

    public boolean isWithTags() {
        return withTags;
    }

    public void setWithTags(boolean withTags) {
        this.withTags = withTags;
    }

    public boolean isWithLinkedIssues() {
        return withLinkedIssues;
    }

    public void setWithLinkedIssues(boolean withLinkedIssues) {
        this.withLinkedIssues = withLinkedIssues;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void setRemoved(boolean removed) {
        isRemoved = removed;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public boolean isHumanReadable() {
        return isHumanReadable;
    }

    public void setHumanReadable(boolean humanReadable) {
        isHumanReadable = humanReadable;
    }

    public boolean isWithDeadlineAndWorkTrigger() {
        return withDeadlineAndWorkTrigger;
    }

    public void setWithDeadlineAndWorkTrigger(boolean withDeadlineAndWorkTrigger) {
        this.withDeadlineAndWorkTrigger = withDeadlineAndWorkTrigger;
    }

    public Set<En_TimeElapsedGroup> getTimeElapsedGroups() {
        return timeElapsedGroups;
    }

    public void setTimeElapsedGroups(Set<En_TimeElapsedGroup> timeElapsedGroups) {
        this.timeElapsedGroups = timeElapsedGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Objects.equals(id, report.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", reportType=" + reportType +
                ", status=" + status +
                ", query='" + query + '\'' +
                ", creatorId=" + creatorId +
                ", creator=" + creator +
                ", created=" + created +
                ", modified=" + modified +
                ", locale='" + locale + '\'' +
                ", isRestricted=" + isRestricted +
                ", scheduledType=" + scheduledType +
                ", withDescription=" + withDescription +
                ", withTags=" + withTags +
                ", withLinkedIssues=" + withLinkedIssues +
                ", isRemoved=" + isRemoved +
                ", systemId='" + systemId + '\'' +
                ", isHumanReadable=" + isHumanReadable +
                ", timeElapsedGroups=" + timeElapsedGroups +
                '}';
    }

    public interface Columns{
        String STATUS = "status";
        String MODIFIED = "modified";
        String REMOVED = "is_removed";
        String SYSTEM_ID = "system_id";
    }
}
