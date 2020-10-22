package ru.protei.portal.ui.common.client.widget.selector.dutylog;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

public class DutyLogFilterSelector extends ButtonPopupSingleSelector<FilterShortView> implements FilterSelector<FilterShortView> {
    @Inject
    public void init(DutyLogFilterModel model ) {
        setAsyncModel( model );
        setItemRenderer( option -> option == null ? defaultValue : option.getName() );
        setSearchEnabled(false);
        setHasNullValue(true);
        setDefaultValue(lang.filterNotDefined());
    }
}