package ru.protei.portal.core.model.youtrack.dto.bundle;

import ru.protei.portal.core.model.youtrack.dto.bundleelemenet.YtBundleElement;

import java.util.List;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-BaseBundle.html
 */
public abstract class YtBaseBundle extends YtBundle {
    public List<YtBundleElement> values;
}
