package ru.protei.portal.ui.issueassignment.client.activity.desk;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractDeskView extends IsWidget {

    void setActivity(AbstractDeskActivity activity);

    HasWidgets tableContainer();

    HasVisibility tableViewVisibility();

    HasVisibility loadingViewVisibility();

    HasVisibility failedViewVisibility();

    HasVisibility notificationVisibility();

    TakesValue<String> notificationText();

    void setFailedViewText(String text);
}
