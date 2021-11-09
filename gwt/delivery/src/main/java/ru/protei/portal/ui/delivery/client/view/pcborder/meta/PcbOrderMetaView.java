package ru.protei.portal.ui.delivery.client.view.pcborder.meta;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.dict.En_PcbOrderPromptness;
import ru.protei.portal.core.model.dict.En_PcbOrderState;
import ru.protei.portal.core.model.dict.En_PcbOrderType;
import ru.protei.portal.core.model.dict.En_StencilType;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyFormSelector;
import ru.protei.portal.ui.delivery.client.activity.pcborder.meta.AbstractPcbOrderMetaActivity;
import ru.protei.portal.ui.delivery.client.activity.pcborder.meta.AbstractPcbOrderMetaView;
import ru.protei.portal.ui.delivery.client.widget.pcborder.ordertype.PcbOrderTypeFormSelector;
import ru.protei.portal.ui.delivery.client.widget.pcborder.promptness.PcbOrderPromptnessFormSelector;
import ru.protei.portal.ui.delivery.client.widget.pcborder.state.PcbOrderStateFormSelector;
import ru.protei.portal.ui.delivery.client.widget.pcborder.stenciltype.PcbOrderStencilTypeFormSelector;

import java.util.Date;

public class PcbOrderMetaView extends Composite implements AbstractPcbOrderMetaView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractPcbOrderMetaActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<En_PcbOrderState> state() {
        return state;
    }

    @Override
    public HasValue<En_PcbOrderPromptness> promptness() {
        return promptness;
    }

    @Override
    public HasValue<En_PcbOrderType> orderType() {
        return orderType;
    }

    @Override
    public HasVisibility stencilTypeContainer() {
        return stencilTypeContainer;
    }

    @Override
    public HasValue<En_StencilType> stencilType() {
        return stencilType;
    }

    @Override
    public HasValue<EntityOption> contractor() {
        return contractor;
    }

    @Override
    public HasValue<Date> orderDate() {
        return orderDate;
    }

    @Override
    public HasValue<Date> readyDate() {
        return readyDate;
    }

    @Override
    public HasValue<Date> receiptDate() {
        return receiptDate;
    }

    @Override
    public void setOrderDateValid(boolean isValid) {
        orderDate.markInputValid(isValid);
    }

    @Override
    public void setReadyDateValid(boolean isValid) {
        readyDate.markInputValid(isValid);
    }

    @Override
    public void setReceiptDateValid(boolean isValid) {
        receiptDate.markInputValid(isValid);
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        state.setEnsureDebugId(DebugIds.PCB_ORDER.STATE_SELECTOR);
        promptness.setEnsureDebugId(DebugIds.PCB_ORDER.PROMPTNESS_SELECTOR);
        orderType.setEnsureDebugId(DebugIds.PCB_ORDER.ORDER_TYPE_SELECTOR);
        stencilType.setEnsureDebugId(DebugIds.PCB_ORDER.STENCIL_TYPE_SELECTOR);
        contractor.setEnsureDebugId(DebugIds.PCB_ORDER.CONTRACTOR_SELECTOR);
        orderDate.setEnsureDebugId(DebugIds.PCB_ORDER.ORDER_DATE);
        readyDate.setEnsureDebugId(DebugIds.PCB_ORDER.READY_DATE);
        receiptDate.setEnsureDebugId(DebugIds.PCB_ORDER.RECEIPT_DATE);
    }

    @UiHandler("orderType")
    public void onDeadlineChanged(ValueChangeEvent<En_PcbOrderType> event) {
        activity.onOrderTypeChanged(event.getValue());
    }

    @UiField
    HTMLPanel root;
    @Inject
    @UiField( provided = true )
    PcbOrderStateFormSelector state;
    @Inject
    @UiField( provided = true )
    PcbOrderPromptnessFormSelector promptness;
    @Inject
    @UiField( provided = true )
    PcbOrderTypeFormSelector orderType;
    @UiField
    HTMLPanel stencilTypeContainer;
    @Inject
    @UiField( provided = true )
    PcbOrderStencilTypeFormSelector stencilType;
    @Inject
    @UiField( provided = true )
    CompanyFormSelector contractor;
    @Inject
    @UiField( provided = true )
    SinglePicker orderDate;
    @Inject
    @UiField( provided = true )
    SinglePicker readyDate;
    @Inject
    @UiField( provided = true )
    SinglePicker receiptDate;

    @UiField
    Lang lang;

    private AbstractPcbOrderMetaActivity activity;

    interface ViewUiBinder extends UiBinder<HTMLPanel, PcbOrderMetaView> {}
    private static ViewUiBinder ourUiBinder = GWT.create(ViewUiBinder.class);
}
