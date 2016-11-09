package ru.protei.portal.ui.common.client.widget.selector.sortfield;

import com.google.gwt.uibinder.client.UiField;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.ui.common.client.lang.En_SortFieldLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

/**
 * Селектор списка сортировки
 */
public class SortFieldSelector extends ButtonSelector< En_SortField > {

    public void setType( ModuleType type ) {
        this.type = type;
        fillOptions();
    }

    public void fillOptions() {
        clearOptions();

        switch ( type ) {
            case COMPANY:
                addOption(sortFieldLang.getName( En_SortField.comp_name ), En_SortField.comp_name );
                addOption( sortFieldLang.getName( En_SortField.creation_date ), En_SortField.creation_date );
                break;
            case PRODUCT:
                addOption( sortFieldLang.getName( En_SortField.prod_name ), En_SortField.prod_name );
                addOption( sortFieldLang.getName( En_SortField.creation_date ), En_SortField.creation_date );
                break;
            case CONTACT:
                addOption( sortFieldLang.getName( En_SortField.person_full_name ), En_SortField.person_full_name );
                addOption( sortFieldLang.getName( En_SortField.person_position ), En_SortField.person_position );
                addOption( lang.company(), En_SortField.comp_name );
            case ISSUE:
                addOption( sortFieldLang.getName( En_SortField.creation_date ), En_SortField.creation_date );
                break;
        }
    }

    private ModuleType type;// = ModuleType.CONTACT;

    @Inject
    private Lang lang;

    @Inject
    private En_SortFieldLang sortFieldLang;
}
