package ru.protei.portal.core.model.yt.api.bundle;

import ru.protei.portal.core.model.yt.api.bundleelemenet.YtBundleElement;

import java.util.List;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-BaseBundle.html */
public abstract class YtBaseBundle extends YtBundle {
    public List<YtBundleElement> values;
}
