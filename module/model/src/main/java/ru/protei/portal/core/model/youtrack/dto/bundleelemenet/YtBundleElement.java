package ru.protei.portal.core.model.youtrack.dto.bundleelemenet;

import ru.protei.portal.core.model.youtrack.dto.YtDto;

/** https://www.jetbrains.com/help/youtrack/standalone/api-entity-BundleElement.html */
public abstract class YtBundleElement extends YtDto {
    public String name;
    public String description;
}
