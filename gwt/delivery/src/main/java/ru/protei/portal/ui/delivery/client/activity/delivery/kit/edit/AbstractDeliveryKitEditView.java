package ru.protei.portal.ui.delivery.client.activity.delivery.kit.edit;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CommentOrHistoryType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.widget.tab.multi.MultiTabWidget;

public interface AbstractDeliveryKitEditView extends IsWidget {

    void setActivity(AbstractDeliveryKitEditActivity activity);

    void setCreatedBy(String value);

    HasWidgets getItemsContainer();

    MultiTabWidget<En_CommentOrHistoryType> getMultiTabWidget();

    void setStateEnabled(boolean isEnabled);

    void setNameEnabled(boolean isEnabled);

    void setSerialNumber(String serialNumber);

    TakesValue<CaseState> state();

    TakesValue<String> name();
}
