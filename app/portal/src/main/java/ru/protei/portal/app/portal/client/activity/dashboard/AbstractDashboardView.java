package ru.protei.portal.app.portal.client.activity.dashboard;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractDashboardView extends IsWidget {

    HasWidgets container();

    HasVisibility loadingViewVisibility();

    HasVisibility failedViewVisibility();

    HasVisibility emptyViewVisibility();

    void setFailedViewText(String text);
}
