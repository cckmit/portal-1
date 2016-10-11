package ru.protei.portal.ui.product.client.widgets.sortfieldselector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;

/**
 * Специфичный селектор списка сервисов для правил маршрутизации
 */
public class SortFieldSelector
        extends Selector<En_SortField>
{
    @Inject
    public void init() {
        fillOptions();
    }

    @Override
    public void fillSelectorView( String selectedValue) { }

    public void fillOptions() {
        clearOptions();

        addOption( lang.name(), En_SortField.prod_name );
        addOption( lang.created(), En_SortField.creation_date );
    }

    @Inject
    private Lang lang;

}