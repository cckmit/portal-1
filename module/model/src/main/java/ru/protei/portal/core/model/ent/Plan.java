package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.List;

@JdbcEntity(table = "plan")
public class Plan extends AuditableObject {
    public static final String AUDIT_TYPE = "Plan";

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "name")
    private String name;

    @JdbcColumn(name = "created")
    private Date created;

    @JdbcColumn(name = "creator_id")
    private Long creatorId;

    @JdbcColumn(name = "date_from")
    private Date dateFrom;

    @JdbcColumn(name = "date_to")
    private Date dateTo;

    @JdbcManyToMany(linkTable = "plan_to_case_object", localLinkColumn = "plan_id", remoteLinkColumn = "case_object_id")
    public List<CaseShortView> issueList;

    @JdbcOneToMany(localColumn = "id", remoteColumn = "plan_id")
    public List<PlanToCaseObject> issueOrderList;

    @Override
    public String getAuditType() {
        return AUDIT_TYPE;
    }

    @Override
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public List<CaseShortView> getIssueList() {
        return issueList;
    }

    public void setIssueList(List<CaseShortView> issueList) {
        this.issueList = issueList;
    }

    public List<PlanToCaseObject> getIssueOrderList() {
        return issueOrderList;
    }

    public void setIssueOrderList(List<PlanToCaseObject> issueOrderList) {
        this.issueOrderList = issueOrderList;
    }

    @Override
    public String toString() {
        return "Plan{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", created=" + created +
                ", creatorId=" + creatorId +
                ", dateFrom=" + dateFrom +
                ", dateTo=" + dateTo +
                ", issueList=" + issueList +
                ", issueOrderList=" + issueOrderList +
                '}';
    }
}
