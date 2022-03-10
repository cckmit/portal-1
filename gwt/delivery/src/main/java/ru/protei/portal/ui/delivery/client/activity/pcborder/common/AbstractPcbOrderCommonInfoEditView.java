package ru.protei.portal.ui.delivery.client.activity.pcborder.common;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.view.EntityOption;

public interface AbstractPcbOrderCommonInfoEditView extends IsWidget {

    void setActivity(AbstractPcbOrderCommonInfoEditActivity activity);

    HasValue<EntityOption> cardType();

    HasValue<Integer> amount();

    String getAmount();

    HasValue<String> modification();

    HasValue<String> comment();

    HasVisibility buttonsContainerVisibility();

    HasEnabled saveEnabled();

    void setAmountValid(boolean isValid);
}
