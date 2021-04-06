package ru.protei.portal.ui.common.client.widget.selector.project.filter;

import com.google.inject.Inject;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.ui.common.client.widget.filterwidget.FilterSelector;
import ru.protei.portal.ui.common.client.widget.selector.button.ButtonPopupSingleSelector;

public class ProjectFilterSelector extends ButtonPopupSingleSelector<FilterShortView>
        implements FilterSelector<FilterShortView> {

    @Inject
    public void init( ProjectFilterModel model ) {
        setAsyncModel( model );
        setItemRenderer( option -> option == null ? defaultValue : option.getName() );
        setSearchEnabled(false);
        setHasNullValue(true);
        setDefaultValue(lang.filterNotDefined());
    }
}
