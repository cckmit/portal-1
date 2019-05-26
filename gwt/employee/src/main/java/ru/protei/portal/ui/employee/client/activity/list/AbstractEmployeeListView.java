package ru.protei.portal.ui.employee.client.activity.list;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Представление списка сотрудников
 */
public interface AbstractEmployeeListView extends IsWidget {

    void setActivity( AbstractEmployeeListActivity activity );
    HasWidgets getChildContainer();
    HasWidgets getFilterContainer ();
    void showLoader( boolean isShow );
    void updateLabel( int totalCount );
}
