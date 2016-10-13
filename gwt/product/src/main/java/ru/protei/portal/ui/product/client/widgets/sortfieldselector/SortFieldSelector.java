package ru.protei.portal.ui.product.client.widgets.sortfieldselector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.ui.common.client.lang.En_SortFieldLang;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonSelector;

/**
 * Селектор полей сортировки продуктов
 */
public class SortFieldSelector
        extends ButtonSelector<En_SortField>
{
    @Inject
    public void init() {
        fillOptions();
    }


    public void fillOptions() {
        clearOptions();

        addOption( lang.getName(En_SortField.prod_name), En_SortField.prod_name );
        addOption( lang.getName(En_SortField.creation_date), En_SortField.creation_date );
    }

    @Inject
    private En_SortFieldLang lang;

}