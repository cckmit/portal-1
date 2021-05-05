package ru.protei.portal.ui.delivery.client.view.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

/**
 * продукты, наименование, описание, дата отправки
 */
public class InfoColumn extends ClickColumn<Delivery>{

    @Inject
    public InfoColumn(Lang lang ) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "info" );
        columnHeader.setInnerText( lang.deliveryInfo() );
    }

    @Override
    public void fillColumnValue( Element cell, Delivery value ) {
        cell.addClassName( "info" );

        Element divElement = DOM.createDiv();

        Element productElement = DOM.createLabel();
        //TODO remove stub
        List<String> ts = Arrays.asList("ProductName1", "ProductName2", "ProductName3");
        productElement.setInnerText(StringUtils.join(ts,","));
//        productElement.setInnerText( value == null ? "" : value.getName() == null ? "" : value.getProductName() );

        productElement.setAttribute( DEBUG_ID_ATTRIBUTE, DebugIds.TABLE.DELIVERY.PRODUCT );
        divElement.appendChild( productElement );

        Date delivered = value == null ? null : value.getDelivered();
        if ( delivered != null ) {
            Element groupElement = DOM.createElement( "p" );
            groupElement.addClassName( "text-semimuted pull-right" );

            Element i = DOM.createElement( "i" );
            i.addClassName( "fas fa-shipping-fast" );
            groupElement.appendChild( i );

            Element createdElement = DOM.createSpan();
            createdElement.setInnerText( " " + DateFormatter.formatDateTime( delivered ) );
            createdElement.setAttribute( DEBUG_ID_ATTRIBUTE, DebugIds.TABLE.DELIVERY.DELIVERY_DATE );
            groupElement.appendChild( createdElement );

            divElement.appendChild( groupElement );
        }

        Element nameElement = DOM.createElement( "p" );
        nameElement.setInnerText( value == null ? "" : value.getName() == null ? "" : value.getName() );
        nameElement.setAttribute( DEBUG_ID_ATTRIBUTE, DebugIds.TABLE.DELIVERY.NAME);
        divElement.appendChild( nameElement );

        Element descElement = DOM.createElement( "p" );
        descElement.setInnerText( value == null ? "" : value.getDescription()== null? "" : value.getDescription() );
        descElement.setAttribute( DEBUG_ID_ATTRIBUTE, DebugIds.TABLE.DELIVERY.DESCRIPTION );
        divElement.appendChild( descElement );

        cell.appendChild( divElement );
    }

    Lang lang;
}
