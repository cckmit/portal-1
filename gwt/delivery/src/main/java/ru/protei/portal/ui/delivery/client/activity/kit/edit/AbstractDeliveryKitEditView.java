package ru.protei.portal.ui.delivery.client.activity.kit.edit;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractDeliveryKitEditView extends IsWidget {

    void setActivity(AbstractDeliveryKitEditActivity activity);

    HasWidgets getKitsContainer();
}
