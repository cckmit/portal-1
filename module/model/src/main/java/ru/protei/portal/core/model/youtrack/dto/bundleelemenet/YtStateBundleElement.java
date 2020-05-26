package ru.protei.portal.core.model.youtrack.dto.bundleelemenet;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-StateBundleElement.html
 */
public class YtStateBundleElement extends YtLocalizableBundleElement {

    public Boolean isResolved;

    @Override
    public String toString() {
        return "YtStateBundleElement{" +
                "isResolved=" + isResolved +
                ", localizedName='" + localizedName + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", archived=" + archived + '\'' +
                '}';
    }
}
