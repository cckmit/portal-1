package ru.protei.portal.ui.ipreservation.client.view.reservedip.table.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Колонка "Информация о последней проверке"
 */
public class LastCheckColumn extends ClickColumn<ReservedIp> {

    @Inject
    public LastCheckColumn(Lang lang) { this.lang = lang; }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("ip-last-check");
        columnHeader.setInnerText(lang.reservedIpCheck());
    }

    @Override
    public void fillColumnValue(Element cell, ReservedIp value) {
        cell.addClassName("ip-last-check");

        if ( value == null ) { return; }

        com.google.gwt.dom.client.Element divElement = DOM.createDiv();

        if (value.getLastActiveDate() != null) {
            com.google.gwt.dom.client.Element checkDateElement = DOM.createElement("p");
            checkDateElement.setInnerText(DateFormatter.formatDateTime(value.getLastActiveDate()));
            divElement.appendChild(checkDateElement);
        }
        if (value.getLastCheckInfo() != null) {
            com.google.gwt.dom.client.Element checkInfoElement = DOM.createElement("p");
            checkInfoElement.setInnerText( value.getLastCheckInfo() );
            divElement.appendChild(checkInfoElement);
        }

        cell.appendChild( divElement );
    }

    private Lang lang;
}
