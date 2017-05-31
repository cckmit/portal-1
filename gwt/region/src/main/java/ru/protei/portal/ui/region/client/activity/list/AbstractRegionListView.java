package ru.protei.portal.ui.region.client.activity.list;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида списка продуктов
 */
public interface AbstractRegionListView extends IsWidget {

    void setActivity( AbstractRegionListActivity activity );
    HasWidgets getChildContainer();
    HasWidgets getFilterContainer();
}
