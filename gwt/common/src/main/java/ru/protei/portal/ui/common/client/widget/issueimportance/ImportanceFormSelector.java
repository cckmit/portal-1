package ru.protei.portal.ui.common.client.widget.issueimportance;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.ui.common.client.common.ImportanceStyleProvider;
import ru.protei.portal.ui.common.client.lang.En_CaseImportanceLang;
import ru.protei.portal.ui.common.client.widget.form.FormSelector;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;

/**
 * Селектор критичности кейсов
 */
public class ImportanceFormSelector extends FormSelector<En_ImportanceLevel> {

    @Inject
    public void init( ) {
        setDisplayOptionCreator(value -> new DisplayOption(
                value == null ? defaultValue : lang.getImportanceName(value),
                "importance-item",
                value == null ? null : ImportanceStyleProvider.getImportanceIcon(value) + " selector"
        ));

        fillOptions();
    }

    public void setDefaultValue( String value ) {
        this.defaultValue = value;
    }

    private void fillOptions() {
        if ( defaultValue != null ) {
            addOption( null );
        }
        for ( En_ImportanceLevel value : En_ImportanceLevel.values() ) {
            addOption( value );
        }
    }

    @Inject
    En_CaseImportanceLang lang;

    private String defaultValue;
}
