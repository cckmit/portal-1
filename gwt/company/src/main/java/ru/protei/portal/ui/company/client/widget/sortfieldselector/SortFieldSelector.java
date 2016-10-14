package ru.protei.portal.ui.company.client.widget.sortfieldselector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

/**
 * Селектор списка сортировки
 */
public class SortFieldSelector extends ButtonSelector< En_SortField > {

    @Inject
    public void init() {
        fillOptions();
    }

    public void fillOptions() {
        clearOptions();

        addOption( lang.name(), En_SortField.comp_name );
        addOption( lang.date(), En_SortField.creation_date );
    }

    @Inject
    Lang lang;
}
