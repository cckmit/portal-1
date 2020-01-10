package ru.protei.portal.core.model.yt.dto.bundleelemenet;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-StateBundleElement.html */
public class YtStateBundleElement extends YtLocalizableBundleElement {

    public Boolean isResolved;

    @Override
    public String toString() {
        return "YtStateBundleElement{" +
                "isResolved=" + isResolved +
                ", localizedName='" + localizedName + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
