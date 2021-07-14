package ru.protei.portal.ui.delivery.client.activity.kit.table;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractKitTableView extends IsWidget {
    void setActivity(AbstractKitTableActivity activity);

    HasWidgets getKitTableContainer();

    HasWidgets getModuleTableContainer();

    HasWidgets getModuleEditContainer();
}
