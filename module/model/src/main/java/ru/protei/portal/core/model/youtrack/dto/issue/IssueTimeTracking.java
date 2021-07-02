package ru.protei.portal.core.model.youtrack.dto.issue;

import ru.protei.portal.core.model.youtrack.annotation.YtEntityName;
import ru.protei.portal.core.model.youtrack.dto.YtDto;

import java.util.List;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-Issue.html
 */
@YtEntityName("IssueTimeTracking")
public class IssueTimeTracking extends YtDto {

    public List<IssueWorkItem> workItems;

    @Override
    public String toString() {
        return "IssueTimeTracking{" +
                "id='" + id + '\'' +
                ", $type='" + $type + '\'' +
                ", workItems=" + workItems +
                '}';
    }
}
