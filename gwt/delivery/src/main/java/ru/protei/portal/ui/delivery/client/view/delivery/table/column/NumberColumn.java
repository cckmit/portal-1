package ru.protei.portal.ui.delivery.client.view.delivery.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.DeliveryStateLang;
import ru.protei.portal.ui.common.client.lang.En_DeliveryTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 *  серийный номер первого комплекта, тип поставки, статус поставки
 */
public class NumberColumn extends ClickColumn<Delivery> {

    @Inject
    public NumberColumn( Lang lang,
                         En_DeliveryTypeLang typeLang,
                         DeliveryStateLang stateLang) {
        this.lang = lang;
        this.typeLang = typeLang;
        this.stateLang = stateLang;
        setStopPropogationElementClassName("number-size");
    }

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "number" );
        columnHeader.setInnerText( lang.deliveryColumnNumber() );
    }

    @Override
    public void fillColumnValue( Element cell, Delivery delivery ) {
        if ( delivery == null ) {
            return;
        }

        cell.addClassName( "number" );
        com.google.gwt.dom.client.Element divElement = DOM.createDiv();

        com.google.gwt.dom.client.Element numberElement = DOM.createElement( "p" );
        numberElement.addClassName( "number-size" );
        numberElement.setInnerText(delivery.getNumber());
        divElement.appendChild( numberElement );

        com.google.gwt.dom.client.Element typeElement = DOM.createElement( "p" );
        typeElement.addClassName( "delivery-type" );
        typeElement.setInnerText( typeLang.getName(delivery.getType()) );
        divElement.appendChild( typeElement );

        com.google.gwt.dom.client.Element stateElement = DOM.createElement("p");
        stateElement.addClassName("label");
        CaseState caseState = delivery.getState();
        stateElement.getStyle().setBackgroundColor(caseState == null ? null : caseState.getColor());
        stateElement.setInnerText(caseState == null ? null : stateLang.getStateName(caseState));

        divElement.appendChild( stateElement );

        cell.appendChild( divElement );
    }

    private Lang lang;
    private En_DeliveryTypeLang typeLang;
    private DeliveryStateLang stateLang;
}
