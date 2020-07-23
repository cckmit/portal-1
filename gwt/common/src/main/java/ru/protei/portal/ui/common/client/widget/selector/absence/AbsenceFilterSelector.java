package ru.protei.portal.ui.common.client.widget.selector.absence;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.AbsenceFilterShortView;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

public class AbsenceFilterSelector extends ButtonPopupSingleSelector<AbsenceFilterShortView> implements FilterSelector<AbsenceFilterShortView> {
    @Inject
    public void init( AbsenceFilterModel model ) {
        setAsyncModel( model );
        setItemRenderer( option -> option == null ? defaultValue : option.getName() );
        setSearchEnabled(false);
        setHasNullValue(true);
        setDefaultValue(lang.filterNotDefined());
    }
}