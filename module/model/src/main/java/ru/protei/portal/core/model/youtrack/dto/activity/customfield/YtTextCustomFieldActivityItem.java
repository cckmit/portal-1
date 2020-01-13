package ru.protei.portal.core.model.youtrack.dto.activity.customfield;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-TextCustomFieldActivityItem.html */
public class YtTextCustomFieldActivityItem extends YtCustomFieldActivityItem {

    public String removed;
    public String added;
    public String markup;

    @Override
    public String toString() {
        return "YtTextCustomFieldActivityItem{" +
                "removed=" + removed +
                ", added=" + added +
                ", field=" + field +
                ", author=" + author +
                ", timestamp=" + timestamp +
                '}';
    }
}
