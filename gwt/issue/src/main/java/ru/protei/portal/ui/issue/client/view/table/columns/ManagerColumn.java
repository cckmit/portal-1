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

        Company company = value == null ? null : value.getManager() == null ? null : value.getManager().getCompany();
        com.google.gwt.dom.client.Element companyElement= DOM.createLabel();
        companyElement.setInnerText( company == null ? "" : company.getCname() );
        cell.appendChild( companyElement );

        Person manager = value == null ? null : value.getManager();
        com.google.gwt.dom.client.Element managerElement = DOM.createDiv();
        managerElement.setInnerText( manager == null ? "" : manager.getDisplayName() );
        cell.appendChild( managerElement );
    }

    Lang lang;
}
