package ru.protei.portal.ui.delivery.client.activity.pcborder.edit.modal;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractPcbOrderModalEditView extends IsWidget {

    void setActivity(AbstractPcbOrderModalEditActivity activity);

    HasValue<Integer> receivedAmount();

    void setReceivedAmountValid(boolean isValid);
}
