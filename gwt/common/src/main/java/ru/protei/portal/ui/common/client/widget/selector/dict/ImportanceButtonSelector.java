package ru.protei.portal.ui.common.client.widget.selector.dict;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.ui.common.client.lang.En_CaseImportanceLang;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

/**
 * Селектор критичности кейсов
 */
public class ImportanceButtonSelector extends ButtonSelector<En_ImportanceLevel> {

    @Inject
    public void init( ) {
        addOption(lang.getImportanceName(En_ImportanceLevel.BASIC), En_ImportanceLevel.BASIC);
        addOption(lang.getImportanceName(En_ImportanceLevel.IMPORTANT), En_ImportanceLevel.IMPORTANT);
        addOption(lang.getImportanceName(En_ImportanceLevel.CRITICAL), En_ImportanceLevel.CRITICAL);
        addOption(lang.getImportanceName(En_ImportanceLevel.COSMETIC), En_ImportanceLevel.COSMETIC);
    }

    public void setDefaultValue( String value ) {
        addOption( value , null );
    }

    @Inject
    En_CaseImportanceLang lang;

}
