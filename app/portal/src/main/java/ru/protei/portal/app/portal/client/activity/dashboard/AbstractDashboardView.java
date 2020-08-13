package ru.protei.portal.app.portal.client.activity.dashboard;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface AbstractDashboardView extends IsWidget {

    void addTableToContainer (Widget widget);

    void clearContainers();

    HasVisibility loadingViewVisibility();

    HasVisibility failedViewVisibility();

    HasVisibility emptyViewVisibility();

    HasWidgets quickview();

    void showQuickview(boolean isShow);

    void setFailedViewText(String text);
}
