package ru.protei.portal.ui.common.client.widget.issueimportance;

import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.ImportanceLevel;
import ru.protei.portal.ui.common.client.common.ImportanceStyleProvider;
import ru.protei.portal.ui.common.client.widget.form.FormSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;

/**
 * Селектор критичности кейсов
 */
public class ImportanceFormSelector extends FormSelector<ImportanceLevel> {
    @Inject
    public void init( ) {
        setDisplayOptionCreator(value -> new DisplayOption(
                value == null ? defaultValue : value.getCode(),
                "importance-item",
                value == null ? null : ImportanceStyleProvider.getImportanceIcon(value.getCode()) + " selector"
        ));
    }

    @Override
    public void setValue(ImportanceLevel value) {
        super.setValue(value);
    }

    public void setDefaultValue(String value ) {
        this.defaultValue = value;
    }

    private String defaultValue;
}
