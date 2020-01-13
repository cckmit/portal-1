package ru.protei.portal.core.model.youtrack.dto.bundle;

import ru.protei.portal.core.model.youtrack.dto.bundleelemenet.YtStateBundleElement;

import java.util.List;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-StateBundle.html */
public class YtStateBundle extends YtBaseBundle {

    public List<YtStateBundleElement> values;

    @Override
    public String toString() {
        return "YtStateBundle{" +
                "values=" + values +
                ", isUpdateable=" + isUpdateable +
                '}';
    }
}
