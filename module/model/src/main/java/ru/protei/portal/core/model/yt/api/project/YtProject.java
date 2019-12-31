package ru.protei.portal.core.model.yt.api.project;

import ru.protei.portal.core.model.yt.api.YtDto;
import ru.protei.portal.core.model.yt.api.customfield.project.YtProjectCustomField;

import java.util.List;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-Project.html */
public class YtProject extends YtDto {

    public String name;
    public String shortName;
    public String description;
    public Boolean archived;
    public String iconUrl;
    public List<YtProjectCustomField> customFields;
//    public List<YtIssue> issues;

    @Override
    public String toString() {
        return "YtProject{" +
                "name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", description='" + description + '\'' +
                ", archived=" + archived +
                ", iconUrl='" + iconUrl + '\'' +
                ", customFields=" + customFields +
                '}';
    }
}
