package ru.protei.portal.ui.equipment.client.widget.number.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.events.AddHandler;
import ru.protei.portal.ui.common.client.events.HasAddHandlers;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.mask.MaskedTextBox;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.ui.common.client.widget.selector.event.*;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.equipment.client.provider.AbstractDecimalNumberDataProvider;
import ru.protei.portal.ui.equipment.client.widget.selector.OrganizationCodeSelector;
import ru.protei.winter.web.common.client.common.DisplayStyle;

import java.util.Set;

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

        organizationCode.setValue( value.getOrganizationCode() );
        classifierCode.setText( value.getClassifierCode() == null ? null : NumberFormat.getFormat("000000").format( value.getClassifierCode() ) );
        regNum.setText( value.getRegisterNumber() == null ? null : NumberFormat.getFormat("000").format( value.getRegisterNumber() ) );
        regNumModification.setText( value.getModification() == null ? null : NumberFormat.getFormat("00").format( value.getModification() ) );
        isReserve.setValue( value.isReserve() );

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

    @UiHandler( "regNum" )
    public void onRegNumChanged( KeyUpEvent event ) {
        value.setRegisterNumber( Integer.parseInt( regNum.getValue() ) );
        ValueChangeEvent.fire( this, value );

        if ( regNum.getText().length() == 3 ) {
            checkExistNumber();
            return;
        }

        clearMessage();
    }

    @UiHandler( "classifierCode" )
    public void onClassifierCodeChanged( KeyUpEvent event ) {
        value.setClassifierCode( Integer.parseInt( classifierCode.getValue() ) );
        ValueChangeEvent.fire( this, value );

        markBoxAsError( false );
        if ( classifierCode.getText().length() == 6 ) {
            fillNextAvailableNumber();
            return;
        }

        clearMessage();
    }

    @UiHandler( "regNumModification" )
    public void onRegNumModificationChanged( KeyUpEvent event ) {
        String modificationString = regNumModification.getValue();
        this.value.setModification( modificationString == null || modificationString.isEmpty() ? null : Integer.parseInt( modificationString ) );
        ValueChangeEvent.fire( this, this.value );

        checkExistNumber();

        if (regNumModification.getText().length() == 2) {
            setFocusToNextButton(true);
        }
    }

    @UiHandler( "getNextNumber" )
    public void onGetNextNumber( ClickEvent event ) {
        event.preventDefault();
        fillNextAvailableNumber();
    }

    @UiHandler( "isReserve" )
    public void onIsReserveChanged( ClickEvent event ) {
        value.setReserve( isReserve.getValue() );
    }

    @UiHandler( "organizationCode" )
    public void onOrganizationCodeChanged( ValueChangeEvent<En_OrganizationCode> event ) {
        value.setOrganizationCode( organizationCode.getValue() );
    }

    @UiHandler( "getNextModification" )
    public void onGetNextNumberModification( ClickEvent event ) {
        event.preventDefault();
        fillNextAvailableNumberModification();
    }

    @UiHandler( "remove" )
    public void onRemoveClicked( ClickEvent event ) {
        RemoveEvent.fire( this );
    }

    @UiHandler("next")
    public void onNextClicked(KeyUpEvent event) {
        if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            AddEvent.fire(this);
        }
    }

    public HasEnabled enabledOrganizationCode() {
        return organizationCode;
    }

    public void fillOrganizationCodesOption( Set< En_OrganizationCode > availableValues ) {
        organizationCode.fillOptions( availableValues );
    }

    public void setFocusToNextButton(boolean isFocused) {
        next.setFocus(isFocused);
    }

    private void checkExistNumber() {
        dataProvider.checkIfExistDecimalNumber( value, new RequestCallback< Boolean >() {
            @Override
            public void onError( Throwable throwable ) {
                showMessage( lang.equipmentErrorCheckNumber(), DisplayStyle.DANGER );
            }

            @Override
            public void onSuccess( Boolean result ) {
                markBoxAsError( result );
                if ( result ) {
                    showGetNextNumberMessage();
                    return;
                }

                showMessage( lang.equipmentDecimalNumberEmpty(), DisplayStyle.SUCCESS );
            }
        } );
    }

    private void markBoxAsError( boolean isError ) {
        if ( isError ) {
            container.addClassName( "has-error" );
            return;
        }

        container.removeClassName( "has-error" );
    }

    private void fillNextAvailableNumber() {
        dataProvider.getNextAvailableRegisterNumber( value, new RequestCallback< DecimalNumber>() {
            @Override
            public void onError( Throwable throwable ) {
                showMessage( lang.equipmentErrorGetNextAvailableNumber(), DisplayStyle.DANGER );
            }

            @Override
            public void onSuccess( DecimalNumber result ) {
                markBoxAsError( false );
                value.setRegisterNumber( result.getRegisterNumber() );
                regNum.setText( value.getRegisterNumber() == null ? null : NumberFormat.getFormat("000").format( value.getRegisterNumber() ) );
                showMessage( lang.equipmentDecimalNumberEmpty(), DisplayStyle.SUCCESS );
            }
        } );
    }

    private void fillNextAvailableNumberModification() {
        dataProvider.getNextAvailableRegisterNumberModification( value, new RequestCallback< DecimalNumber>() {
            @Override
            public void onError( Throwable throwable ) {
                showMessage( lang.equipmentErrorGetNextAvailableNumber(), DisplayStyle.DANGER );
            }

            @Override
            public void onSuccess( DecimalNumber result ) {
                markBoxAsError( false );
                value.setModification( result.getModification() );
                regNumModification.setText( value.getModification() == null ? null : NumberFormat.getFormat("00").format( value.getModification() ) );
                showMessage( lang.equipmentDecimalNumberEmpty(), DisplayStyle.SUCCESS );
            }
        } );
    }

    private void showGetNextNumberMessage() {
        msg.addClassName( "hide" );
        getNumberMsg.removeClassName( "hide" );
    }
    private void showMessage( String text, DisplayStyle style ) {
        msg.removeClassName( "hide" );
        getNumberMsg.addClassName( "hide" );
        msg.setClassName( "text text-" + style.name().toLowerCase() );
        msg.setInnerText( text );
    }
    private void clearMessage(){
        getNumberMsg.addClassName( "hide" );
        msg.addClassName( "hide" );
        msg.setInnerText( "" );
    }
    @Inject
    @UiField(provided = true)
    OrganizationCodeSelector organizationCode;

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
    AbstractDecimalNumberDataProvider dataProvider;

    private DecimalNumber value = new DecimalNumber();

    interface DecimalNumberWidgetUiBinder extends UiBinder<HTMLPanel, DecimalNumberBox> {}
    private static DecimalNumberWidgetUiBinder ourUiBinder = GWT.create( DecimalNumberWidgetUiBinder.class );

}