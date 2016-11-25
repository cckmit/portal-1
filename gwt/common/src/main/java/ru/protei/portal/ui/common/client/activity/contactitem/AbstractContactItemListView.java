package ru.protei.portal.ui.common.client.activity.contactitem;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция представления списка
 */
public interface AbstractContactItemListView extends IsWidget{

    HasWidgets getItemsContainer();

}
