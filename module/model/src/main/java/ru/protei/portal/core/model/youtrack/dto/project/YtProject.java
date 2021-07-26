package ru.protei.portal.core.model.youtrack.dto.project;

import ru.protei.portal.core.model.youtrack.annotation.YtAlwaysInclude;
import ru.protei.portal.core.model.youtrack.dto.YtDto;
import ru.protei.portal.core.model.youtrack.dto.customfield.project.YtProjectCustomField;

import java.util.List;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-Project.html
 */
public class YtProject extends YtDto {

    public String name;
    @YtAlwaysInclude
    public String shortName;
    public String description;
    public Boolean archived;
    public String iconUrl;
    public List<YtProjectCustomField> customFields;

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
