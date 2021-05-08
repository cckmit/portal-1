package ru.protei.portal.ui.delivery.client.widget.kit.view.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DeliveryState;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.delivery.state.DeliveryStateFormSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

import static ru.protei.portal.core.model.util.CrmConstants.Masks.DELIVERY_KIT_SERIAL_NUMBER_PATTERN;
import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HAS_ERROR;
import static ru.protei.portal.ui.common.client.common.UiConstants.Styles.HIDE;

public class DeliveryKitItem
        extends Composite
        implements TakesValue<Kit>,
        HasCloseHandlers<DeliveryKitItem>,
        HasValueChangeHandlers<DeliveryKitItem>
{
    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        serialNumber.setRegexp(DELIVERY_KIT_SERIAL_NUMBER_PATTERN);
        serialNumber.setPlaceholder(lang.deliveryKitSerialNumberTitle());
        setTestAttributes();
    }

    @Override
    public Kit getValue() {
        return value;
    }

    @Override
    public void setValue( Kit value ) {
        if (value == null) {
            value = new Kit();
        }
        this.value = value;

        serialNumber.setValue(value.getSerialNumber());
        state.setValue(value.getState());
        name.setValue(value.getName());
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler<DeliveryKitItem> handler ) {
        return addHandler( handler, CloseEvent.getType() );
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DeliveryKitItem> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @UiHandler( "remove" )
    public void onRemoveClicked( ClickEvent event ) {
        event.preventDefault();
        CloseEvent.fire( this, this );
    }

    @UiHandler( "serialNumber" )
    public void onChangeSerialNumber(ValueChangeEvent<String> event) {
        value.setSerialNumber(serialNumber.getValue());
        changeTimer.schedule(50);
    }

    @UiHandler( "state" )
    public void onChangeState(ValueChangeEvent<En_DeliveryState> event) {
        value.setState(state.getValue());
        changeTimer.schedule(50);
    }

    @UiHandler( "name" )
    public void onChangeName(ValueChangeEvent<String> event) {
        value.setName(name.getValue());
        changeTimer.schedule(50);
    }

    public void setSerialNumber(String value) {
        serialNumber.setValue(value, true);
    }

    public void setError(boolean isError, String error) {
        markBoxAsError(isError);

        if (isError) {
            msg.removeClassName(HIDE);
            msg.setInnerText(error);
            return;
        }

        msg.addClassName(HIDE);
        msg.setInnerText(null);
    }

    public boolean isValid(){
        return serialNumber.isValid() & name.isValid() &&
                state.getValue() != null && HelperFunc.isNotEmpty(name.getValue());
    }

    public HasEnabled removeEnable() {
        return remove;
    }

    private void markBoxAsError(boolean isError) {
        if (isError) {
            root.addStyleName(HAS_ERROR);
            return;
        }
        root.removeStyleName(HAS_ERROR);
    }

    private void setTestAttributes() {
        root.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.DELIVERY.KIT.ITEM);
        serialNumber.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.DELIVERY.KIT.SERIAL_NUMBER);
        state.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.DELIVERY.KIT.STATE);
        name.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.DELIVERY.KIT.NAME);
        remove.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.CONTRACT.SPECIFICATION_ITEM.REMOVE_BUTTON);
    }

    Timer changeTimer = new Timer(){
        @Override
        public void run() {
            ValueChangeEvent.fire(DeliveryKitItem.this, DeliveryKitItem.this);
        }
    };

    @UiField
    ValidableTextBox serialNumber;
    @Inject
    @UiField(provided = true)
    DeliveryStateFormSelector state;
    @UiField
    ValidableTextBox name;

    @UiField
    Button remove;
    @UiField
    Element msg;

    @UiField
    Lang lang;
    @UiField
    HTMLPanel root;

    private Kit value = new Kit();

    interface DeliveryKitItemUiBinder extends UiBinder< HTMLPanel, DeliveryKitItem> {}
    private static DeliveryKitItemUiBinder ourUiBinder = GWT.create( DeliveryKitItemUiBinder.class );
}