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

    @JdbcColumn(name = "start_date")
    private Date startDate;

    @JdbcColumn(name = "finish_date")
    private Date finishDate;

    @JdbcManyToMany(linkTable = "plan_to_case_object", localLinkColumn = "plan_id", remoteLinkColumn = "case_object_id")
    private List<CaseShortView> issueList;

    @JdbcJoinedColumn(mappedColumn = "displayShortName", localColumn = "creator_id", remoteColumn = "id", table = "person")
    private String creatorShortName;

    private Long issuesCount;

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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public List<CaseShortView> getIssueList() {
        return issueList;
    }

    public void setIssueList(List<CaseShortView> issueList) {
        this.issueList = issueList;
    }

    public String getCreatorShortName() {
        return creatorShortName;
    }

    public void setCreatorShortName(String creatorShortName) {
        this.creatorShortName = creatorShortName;
    }

    public Long getIssuesCount() {
        return issuesCount;
    }

    public void setIssuesCount(Long issuesCount) {
        this.issuesCount = issuesCount;
    }
}
