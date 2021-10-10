package ru.protei.portal.ui.delivery.client.activity.cardbatch.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

public interface AbstractCardBatchTableView extends IsWidget {
    void setActivity(AbstractCardBatchTableActivity cardTableActivity);
    void setAnimation(TableAnimation animation);

    void clearRecords();
    void triggerTableLoad();
    void setTotalRecords(int totalRecords);
    int getPageCount();
    void scrollTo(int page);

    HasWidgets getPreviewContainer();
    HasWidgets getFilterContainer();
    HasWidgets getPagerContainer();

    void clearSelection();

    void updateRow(CardBatch item);
}
