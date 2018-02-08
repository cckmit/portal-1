package ru.protei.portal.redmine.config;

import java.util.Date;

public class RedmineProjectConfig {
    private int projectId;
    private Date lastCreatedIssueDate;
    private Date lastUpdatedIssueDate;

    public int getProjectId() {
        return projectId;
    }

    public Date getLastCreatedIssueDate() {
        return lastCreatedIssueDate;
    }

    public void setLastCreatedIssueDate(Date lastCreatedIssueDate) {
        this.lastCreatedIssueDate = lastCreatedIssueDate;
    }

    public Date getLastUpdatedIssueDate() {
        return lastUpdatedIssueDate;
    }

    public void setLastUpdatedIssueDate(Date lastUpdatedIssueDate) {
        this.lastUpdatedIssueDate = lastUpdatedIssueDate;
    }
}
