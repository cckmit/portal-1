package ru.protei.portal.ui.delivery.client.activity.card.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

public interface AbstractCardTableView extends IsWidget {
    void setActivity(AbstractCardTableActivity cardTableActivity);
    void setAnimation(TableAnimation animation);

    void clearRecords();
    void triggerTableLoad();
    void setTotalRecords(int totalRecords);
    int getPageCount();
    void scrollTo(int page);

    HasWidgets getFilterContainer();
    HasWidgets getPagerContainer();

    void clearSelection();
}
