package ru.protei.portal.ui.product.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.ui.common.client.widget.viewtype.ViewType;

/**
 * Абстракция вида фильтра продуктов
 */
public interface AbstractProductFilterView extends IsWidget {

    void setActivity( AbstractProductFilterActivity activity );

    HasValue<Boolean> showDeprecated();
    HasValue<En_SortField> sortField();
    HasValue< Boolean > sortDir();
    HasValue< String > searchPattern();
    HasValue<ViewType> viewType();
    void resetFilter();
}