package ru.protei.portal.ui.equipment.client.widget.number.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.lang.OrganizationCodeLang;
import ru.protei.portal.ui.common.client.widget.mask.MaskedTextBox;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.dict.En_OrganizationCode;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.equipment.client.provider.AbstractDecimalNumberDataProvider;
import ru.protei.winter.web.common.client.common.DisplayStyle;

/**
 * Вид виджета децимального номера
 */
public class DecimalNumberBox
        extends Composite implements HasValue<DecimalNumber>, HasEnabled {


    public DecimalNumberBox() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
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
            value.setOrganizationCode( organizationCode );
        }

        classifierCode.setText( value.getClassifierCode() == null ? null : value.getClassifierCode().toString() );
        regNum.setText( value.getRegisterNumber() == null ? null : value.getRegisterNumber().toString() );
        regNumModification.setText( value.getModification() == null ? null : value.getModification().toString() );

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

        if ( !validable ) {
            return;
        }

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

        if ( !validable ) {
            return;
        }

        markBoxAsError( false );
        if ( classifierCode.getText().length() == 6 ) {
            fillNextAvailableNumber();
            return;
        }

        clearMessage();
    }

    @UiHandler( "regNumModification" )
    public void onRegNumModificationChanged( KeyUpEvent event ) {
        value.setRegisterNumber( Integer.parseInt( regNum.getValue() ) );
        ValueChangeEvent.fire( this, value );

        if ( !validable ) {
            return;
        }

        checkExistNumber();
    }


    @UiHandler( "getNextNumber" )
    public void onGetNextNumber( ClickEvent event ) {
        event.preventDefault();
        fillNextAvailableNumber();
    }

    @UiHandler( "getNextModification" )
    public void onGetNextNumberModification( ClickEvent event ) {
        event.preventDefault();
        fillNextAvailableNumberModification();
    }

    public void setClassifierCode( Integer classifierCode ) {
        value.setClassifierCode( classifierCode );
        this.classifierCode.setValue( classifierCode == null ? "" : classifierCode.toString() );
    }

    public void setOrganizationCode( En_OrganizationCode code ) {
        this.organizationCode = code;
        value.setOrganizationCode( organizationCode );
        organizationCodeName.setInnerText( organizationCodeLang.getName( code ) );
    }

    public void setValidable( boolean validable ) {
        this.validable = validable;
    }

    private void checkExistNumber() {
        dataProvider.checkIfExistDecimalNumber( value, new RequestCallback< Boolean >() {
            @Override
            public void onError( Throwable throwable ) {
                showMessage( "Ошибка при проверке номера", DisplayStyle.DANGER );
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
                showMessage(  "Ошибка при получении следующего свободного номера", DisplayStyle.DANGER );
            }

            @Override
            public void onSuccess( DecimalNumber result ) {
                markBoxAsError( false );
                setValue( result );
                showMessage( lang.equipmentDecimalNumberEmpty(), DisplayStyle.SUCCESS );
            }
        } );
    }

    private void fillNextAvailableNumberModification() {
        dataProvider.getNextAvailableRegisterNumberModification( value, new RequestCallback< DecimalNumber>() {
            @Override
            public void onError( Throwable throwable ) {
                showMessage(  "Ошибка при получении следующего свободного номера", DisplayStyle.DANGER );
            }

            @Override
            public void onSuccess( DecimalNumber result ) {
                markBoxAsError( false );
                setValue( result );
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

    @UiField
    LabelElement organizationCodeName;
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

    @Inject
    AbstractDecimalNumberDataProvider dataProvider;

    @Inject
    private OrganizationCodeLang organizationCodeLang;

    private boolean validable = true;
    private DecimalNumber value = new DecimalNumber();
    private En_OrganizationCode organizationCode;

    interface DecimalNumberWidgetUiBinder extends UiBinder<HTMLPanel, DecimalNumberBox> {}
    private static DecimalNumberWidgetUiBinder ourUiBinder = GWT.create( DecimalNumberWidgetUiBinder.class );

}