package ru.protei.portal.core.model.yt.api.issue;

import ru.protei.portal.core.model.yt.api.CustomField;

public class IssueCustomField extends CustomField {
    public CustomField projectCustomField;
    public CustomField value;

    public CustomField getProjectCustomField() {
        return projectCustomField;
    }

    public void setProjectCustomField( CustomField projectCustomField ) {
        this.projectCustomField = projectCustomField;
    }

    public CustomField getValue() {
        return value;
    }

    public void setValue( CustomField value ) {
        this.value = value;
    }
}
