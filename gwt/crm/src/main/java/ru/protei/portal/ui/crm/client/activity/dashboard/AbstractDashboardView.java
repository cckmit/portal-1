package ru.protei.portal.ui.crm.client.activity.dashboard;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by bondarenko on 01.12.16.
 */
public interface AbstractDashboardView extends IsWidget {

    void setActivity( AbstractDashboardActivity activity );

    HasWidgets getActiveRecordsContainer();
    HasWidgets getNewRecordsContainer();
    HasWidgets getInactiveRecordsContainer();

}
