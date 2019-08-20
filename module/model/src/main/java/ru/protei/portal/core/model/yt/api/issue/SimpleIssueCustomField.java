package ru.protei.portal.core.model.yt.api.issue;

public class SimpleIssueCustomField extends IssueCustomField {
    @Override
    public String getValue() {
        return value;
    }

    private String value;
}
