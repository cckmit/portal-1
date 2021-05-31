package ru.protei.portal.ui.delivery.client.activity.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.widget.deliveryfilter.DeliveryFilterWidget;

public interface AbstractDeliveryTableView extends IsWidget {
    void setActivity(AbstractDeliveryTableActivity activity);

    void setAnimation(TableAnimation animation);

    void clearRecords();

    DeliveryFilterWidget getFilterWidget();

    void triggerTableLoad();

    void setTotalRecords(int totalRecords);

    int getPageCount();

    void scrollTo(int page);

    HasWidgets getPreviewContainer();

    HasWidgets getPagerContainer();

    void clearSelection();

    void updateRow(Delivery item);
}
