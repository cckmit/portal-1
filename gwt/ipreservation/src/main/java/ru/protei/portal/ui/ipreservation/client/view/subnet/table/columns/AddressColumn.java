package ru.protei.portal.ui.ipreservation.client.view.subnet.table.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Колонка "Адрес"
 */
public class AddressColumn extends ClickColumn<Subnet> {

    @Inject
    public AddressColumn(Lang lang) { this.lang = lang; }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.setInnerText(lang.address());
    }

    @Override
    public void fillColumnValue(Element cell, Subnet value) {
        if ( value == null ) { return; }

        cell.addClassName( "address" );
        Element divElement = DOM.createDiv();

        Element addrElement = DOM.createElement( "p" );
        addrElement.addClassName( "address-size" );
        String address = value.getAddress();

        if (value.getMask() != null) {
            address += "." + value.getMask();
        }
        addrElement.setInnerText( address );
        divElement.appendChild( addrElement );
        cell.appendChild( divElement );
    }

    private Lang lang;
}
