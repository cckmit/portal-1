package ru.protei.portal.core.model.yt.api.issue;

import ru.protei.portal.core.model.yt.api.value.FieldValue;

public class SingleEnumIssueCustomField extends IssueCustomField {

    FieldValue value;

    @Override
    public String getValue() {
        return value == null ? null : value.getValue();
    }
}
