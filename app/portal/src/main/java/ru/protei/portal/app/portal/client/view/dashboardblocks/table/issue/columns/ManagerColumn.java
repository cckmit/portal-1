package ru.protei.portal.app.portal.client.view.dashboardblocks.table.issue.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

public class ManagerColumn extends ClickColumn<CaseShortView> {

    @Inject
    public ManagerColumn( Lang lang ) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "manager" );
        columnHeader.setInnerText( lang.issueManager() );
    }

    @Override
    public void fillColumnValue( Element cell, CaseShortView value ) {
        cell.addClassName( "manager" );

        com.google.gwt.dom.client.Element divElement = DOM.createDiv();

        String company = value == null ? null : value.getManagerCompanyName();
        com.google.gwt.dom.client.Element companyElement= DOM.createLabel();
        companyElement.setInnerText( company == null ? "" : company );
        divElement.appendChild( companyElement );

        com.google.gwt.dom.client.Element managerElement = DOM.createElement( "p" );
        managerElement.setInnerText( getManagerLabel(value) );

        divElement.appendChild( managerElement );
        cell.appendChild( divElement );
    }

    private String getManagerLabel(CaseShortView value){
        if(value == null)
            return "";

        String managerFio = value.getManagerShortName();
        if(managerFio == null || managerFio.isEmpty())
            managerFio = value.getManagerName();

        return managerFio;
    }

    Lang lang;
}
