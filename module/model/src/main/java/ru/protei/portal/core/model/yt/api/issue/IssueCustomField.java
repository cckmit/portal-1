package ru.protei.portal.core.model.yt.api.issue;

import ru.protei.portal.core.model.yt.api.YoutrackObject;
import ru.protei.portal.core.model.yt.api.project.ProjectCustomField;
import ru.protei.portal.core.model.yt.api.value.FieldValue;

public abstract class IssueCustomField extends YoutrackObject {
    public ProjectCustomField projectCustomField;

    public String name;

    public abstract String getValue();

    @Override
    public String toString() {
        return "IssueCustomField{" +
                "$type='" + $type + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", projectCustomField=" + projectCustomField +
                '}';
    }
}
