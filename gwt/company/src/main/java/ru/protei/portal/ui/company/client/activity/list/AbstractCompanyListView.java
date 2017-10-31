package ru.protei.portal.ui.company.client.activity.list;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Представление списка компаний
 */
public interface AbstractCompanyListView extends IsWidget {

    void setActivity( CompanyListActivity activity );
    HasWidgets getChildContainer();
    HasWidgets getFilterContainer ();

    void setListCreateBtnVisible( boolean isVisible );
}
