package ru.protei.portal.ui.delivery.client.view.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_DeliveryStatus;
import ru.protei.portal.core.model.dict.En_DeliveryType;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.En_DeliveryStatusLang;
import ru.protei.portal.ui.common.client.lang.En_DeliveryTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.core.model.helper.StringUtils.firstUppercaseChar;
import static ru.protei.portal.ui.common.client.util.ColorUtils.makeContrastColor;

/**
 *  серийный номер первого комплекта, тип поставки, статус поставки
 */
public class NumberColumn extends ClickColumn<Delivery> {

    @Inject
    public NumberColumn( Lang lang,
                         En_DeliveryStatusLang statusLang,
                         En_DeliveryTypeLang typeLang) {
        this.lang = lang;
        this.statusLang = statusLang;
        this.typeLang = typeLang;
        setStopPropogationElementClassName("number-size");
    }

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "number" );
        columnHeader.setInnerText( lang.deliveryNumber() );
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
        numberElement.setInnerText( value.getKit() == null ? "" : value.getKit().getSerialNumber() );
        divElement.appendChild( numberElement );

        com.google.gwt.dom.client.Element typeElement = DOM.createElement( "p" );
        typeElement.addClassName( "delivery-type" );
        typeElement.setInnerText( typeLang.getName(value.getType()) );
        divElement.appendChild( typeElement );

        com.google.gwt.dom.client.Element stateElement = DOM.createElement("p");
        stateElement.addClassName("label");
        stateElement.getStyle().setBackgroundColor(statusColor.getColor(value.getStatus()));
        stateElement.setInnerText(statusLang.getName(value.getStatus()));

        divElement.appendChild( stateElement );

        cell.appendChild( divElement );
    }

    private Lang lang;
    private En_DeliveryStatusLang statusLang;
    private DeliveryStatusColor statusColor = new DeliveryStatusColor();
    private En_DeliveryTypeLang typeLang;

    //TODO remove stub
    private class DeliveryStatusColor {
        public String getColor(En_DeliveryStatus status) {
            if (status == null){
                return "";
            }
            switch (status) {
                case PRELIMINARY:
                    return "#ef5350";
                case PRE_RESERVE:
                    return "#42a5f5";
                case RESERVE:
                    return "#e6e6e6";
                case ASSEMBLY:
                    return "#868686";
                case TEST:
                    return "#607D8B";
                case READY:
                    return "#000000";
                case SENT:
                    return "#00bcd4";
                case WORK:
                    return "#580505";
                default:
                    return "";
            }
        }
    }
}
