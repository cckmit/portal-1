package ru.protei.portal.ui.employee.client.activity.list;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.ui.common.client.animation.TableAnimation;

public interface AbstractEmployeeTableView extends IsWidget {
    void setActivity(AbstractEmployeeTableActivity activity);

    void clearRecords();

    HasWidgets getPreviewContainer();

    HasWidgets getFilterContainer();

    HasWidgets getPagerContainer();

    void setPersonsCount(Long issuesCount);

    void triggerTableLoad();

    void setTotalRecords(int totalRecords);

    int getPageCount();

    void scrollTo(int page);

    void updateRow(EmployeeShortView item);

    void setAnimation(TableAnimation animation);
}
