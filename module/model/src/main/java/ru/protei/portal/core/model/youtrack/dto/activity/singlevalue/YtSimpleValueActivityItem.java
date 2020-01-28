package ru.protei.portal.core.model.youtrack.dto.activity.singlevalue;

/**
 *  https://www.jetbrains.com/help/youtrack/standalone/api-entity-SimpleValueActivityItem.html
 */
public class YtSimpleValueActivityItem extends YtSingleValueActivityItem {

    public Object removed;
    public Object added;

    @Override
    public String toString() {
        return "YtSimpleValueActivityItem{" +
                "removed=" + removed +
                ", added=" + added +
                ", field=" + field +
                ", author=" + author +
                ", timestamp=" + timestamp +
                '}';
    }
}
