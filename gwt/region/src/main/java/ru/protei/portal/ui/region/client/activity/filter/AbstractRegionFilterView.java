package ru.protei.portal.ui.region.client.activity.filter;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_SortField;

/**
 * Абстракция вида фильтра регионов
 */
public interface AbstractRegionFilterView extends IsWidget {

    void setActivity( AbstractRegionFilterActivity activity );

    HasValue<Boolean> showDeprecated();
    HasValue<En_SortField> sortField();
    HasValue< Boolean > sortDir();
    HasValue< String > searchPattern();
    void resetFilter();
}