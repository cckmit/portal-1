package ru.protei.portal.ui.company.client.widget.selector;

import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.base.Selector;

/**
 * Created by turik on 11.10.16.
 */
public class SortFieldSelector extends Selector<En_SortField>{

    @Inject
    public void init() {
        fillOptions();
    }

    public void fillOptions() {
        clearOptions();

        addOption( lang.name(), En_SortField.comp_name );
        addOption( lang.date(), En_SortField.creation_date );
    }

    @Override
    public void fillSelectorView( String selectedValue ) {
    }

    @Inject
    Lang lang;
}
