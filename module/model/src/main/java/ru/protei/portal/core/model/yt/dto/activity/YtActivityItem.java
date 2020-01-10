package ru.protei.portal.core.model.yt.dto.activity;

import ru.protei.portal.core.model.yt.dto.YtDto;
import ru.protei.portal.core.model.yt.dto.filterfield.YtFilterField;
import ru.protei.portal.core.model.yt.dto.user.YtUser;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-ActivityItem.html */
public abstract class YtActivityItem extends YtDto {

    public Long timestamp;
    public YtUser author;
    public YtFilterField field;
    // public abstract removed;
    // public abstract added;
}
