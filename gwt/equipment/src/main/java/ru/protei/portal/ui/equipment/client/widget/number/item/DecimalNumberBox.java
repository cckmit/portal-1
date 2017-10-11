package ru.protei.portal.ui.equipment.client.widget.number.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.events.HasAddHandlers;
import ru.protei.portal.ui.common.client.lang.En_OrganizationCodeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.mask.MaskedTextBox;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.ui.common.client.widget.selector.event.*;
import ru.protei.portal.ui.equipment.client.widget.number.list.DecimalNumberList;
import ru.protei.winter.core.utils.StringUtils;
import ru.protei.winter.web.common.client.common.DisplayStyle;

import java.util.Objects;

/**
 * Вид виджета децимального номера
 */
public class DecimalNumberBox
        extends Composite implements HasValue<DecimalNumber>, HasEnabled, HasRemoveHandlers, HasAddHandlers {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        classifierCode.getElement().setAttribute( "placeholder", lang.equipmentClassifierCode().toLowerCase() );
        regNum.getElement().setAttribute( "placeholder", lang.equipmentRegisterNumber().toLowerCase() );
        regNumModification.getElement().setAttribute( "placeholder", lang.equipmentRegisterNumberModification().toLowerCase() );
        organizationCode.setEnabled( false );
    }

    @Override
    public DecimalNumber getValue() {
            // todo: need parse value
        return value;
    }

    @Override
    public void setValue( DecimalNumber decimalNumber ) {
        setValue( decimalNumber, false );
    }

    @Override
    public void setValue( DecimalNumber decimalNumber, boolean fireEvents ) {
        this.value = decimalNumber;
        if ( value == null ) {
            value = new DecimalNumber();
        }

        organizationCode.setText(organizationCodeLang.getName(value.getOrganizationCode()));
        classifierCode.setText( value.getClassifierCode() == null ? null : NumberFormat.getFormat("000000").format( value.getClassifierCode() ) );
        regNum.setText( value.getRegisterNumber() == null ? null : NumberFormat.getFormat("000").format( value.getRegisterNumber() ) );
        regNumModification.setText( value.getModification() == null ? null : NumberFormat.getFormat("00").format( value.getModification() ) );
        isReserve.setValue( value.isReserve() );

        checkNextButtonState();
        clearMessage();

        if ( fireEvents ) {
            ValueChangeEvent.fire( this, decimalNumber );
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<DecimalNumber> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    @Override
    public HandlerRegistration addRemoveHandler( RemoveHandler handler ) {
        return addHandler( handler, RemoveEvent.getType() );
    }

    @Override
    public HandlerRegistration addAddHandler(AddHandler handler) {
        return addHandler(handler, AddEvent.getType());
    }

    @Override
    public boolean isEnabled() {
        return classifierCode.isEnabled() && regNumModification.isEnabled() && regNum.isEnabled();
    }

    @Override
    public void setEnabled( boolean enabled ) {
        classifierCode.setEnabled( enabled );
        regNumModification.setEnabled( enabled );
        regNum.setEnabled( enabled );
    }

    public void setHandler( DecimalNumberList boxHandler) {
        this.handler = boxHandler;
    }

    public void showGetNextNumberMessage() {
        msg.addClassName( "hide" );
        getNumberMsg.removeClassName( "hide" );

        markBoxAsError( true );
    }

    public void showMessage( String text, DisplayStyle style ) {
        msg.removeClassName( "hide" );
        getNumberMsg.addClassName( "hide" );
        msg.setClassName( "text text-" + style.name().toLowerCase() );
        msg.setInnerText( text );

        markBoxAsError( style == DisplayStyle.DANGER );
    }

    public void clearBoxState(){
        clearMessage();
        markBoxAsError( false );
    }

    @UiHandler( {"classifierCode", "regNum", "regNumModification"} )
    public void onNumberChanged( KeyUpEvent event ) {
        changeNumberTimer.cancel();
        changeNumberTimer.schedule( 300 );
    }

    @UiHandler( "getNextNumber" )
    public void onGetNextNumber( ClickEvent event ) {
        event.preventDefault();
        if ( handler != null ) {
            handler.onGetNextNumber( this );
        }
    }

    @UiHandler( "isReserve" )
    public void onIsReserveChanged( ClickEvent event ) {
        value.setReserve( isReserve.getValue() );
    }

    @UiHandler( "getNextModification" )
    public void onGetNextNumberModification( ClickEvent event ) {
        event.preventDefault();
        if ( handler != null ) {
            handler.onGetNextModification( this );
        }
    }

    @UiHandler( "remove" )
    public void onRemoveClicked( ClickEvent event ) {
        RemoveEvent.fire( this );
    }

    @UiHandler("next")
    public void onNextClicked(ClickEvent event) {
            AddEvent.fire(this);
    }

    public void setFocusToRegisterNumberField( boolean isFocused ) {
        regNum.setFocus( isFocused );
    }

    private void setFocusToNextButton(boolean isFocused) {
        next.setFocus(isFocused);
    }

    private void checkNextButtonState() {
        next.setEnabled(!value.isEmpty());
    }

    private void markBoxAsError( boolean isError ) {
        if ( isError ) {
            container.addClassName( "has-error" );
            return;
        }

        container.removeClassName( "has-error" );
    }

    private void clearMessage(){
        getNumberMsg.addClassName( "hide" );
        msg.addClassName( "hide" );
        msg.setInnerText( "" );
    }

    private Integer parseValue( String value ) {
        return value == null || value.isEmpty() ? null : Integer.parseInt( value );
    }

    @UiField
    TextBox organizationCode;
    @UiField
    MaskedTextBox regNumModification;
    @UiField
    MaskedTextBox regNum;
    @UiField
    MaskedTextBox classifierCode;
    @UiField
    Element msg;
    @UiField
    @Inject
    Lang lang;
    @UiField
    DivElement container;
    @UiField
    SpanElement getNumberMsg;
    @UiField
    ToggleButton isReserve;
    @UiField
    Button next;

    @Inject
    private En_OrganizationCodeLang organizationCodeLang;

    private DecimalNumberBoxHandler handler;
    private DecimalNumber value = new DecimalNumber();

    private Timer changeNumberTimer = new Timer() {
        @Override
        public void run() {
            value.setClassifierCode( parseValue( classifierCode.getValue() ) );
            value.setRegisterNumber( parseValue( regNum.getValue() ));
            value.setModification( parseValue( regNumModification.getValue() ) );

            checkNextButtonState();
            ValueChangeEvent.fire( DecimalNumberBox.this, value );

            if ( handler == null ) {
                return;
            }

            if ( classifierCode.getText().length() == 6
                    && regNum.getText().length() < 3 ) {
                handler.onGetNextNumber( DecimalNumberBox.this );
            }

            if (regNumModification.getText().length() == 2) {
                setFocusToNextButton(true);
            }
        }
    };

    interface DecimalNumberWidgetUiBinder extends UiBinder<HTMLPanel, DecimalNumberBox> {}
    private static DecimalNumberWidgetUiBinder ourUiBinder = GWT.create( DecimalNumberWidgetUiBinder.class );

}