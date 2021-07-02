package ru.protei.portal.core.model.youtrack.dto.issue;

import ru.protei.portal.core.model.youtrack.annotation.YtEntityName;
import ru.protei.portal.core.model.youtrack.dto.YtDto;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-Issue.html
 */
@YtEntityName("WorkItemType")
public class WorkItemType extends YtDto {
    public String name;

    @Override
    public String toString() {
        return "WorkItemType{" +
                "id='" + id + '\'' +
                ", $type='" + $type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
