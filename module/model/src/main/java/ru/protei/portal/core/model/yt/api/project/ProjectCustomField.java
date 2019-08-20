package ru.protei.portal.core.model.yt.api.project;

import ru.protei.portal.core.model.yt.api.YoutrackObject;

public class ProjectCustomField extends YoutrackObject {
    public String emptyFieldText;
    public CustomField field;
    public String localizedName;

    @Override
    public String toString() {
        return "ProjectCustomField{" +
                "$type='" + $type + '\'' +
                ", id='" + id + '\'' +
                ", field=" + field +
                ", localizedName='" + localizedName + '\'' +
                ", emptyFieldText='" + emptyFieldText + '\'' +
                '}';
    }
}
