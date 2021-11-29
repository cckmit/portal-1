package ru.protei.portal.ui.delivery.client.widget.rfidlabel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.RFIDLabel;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RFIDLabelControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;
import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;

public class RFIDLabelFormField extends Composite
        implements HasValue<RFIDLabel> {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        setDebugId();
    }

    @Override
    public RFIDLabel getValue() {
        return label;
    }

    @Override
    public void setValue(RFIDLabel rfidLabel) {
        setValue(rfidLabel, false);
    }

    @Override
    public void setValue(RFIDLabel rfidLabel, boolean fireEvents) {
        this.label = rfidLabel;

        if (rfidLabel != null) {
            this.rfidLabelEpc.setValue(rfidLabel.getEpc());
            this.rfidLabelDevice.setText(rfidLabel.getRfidDevice().getReaderId());
            this.rfidLabelLastScanDate.setText(DateFormatter.formatDateTime(rfidLabel.getLastScanDate()));
        } else {
            this.rfidLabelEpc.setValue(null);
            this.rfidLabelDevice.setText(null);
            this.rfidLabelLastScanDate.setText(null);
        }

        if (fireEvents) {
            ValueChangeEvent.fire(this, label);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<RFIDLabel> valueChangeHandler) {
        return addHandler(valueChangeHandler, ValueChangeEvent.getType());
    }

    @UiHandler("rfidLabelEpc")
    public void onRfidLabelFocus(FocusEvent event) {
        if (rfidLabelEpc.getValue() != null && isNotEmpty(rfidLabelEpc.getValue())) {
            return;
        }
        rfidLabelController.getLastScanLabel(true, new FluentCallback<RFIDLabel>()
                .withSuccess(label -> {
                    if (label != null) {
                        setRFIDLabel(label);
                        return;
                    }
                    timer.prepare();
                    timer.schedule(timer.DELAY);
                }));
    }

    @UiHandler("rfidLabelEpc")
    public void onRfidLabelChangeValue(ValueChangeEvent<String> event) {
        setValue(null);
    }

    private void setRFIDLabel(RFIDLabel label) {
        if (this.isAttached() && isEmpty(rfidLabelEpc.getValue())) {
            setValue(label, true);
        }
    }

    private class RFIDLabelTimer extends Timer {
        private int rfidLabelCount = 0;
        private final int MAX_COUNT = 5;
        final int DELAY = 5000;

        public void prepare() {
            this.rfidLabelCount = 0;
        }

        @Override
        public void run() {
            if (!RFIDLabelFormField.this.isAttached() || isNotEmpty(rfidLabelEpc.getValue())) {
                return;
            }
            rfidLabelCount++;

            if (MAX_COUNT < rfidLabelCount) {
                return;
            }
            rfidLabelController.getLastScanLabel(false, new FluentCallback<RFIDLabel>()
                    .withSuccess(labelInner -> {
                        if (labelInner == null) {
                            this.schedule(DELAY);
                        } else {
                            setRFIDLabel(labelInner);
                        }
                    }));
        }
    }

    private void setDebugId() {
        // todo
/*        root.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.DELIVERY.KIT.ITEM);
        serialNumber.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.DELIVERY.KIT.SERIAL_NUMBER);
        state.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.DELIVERY.KIT.STATE);
        name.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.DELIVERY.KIT.NAME);
        remove.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.DELIVERY.KIT.REMOVE_BUTTON);*/
    }

    @UiField
    TextBox rfidLabelEpc;
    @UiField
    Label rfidLabelDevice;
    @UiField
    Label rfidLabelLastScanDate;

    @UiField
    Lang lang;
    @UiField
    HTMLPanel root;

    @Inject
    private RFIDLabelControllerAsync rfidLabelController;

    private RFIDLabel label;
    private final RFIDLabelTimer timer = new RFIDLabelTimer();

    interface RFIDLabelFormFieldItemUiBinder extends UiBinder< HTMLPanel, RFIDLabelFormField> {}
    private static RFIDLabelFormFieldItemUiBinder ourUiBinder = GWT.create( RFIDLabelFormFieldItemUiBinder.class );
}