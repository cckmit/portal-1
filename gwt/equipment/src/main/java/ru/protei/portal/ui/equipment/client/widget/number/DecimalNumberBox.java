package ru.protei.portal.ui.equipment.client.widget.number;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.ParagraphElement;
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
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;
import ru.protei.portal.ui.common.shared.model.DecimalNumber;
import ru.protei.portal.ui.common.shared.model.OrganizationCode;
import ru.protei.portal.ui.equipment.client.provider.AbstractDecimalNumberDataProvider;

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

        value.setOrganizationCode( organizationCode );
        classifierCode.setText( value.getClassifierCode() == null ? null : value.getClassifierCode());
        regNum.setText( value.getRegisterNumber() == null ? null : value.getRegisterNumber() );
        regNumModification.setText( value.getModification() == null ? null : value.getModification() );

        if ( fireEvents ) {
            ValueChangeEvent.fire( this, decimalNumber );
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<DecimalNumber> handler ) {
        return addHandler( handler, ValueChangeEvent.getType() );
    }

    public void setOrganizationCode( OrganizationCode organizationCode ) {
        this.organizationCode = organizationCode;
        organizationCodeName.setInnerText( organizationCodeLang.getName( organizationCode ) );
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
        value.setRegisterNumber( regNum.getValue() );
        ValueChangeEvent.fire( this, value );

//        if ( regNum.getText().length() == 3 ) {
//            msg.setInnerText( "res: " + dataProvider.checkIfExistDecimalNumber( value ) );
//        }
    }

    @UiHandler( "classifierCode" )
    public void onClassifierCodeChanged( KeyUpEvent event ) {
        value.setClassifierCode( classifierCode.getValue() );
        ValueChangeEvent.fire( this, value );

//        if ( regNum.getText().length() == 6 ) {
//            msg.setInnerText( "res: " + dataProvider.checkIfExistDecimalNumber( value ) );
//        }
    }

    @UiHandler( "regNumModification" )
    public void onRegNumModificationChanged( KeyUpEvent event ) {
        value.setRegisterNumber( regNum.getValue() );
        ValueChangeEvent.fire( this, value );

//        msg.setInnerText( "res: " + dataProvider.checkIfExistDecimalNumber( value ) );
    }

    public void setClassifierCode( String classifierCode ) {
        value.setClassifierCode( classifierCode );
        this.classifierCode.setValue( classifierCode );
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
    ParagraphElement msg;

    @UiField
    @Inject
    Lang lang;

    @Inject
    AbstractDecimalNumberDataProvider dataProvider;

    @Inject
    private OrganizationCodeLang organizationCodeLang;

    private DecimalNumber value;
    private OrganizationCode organizationCode;

    interface DecimalNumberWidgetUiBinder extends UiBinder<HTMLPanel, DecimalNumberBox> {}
    private static DecimalNumberWidgetUiBinder ourUiBinder = GWT.create( DecimalNumberWidgetUiBinder.class );

}