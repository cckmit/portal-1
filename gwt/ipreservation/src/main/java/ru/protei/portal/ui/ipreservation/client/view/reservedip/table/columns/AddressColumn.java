package ru.protei.portal.ui.ipreservation.client.view.reservedip.table.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Колонка "Адрес"
 */
public class AddressColumn extends ClickColumn<ReservedIp> {

    @Inject
    public AddressColumn(Lang lang) { this.lang = lang; }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("ip-address");
        columnHeader.setInnerText(lang.address());
    }

    @Override
    public void fillColumnValue(Element cell, ReservedIp value) {
        if ( value == null ) { return; }

        cell.addClassName( "ip-address" );
        com.google.gwt.dom.client.Element divElement = DOM.createDiv();

        com.google.gwt.dom.client.Element ipElement = DOM.createElement( "p" );
        ipElement.addClassName( "address-size" );
        ipElement.setInnerText( value.getIpAddress() );
        divElement.appendChild( ipElement );

        if (StringUtils.isNotBlank(value.getMacAddress())) {
            com.google.gwt.dom.client.Element macElement = DOM.createElement("p");
            macElement.addClassName( "small" );
            macElement.setInnerText( "[" + value.getMacAddress() + "]");
            divElement.appendChild(macElement);
        }
        cell.appendChild( divElement );
    }

    private Lang lang;
}
