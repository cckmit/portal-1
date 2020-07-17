package ru.protei.portal.ui.education.client.activity.admin;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractEducationAdminView extends IsWidget {

    void setActivity(AbstractEducationAdminActivity activity);

    void clearRecords();

    void triggerTableLoad();

    void setTotalRecords(int totalRecords);

    int getPageCount();

    void scrollTo(int page);

    HasWidgets getFilterContainer();

    HasWidgets getPagerContainer();

    void clearSelection();
}
