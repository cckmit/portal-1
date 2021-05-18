package ru.protei.portal.ui.delivery.client.view.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Колонка "Контакты"
 */
public class ContactColumn extends ClickColumn<Delivery> {

    @Inject
    public ContactColumn(Lang lang ) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "contacts" );
        columnHeader.setInnerText( lang.deliveryColumnContacts() );
    }

    @Override
    public void fillColumnValue( Element cell, Delivery delivery ) {
        if ( delivery == null ) {
            return;
        }

        cell.addClassName( "contacts" );

        com.google.gwt.dom.client.Element divElement = DOM.createDiv();

        ProjectInfo projectInfo = ProjectInfo.fromProject(delivery.getProject());

        String company = projectInfo == null || projectInfo.getContragent() == null ? null : projectInfo.getContragent().getDisplayText();
        com.google.gwt.dom.client.Element companyElement= DOM.createLabel();
        companyElement.setInnerText( company == null ? "" : company );
        divElement.appendChild( companyElement );

        String initiator = delivery.getInitiator() == null ? null : delivery.getInitiator().getDisplayName();
        com.google.gwt.dom.client.Element initiatorElement = DOM.createElement( "p" );
        initiatorElement.setInnerText( initiator == null ? "" : initiator );
        divElement.appendChild( initiatorElement );

        cell.appendChild( divElement );
    }

    Lang lang;
}
