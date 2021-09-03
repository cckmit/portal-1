package ru.protei.portal.core.model.struct.reportytwork;

import java.util.Objects;

public class ReportYtWorkClassificationError implements ReportYtWorkRow {
    final String issue;

    public ReportYtWorkClassificationError(String issue) {
        this.issue = issue;
    }

    public String getIssue() {
        return issue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReportYtWorkClassificationError)) return false;
        ReportYtWorkClassificationError that = (ReportYtWorkClassificationError) o;
        return issue.equals(that.issue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(issue);
    }

    @Override
    public String toString() {
        return "ReportYtWorkClassificationError{" +
                "issue='" + issue + '\'' +
                '}';
    }
}
