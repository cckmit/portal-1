package ru.protei.portal.ui.common.client.widget.selector.absence;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

public class AbsenceFilterSelector extends ButtonPopupSingleSelector<FilterShortView> implements FilterSelector<FilterShortView> {
    @Inject
    public void init( AbsenceFilterModel model ) {
        setAsyncModel( model );
        setItemRenderer( option -> option == null ? defaultValue : option.getName() );
        setSearchEnabled(false);
        setHasNullValue(true);
        setDefaultValue(lang.filterNotDefined());
    }
}