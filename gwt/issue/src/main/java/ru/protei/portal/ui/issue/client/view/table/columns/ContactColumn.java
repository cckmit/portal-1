package ru.protei.portal.ui.issue.client.view.table.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Колонка "Клиент"
 */
public class ContactColumn extends ClickColumn< CaseObject > {

    @Inject
    public ContactColumn( Lang lang ) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "contacts" );
        columnHeader.setInnerText( lang.issueContacts() );
    }

    @Override
    public void fillColumnValue( Element cell, CaseObject value ) {
        cell.addClassName( "contacts" );

        com.google.gwt.dom.client.Element divElement = DOM.createDiv();

        Company company = value == null ? null : value.getInitiatorCompany();
        com.google.gwt.dom.client.Element companyElement= DOM.createLabel();
        companyElement.setInnerText( company == null ? "" : company.getCname() );
        divElement.appendChild( companyElement );

        Person initiator = value == null ? null : value.getInitiator();
        com.google.gwt.dom.client.Element initiatorElement = DOM.createElement( "p" );
        initiatorElement.setInnerText( initiator == null ? "" : initiator.getDisplayName() );
        divElement.appendChild( initiatorElement );

        cell.appendChild( divElement );
    }

    Lang lang;
}
