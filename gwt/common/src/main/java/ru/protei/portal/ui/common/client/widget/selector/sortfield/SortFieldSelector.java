package ru.protei.portal.ui.common.client.widget.selector.sortfield;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.ui.common.client.lang.En_SortFieldLang;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

/**
 * Селектор списка сортировки
 */
public class SortFieldSelector extends ButtonSelector< En_SortField > {

    public void fillOptions( ModuleType type ) {
        clearOptions();

        switch ( type ) {
            case COMPANY:
                addOption(lang.getName( En_SortField.comp_name ), En_SortField.comp_name );
                addOption( lang.getName( En_SortField.creation_date ), En_SortField.creation_date );
                break;
            case PRODUCT:
                addOption( lang.getName( En_SortField.prod_name ), En_SortField.prod_name );
                addOption( lang.getName( En_SortField.creation_date ), En_SortField.creation_date );
                break;
            case CLIENT:
                addOption( lang.getName( En_SortField.person_full_name ), En_SortField.person_full_name );
                addOption( lang.getName( En_SortField.person_position ), En_SortField.person_position );
                addOption( lang.getName( En_SortField.comp_name ), En_SortField.comp_name );
                break;
        }
    }

    @Inject
    private En_SortFieldLang lang;
}
