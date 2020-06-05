package ru.protei.portal.core.model.query;

import java.util.Date;

public class PlanQuery extends BaseQuery{

    private String name;
    private Date created;
    private Long creatorId;
    private Date startDateFrom;
    private Date startDateTo;
    private Date finishDateFrom;
    private Date finishDateTo;
    private Long issueId;
    private Long issueNumber;

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

    public Date getStartDateFrom() {
        return startDateFrom;
    }

    public void setStartDateFrom(Date startDateFrom) {
        this.startDateFrom = startDateFrom;
    }

    public Date getStartDateTo() {
        return startDateTo;
    }

    public void setStartDateTo(Date startDateTo) {
        this.startDateTo = startDateTo;
    }

    public Date getFinishDateFrom() {
        return finishDateFrom;
    }

    public void setFinishDateFrom(Date finishDateFrom) {
        this.finishDateFrom = finishDateFrom;
    }

    public Date getFinishDateTo() {
        return finishDateTo;
    }

    public void setFinishDateTo(Date finishDateTo) {
        this.finishDateTo = finishDateTo;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(Long issueNumber) {
        this.issueNumber = issueNumber;
    }
}
