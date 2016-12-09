package ru.protei.portal.ui.crm.client.view.dashboardblocks.table.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Колонка "Менеджер"
 */
public class ManagerColumn extends ClickColumn< CaseObject > {

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
    public void fillColumnValue( Element cell, CaseObject value ) {
        cell.addClassName( "manager" );

        com.google.gwt.dom.client.Element divElement = DOM.createDiv();

        Company company = value == null ? null : value.getManager() == null ? null : value.getManager().getCompany();
        com.google.gwt.dom.client.Element companyElement= DOM.createLabel();
        companyElement.setInnerText( company == null ? "" : company.getCname() );
        divElement.appendChild( companyElement );

        Person manager = value == null ? null : value.getManager();
        com.google.gwt.dom.client.Element managerElement = DOM.createElement( "p" );


        managerElement.setInnerText( getManagerLabel(manager) );

        divElement.appendChild( managerElement );
        cell.appendChild( divElement );
    }

    private String getManagerLabel(Person manager){
        if(manager == null)
            return "";

        String managerFio = manager.getDisplayShortName();
        if(managerFio == null || managerFio.isEmpty())
            managerFio = manager.getDisplayName();

        return managerFio;
    }

    Lang lang;
}
