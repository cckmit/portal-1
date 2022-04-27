package ru.protei.portal.core.model.struct.reportytwork;

import java.util.Objects;

public class ReportYtWorkClassificationError implements ReportYtWorkRow {
    final String issue;
    final String customer;

    public ReportYtWorkClassificationError(String issue, String customer) {
        this.issue = issue;
        this.customer = customer;
    }

    public String getIssue() {
        return issue;
    }

    public String getCustomer() {
        return customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReportYtWorkClassificationError)) return false;
        ReportYtWorkClassificationError that = (ReportYtWorkClassificationError) o;
        return Objects.equals(issue, that.issue) && Objects.equals(customer, that.customer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(issue, customer);
    }

    @Override
    public String toString() {
        return "ReportYtWorkClassificationError{" +
                "issue='" + issue + '\'' +
                ", customer='" + customer + '\'' +
                '}';
    }
}
