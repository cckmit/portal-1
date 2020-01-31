package ru.protei.portal.core.model.youtrack.dto.customfield.project;

import ru.protei.portal.core.model.youtrack.dto.bundle.YtStateBundle;
import ru.protei.portal.core.model.youtrack.dto.bundleelemenet.YtStateBundleElement;

import java.util.List;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-StateProjectCustomField.html
 */
public class YtStateProjectCustomField extends YtBundleProjectCustomField {

    public YtStateBundle bundle;
    public List<YtStateBundleElement> defaultValues;

    @Override
    public String toString() {
        return "YtStateProjectCustomField{" +
                "bundle=" + bundle +
                ", defaultValues=" + defaultValues +
                ", field=" + field +
                ", project=" + project +
                ", canBeEmpty=" + canBeEmpty +
                ", emptyFieldText='" + emptyFieldText + '\'' +
                '}';
    }
}
