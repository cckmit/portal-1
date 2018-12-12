package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_ReportStatus;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;

@JdbcEntity(table = "report")
public class Report implements Removable, Downloadable, Refreshable, Serializable {

    /**
     * Уникальный идентификатор отчета
     * Идентификатор содержимого файла в хранилище (Имеет смысл только при En_ReportStatus.READY)
     */
    @JdbcId(name = "id")
    private Long id;

    /**
     * Название отчета
     */
    @JdbcColumn(name = "name")
    private String name;

    /**
     * Тип отчета
     */
    @JdbcColumn(name = "type")
    @JdbcEnumerated(EnumType.STRING)
    private En_ReportType reportType;

    /**
     * Текущее состояние отчета
     */
    @JdbcColumn(name = "status")
    @JdbcEnumerated(EnumType.STRING)
    private En_ReportStatus status;

    /**
     * Фильтр по обращениям
     */
    @JdbcColumn(name = "case_query", converterType = ConverterType.JSON)
    private CaseQuery caseQuery;

    /**
     * Фильтр по комментариям
     */
    @JdbcColumn(name = "case_comment_query", converterType = ConverterType.JSON)
    private CaseCommentQuery caseCommentQuery;

    /**
     * Профиль пользователя, создавшего отчет
     */
    @JdbcColumn(name = "creator")
    private Long creatorId;

    @JdbcJoinedObject(localColumn = "creator", remoteColumn = "id")
    private Person creator;

    /**
     * Дата запроса отчета
     */
    @JdbcColumn(name = "created")
    private Date created;

    /**
     * Дата последней смены состояния отчета
     */
    @JdbcColumn(name = "modified")
    private Date modified;

    /**
     * Язык отчета
     */
    @JdbcColumn
    private String locale;

    @Override
    public boolean isAllowedRemove() {
        return status != En_ReportStatus.PROCESS;
    }

    @Override
    public boolean isAllowedDownload() {
        return status == En_ReportStatus.READY;
    }

    @Override
    public boolean isAllowedRefresh() {
        return status == En_ReportStatus.ERROR;
    }

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

    public CaseQuery getCaseQuery() {
        return caseQuery;
    }

    public void setCaseQuery(CaseQuery filter) {
        this.caseQuery = filter;
    }

    public CaseCommentQuery getCaseCommentQuery() {
        return caseCommentQuery;
    }

    public void setCaseCommentQuery(CaseCommentQuery caseCommentQuery) {
        this.caseCommentQuery = caseCommentQuery;
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

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", reportType=" + reportType +
                ", status=" + status +
                ", caseQuery=" + caseQuery +
                ", caseCommentQuery=" + caseCommentQuery +
                ", creatorId=" + creatorId +
                ", creator=" + creator +
                ", created=" + created +
                ", modified=" + modified +
                ", locale='" + locale + '\'' +
                '}';
    }
}
