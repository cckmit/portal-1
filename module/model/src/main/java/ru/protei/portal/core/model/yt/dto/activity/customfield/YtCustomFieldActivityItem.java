package ru.protei.portal.core.model.yt.dto.activity.customfield;

import ru.protei.portal.core.model.yt.annotation.YtDtoFieldSubclassesSpecifier;
import ru.protei.portal.core.model.yt.dto.YtDto;
import ru.protei.portal.core.model.yt.dto.activity.YtActivityItem;
import ru.protei.portal.core.model.yt.dto.bundleelemenet.YtBundleElement;

import java.util.List;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-CustomFieldActivityItem.html */
public class YtCustomFieldActivityItem extends YtActivityItem {

    @YtDtoFieldSubclassesSpecifier({ YtBundleElement.class })
    public List<YtDto> removed;
    @YtDtoFieldSubclassesSpecifier({ YtBundleElement.class })
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
