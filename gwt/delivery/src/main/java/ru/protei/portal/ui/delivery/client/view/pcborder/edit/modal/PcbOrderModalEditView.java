package ru.protei.portal.ui.delivery.client.view.pcborder.edit.modal;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.inject.Inject;
import ru.protei.portal.ui.delivery.client.activity.pcborder.edit.modal.AbstractPcbOrderModalEditActivity;
import ru.protei.portal.ui.delivery.client.activity.pcborder.edit.modal.AbstractPcbOrderModalEditView;

import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.REQUIRED;

public class PcbOrderModalEditView extends Composite implements AbstractPcbOrderModalEditView {
    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractPcbOrderModalEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<Integer> receivedAmount() {
        return receivedAmount;
    }

    @Override
    public void setReceivedAmountValid(boolean isValid) {
        receivedAmount.setStyleName(REQUIRED, !isValid);
    }

    @UiHandler("receivedAmount")
    public void onChangeReceivedAmount(KeyUpEvent event) {
        activity.onReceivedAmountChanged();
    }

    @UiField
    IntegerBox receivedAmount;

    private AbstractPcbOrderModalEditActivity activity;

    private static PcbOrderModalEditViewUiBinder ourUiBinder = GWT.create(PcbOrderModalEditViewUiBinder.class);
    interface PcbOrderModalEditViewUiBinder extends UiBinder<HTMLPanel, PcbOrderModalEditView> {}
}
