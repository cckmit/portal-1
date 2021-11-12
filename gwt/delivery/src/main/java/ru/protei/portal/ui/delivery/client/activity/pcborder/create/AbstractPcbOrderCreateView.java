package ru.protei.portal.ui.delivery.client.activity.pcborder.create;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractPcbOrderCreateView extends IsWidget {

    void setActivity(AbstractPcbOrderCreateActivity activity);

    HasEnabled saveEnabled();

    HasWidgets getCommonInfoContainer();

    HasWidgets getMetaContainer();
}
