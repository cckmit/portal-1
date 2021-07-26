package ru.protei.portal.core.model.struct;

public class ReportYtWorkInfo {
    final String email;
    final String issue;
    final String customer;
    final Long spentTime;
    final String project;

    public ReportYtWorkInfo(String email, String issue, String customer, Long spentTime, String project) {
        this.email = email;
        this.issue = issue;
        this.customer = customer;
        this.spentTime = spentTime;
        this.project = project;
    }

    public String getEmail() {
        return email;
    }

    public String getIssue() {
        return issue;
    }

    public String getCustomer() {
        return customer;
    }

    public Long getSpentTime() {
        return spentTime;
    }

    public String getProject() {
        return project;
    }
}
