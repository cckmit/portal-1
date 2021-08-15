package ru.protei.portal.ui.delivery.client.activity.module.create;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractModuleCreateView extends IsWidget {

    void setActivity(AbstractModuleCreateActivity activity);

    HasEnabled saveEnabled();

    HasValue<String> serialNumber();

    HasValue<String> name();

    HasValue<String> description();

    HasWidgets getMetaViewContainer();
}
