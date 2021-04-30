package ru.protei.portal.ui.delivery.client.activity.table;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

public interface AbstractDeliveryTableView extends IsWidget {
    void setActivity(AbstractDeliveryTableActivity activity);

    void setAnimation(TableAnimation animation);

    void clearRecords();

    void triggerTableLoad();

    void setTotalRecords(int totalRecords);

    int getPageCount();

    void scrollTo(int page);

    HasWidgets getPreviewContainer();

    HTMLPanel getFilterContainer();

    HasWidgets getPagerContainer();

    void clearSelection();
}
