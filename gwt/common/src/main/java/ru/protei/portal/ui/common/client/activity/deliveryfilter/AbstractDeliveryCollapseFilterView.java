package ru.protei.portal.ui.common.client.activity.deliveryfilter;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида фильтра поставок
 */
public interface AbstractDeliveryCollapseFilterView extends IsWidget {

    void setActivity(AbstractDeliveryCollapseFilterActivity activity);

    HasWidgets getContainer();
}