package ru.protei.portal.core.model.youtrack.dto.activity;

import ru.protei.portal.core.model.youtrack.dto.YtDto;
import ru.protei.portal.core.model.youtrack.dto.filterfield.YtFilterField;
import ru.protei.portal.core.model.youtrack.dto.user.YtUser;

import java.util.Date;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-ActivityItem.html
 */
public abstract class YtActivityItem extends YtDto {

    public Date timestamp;
    public YtUser author;
    public YtFilterField field;
    // public abstract removed;
    // public abstract added;
}
