package ru.protei.portal.ui.common.client.widget.selector.dict;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.ui.common.client.common.ImportanceStyleProvider;
import ru.protei.portal.ui.common.client.lang.En_CaseImportanceLang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

/**
 * Селектор критичности кейсов
 */
public class ImportanceButtonSelector extends ButtonSelector<En_ImportanceLevel> {

    @Inject
    public void init( ) {
        setDisplayOptionCreator(value -> new DisplayOption(
                value == null ? defaultValue : lang.getImportanceName(value),
                "importance-item",
                value == null ? null : ImportanceStyleProvider.getImportanceIcon(value) + " selector"
        ));

        addBtnStyleName("importance-btn");

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
