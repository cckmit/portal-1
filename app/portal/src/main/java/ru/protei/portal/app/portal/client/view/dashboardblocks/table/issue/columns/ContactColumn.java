package ru.protei.portal.app.portal.client.view.dashboardblocks.table.issue.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

public class ContactColumn extends ClickColumn<CaseShortView> {

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
    public void fillColumnValue( Element cell, CaseShortView value ) {
        cell.addClassName( "contacts" );

        com.google.gwt.dom.client.Element divElement = DOM.createDiv();

        String company = value == null ? null : value.getInitiatorCompanyName();
        com.google.gwt.dom.client.Element companyElement= DOM.createLabel();
        companyElement.setInnerText( company == null ? "" : company );
        divElement.appendChild( companyElement );

        com.google.gwt.dom.client.Element initiatorElement = DOM.createElement( "p" );
        initiatorElement.setInnerText( getInitiatorLabel( value ) );

        divElement.appendChild( initiatorElement );

        cell.appendChild( divElement );
    }

    private String getInitiatorLabel(CaseShortView value){
        if(value == null)
            return "";

        String initiatorFio = value.getInitiatorShortName();
        if(initiatorFio == null || initiatorFio.isEmpty())
            initiatorFio = value.getInitiatorName();

        return initiatorFio;
    }

    Lang lang;
}
