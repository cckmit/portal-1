package ru.protei.portal.ui.plan.client.activity.table;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

public interface AbstractPlanTableView extends IsWidget {
    void setActivity(AbstractPlanTableActivity activity);

    void clearSelection();

    void updateRow(Plan plan);

    void triggerTableLoad();

    void setTotalRecords(int totalRecords);

    int getPageCount();

    void scrollTo(int page);

    HTMLPanel getFilterContainer();

    HTMLPanel getPreviewContainer();

    HasWidgets getPagerContainer();

    void setAnimation(TableAnimation animation);

    void clearRecords();
}
