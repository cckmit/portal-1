package ru.protei.portal.ui.employeeregistration.client.activity.table;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.winter.core.utils.beans.SearchResult;

public interface AbstractEmployeeRegistrationTableView extends IsWidget {
    void setActivity(AbstractEmployeeRegistrationTableActivity activity);

    void setAnimation(TableAnimation animation);

    void clearRecords();

    void triggerTableLoad();

    void setTotalRecords(int totalRecords);

    HasWidgets getPreviewContainer();

    HTMLPanel getFilterContainer();

    void clearSelection();
}
