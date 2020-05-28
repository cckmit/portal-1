package ru.protei.portal.core.model.youtrack.dto.bundleelemenet;

import ru.protei.portal.core.model.youtrack.dto.YtDto;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-BundleElement.html
 */
public abstract class YtBundleElement extends YtDto {
    public String name;
    public String description;

    //в описании полей сущности BundleElement этого поля нет:
    //https://www.jetbrains.com/help/youtrack/standalone/api-entity-BundleElement.html
    //В подтипахтак же нет, например:
    //https://www.jetbrains.com/help/youtrack/standalone/api-entity-StateBundleElement.html
    //https://www.jetbrains.com/help/youtrack/standalone/api-entity-EnumBundleElement.html
    //но это поле есть в описании JSON всех подтипов BundleElement. Например:
    //https://www.jetbrains.com/help/youtrack/standalone/api-json-schema.html#StateBundleElement
    //https://www.jetbrains.com/help/youtrack/standalone/api-json-schema.html#EnumBundleElement
    public Boolean archived;
}
