package ru.protei.portal.ui.delivery.client.view.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.En_DeliveryTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 *  серийный номер первого комплекта, тип поставки, статус поставки
 */
public class NumberColumn extends ClickColumn<Delivery> {

    @Inject
    public NumberColumn( Lang lang,
                         En_DeliveryTypeLang typeLang) {
        this.lang = lang;
        this.typeLang = typeLang;
        setStopPropogationElementClassName("number-size");
    }

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "number" );
        columnHeader.setInnerText( lang.deliveryColumnNumber() );
    }

    @Override
    public void fillColumnValue( Element cell, Delivery value ) {
        if ( value == null ) {
            return;
        }

        cell.addClassName( "number" );
        com.google.gwt.dom.client.Element divElement = DOM.createDiv();

        com.google.gwt.dom.client.Element numberElement = DOM.createElement( "p" );
        numberElement.addClassName( "number-size" );
        numberElement.setInnerText(getFirstKitSerialNumber(value));
        divElement.appendChild( numberElement );

        com.google.gwt.dom.client.Element typeElement = DOM.createElement( "p" );
        typeElement.addClassName( "delivery-type" );
        typeElement.setInnerText( typeLang.getName(value.getType()) );
        divElement.appendChild( typeElement );

        com.google.gwt.dom.client.Element stateElement = DOM.createElement("p");
        stateElement.addClassName("label");
        stateElement.getStyle().setBackgroundColor(value.getStateColor());
        stateElement.setInnerText(value.getStateName());

        divElement.appendChild( stateElement );

        cell.appendChild( divElement );
    }

    private String getFirstKitSerialNumber(Delivery value) {
        //TODO how to get first KIT?
        return "111.222";
//        return value.getKit() == null ? "" : value.getKit().getSerialNumber();
    }

    private Lang lang;
    private En_DeliveryTypeLang typeLang;
}
