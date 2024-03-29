package ru.protei.portal.core.model.youtrack.dto.bundleelemenet;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-EnumBundleElement.html
 */
public class YtEnumBundleElement extends YtLocalizableBundleElement {

    @Override
    public String toString() {
        return "YtEnumBundleElement{" +
                "localizedName='" + localizedName + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", archived=" + archived + '\'' +
                '}';
    }
}
