package ru.protei.portal.ui.common.client.widget.selector.sortfield;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.ui.common.client.lang.En_SortFieldLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.DisplayOption;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

/**
 * Селектор списка сортировки
 */
public class SortFieldSelector extends ButtonSelector< En_SortField > {

    public void setType( ModuleType type ) {
        this.type = type;
        setDisplayOptionCreator( value -> new DisplayOption( sortFieldLang.getName( value ) ) );

        fillOptions();
    }

    public void fillOptions() {
        clearOptions();

        switch ( type ) {
            case COMPANY:
                addOption( En_SortField.comp_name );
                addOption( En_SortField.creation_date );
                break;
            case PRODUCT:
                addOption( En_SortField.prod_name );
                addOption( En_SortField.creation_date );
                break;
            case CONTACT:
                addOption( En_SortField.person_full_name );
                addOption( En_SortField.person_position );
                addOption( En_SortField.comp_name );
                break;
            case ISSUE:
                addOption( En_SortField.creation_date );
                addOption( En_SortField.last_update );
                addOption( En_SortField.issue_number );
                break;
            case EQUIPMENT:
                addOption( En_SortField.name );
                addOption( En_SortField.equipment_name_sldwrks );
                addOption( En_SortField.project );
                break;
            case ACCOUNT:
                addOption( En_SortField.ulogin );
                addOption( En_SortField.person_full_name );
                break;
            case ROLE:
                addOption( En_SortField.role_name );
                break;
            case OFFICIAL:
                addOption( En_SortField.creation_date );
                addOption( En_SortField.region_name );
                addOption( En_SortField.prod_name );
                break;
        }
    }

    private ModuleType type;

    @Inject
    private En_SortFieldLang sortFieldLang;
}
