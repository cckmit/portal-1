package ru.protei.portal.ui.company.client.activity.list;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Created by turik on 27.09.16.
 */
public interface AbstractCompanyListView extends IsWidget {

    void setActivity( AbstractCompanyListActivity activity );
    HasWidgets getCompanyContainer();
    String getSearchPattern();
    ListBox getGroupList();
}
