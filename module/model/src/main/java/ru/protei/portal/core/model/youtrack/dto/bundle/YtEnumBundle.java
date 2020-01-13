package ru.protei.portal.core.model.youtrack.dto.bundle;

import ru.protei.portal.core.model.youtrack.dto.bundleelemenet.YtEnumBundleElement;

import java.util.List;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-EnumBundle.html */
public class YtEnumBundle extends YtBaseBundle {

    public List<YtEnumBundleElement> values;

    @Override
    public String toString() {
        return "YtEnumBundle{" +
                "values=" + values +
                ", isUpdateable=" + isUpdateable +
                '}';
    }
}
