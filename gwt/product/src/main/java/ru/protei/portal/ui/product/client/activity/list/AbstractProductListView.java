package ru.protei.portal.ui.product.client.activity.list;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида списка продуктов
 */
public interface AbstractProductListView extends IsWidget {

    void setActivity( AbstractProductListActivity activity );
    HasWidgets getChildContainer();
    HasWidgets getFilterContainer ();

    void setListCreateBtnVisible( boolean visibility );
}
