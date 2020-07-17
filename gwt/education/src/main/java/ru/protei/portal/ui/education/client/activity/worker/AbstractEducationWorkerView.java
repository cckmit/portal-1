package ru.protei.portal.ui.education.client.activity.worker;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractEducationWorkerView extends IsWidget {

    void setActivity(AbstractEducationWorkerActivity activity);

    HasWidgets walletContainer();

    HasVisibility walletLoadingViewVisibility();

    HasVisibility walletFailedViewVisibility();

    void walletFailedViewText(String text);

    HasWidgets tableContainer();
}
