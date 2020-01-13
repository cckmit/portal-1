package ru.protei.portal.core.model.youtrack.dto.activity.singlevalue;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-TextMarkupActivityItem.html
 */
public class YtTextMarkupActivityItem extends YtSimpleValueActivityItem {

    public String removed;
    public String added;
    public String markup;

    @Override
    public String toString() {
        return "YtTextMarkupActivityItem{" +
                "removed=" + removed +
                ", added=" + added +
                ", field=" + field +
                ", author=" + author +
                ", timestamp=" + timestamp +
                '}';
    }
}
