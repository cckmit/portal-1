package ru.protei.portal.core.model.youtrack.dto.activity.customfield;

import ru.protei.portal.core.model.youtrack.annotation.YtDtoFieldCustomSubclasses;
import ru.protei.portal.core.model.youtrack.dto.YtDto;
import ru.protei.portal.core.model.youtrack.dto.activity.YtActivityItem;
import ru.protei.portal.core.model.youtrack.dto.bundleelemenet.YtBundleElement;

import java.util.List;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-CustomFieldActivityItem.html
 */
public class YtCustomFieldActivityItem extends YtActivityItem {

    @YtDtoFieldCustomSubclasses({ YtBundleElement.class })
    public List<YtDto> removed;
    @YtDtoFieldCustomSubclasses({ YtBundleElement.class })
    public List<YtDto> added;

    @Override
    public String toString() {
        return "YtCustomFieldActivityItem{" +
                "removed=" + removed +
                ", added=" + added +
                ", field=" + field +
                ", author=" + author +
                ", timestamp=" + timestamp +
                '}';
    }
}
