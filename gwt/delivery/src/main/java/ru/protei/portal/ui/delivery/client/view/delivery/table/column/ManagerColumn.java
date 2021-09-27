package ru.protei.portal.ui.delivery.client.view.delivery.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Колонка "Ответственный"
 */
public class ManagerColumn extends ClickColumn<Delivery> {

    @Inject
    public ManagerColumn(Lang lang ) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "manager" );
        columnHeader.setInnerText( lang.deliveryColumnManager() );
    }

    @Override
    public void fillColumnValue( Element cell, Delivery delivery ) {
        if ( delivery == null ) {
            return;
        }

        cell.addClassName( "manager" );

        com.google.gwt.dom.client.Element divElement = DOM.createDiv();

        String manager = delivery.getProject() == null ? null : delivery.getProject().getManagerFullName();

        com.google.gwt.dom.client.Element managerElement = DOM.createElement( "p" );
        managerElement.setInnerText( manager == null ? "" : manager );
        divElement.appendChild( managerElement );

        cell.appendChild( divElement );
    }

    Lang lang;
}
