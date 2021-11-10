package ru.protei.portal.ui.delivery.client.activity.pcborder.meta;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_PcbOrderPromptness;
import ru.protei.portal.core.model.dict.En_PcbOrderState;
import ru.protei.portal.core.model.dict.En_PcbOrderType;
import ru.protei.portal.core.model.dict.En_StencilType;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.Date;

public interface AbstractPcbOrderMetaView extends IsWidget {

    void setActivity(AbstractPcbOrderMetaActivity activity);

    HasValue<En_PcbOrderState> state();

    HasValue<En_PcbOrderPromptness> promptness();

    HasValue<En_PcbOrderType> orderType();

    HasVisibility stencilTypeVisibility();

    HasValue<En_StencilType> stencilType();

    HasValue<EntityOption> contractor();

    HasValue<Date> orderDate();

    HasValue<Date> readyDate();

    HasValue<Date> receiptDate();

    boolean isOrderDateValid();

    boolean isReadyDateValid();

    boolean isReceiptDateValid();

    void clearDatesValidationMarks();
}
