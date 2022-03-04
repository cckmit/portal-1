package ru.protei.portal.ui.issue.client.view.table.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.ui.common.client.util.ClientTransliterationUtils.transliteration;

/**
 * Колонка "Менеджер"
 */
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

        String company = value == null ? null : transliteration(value.getManagerCompanyName());
        com.google.gwt.dom.client.Element companyElement= DOM.createLabel();
        companyElement.setInnerText( company == null ? "" : company );
        divElement.appendChild( companyElement );

        String manager = value == null ? null : transliteration(value.getManagerName());
        com.google.gwt.dom.client.Element managerElement = DOM.createElement( "p" );
        managerElement.setInnerText( manager == null ? "" : manager );
        divElement.appendChild( managerElement );

        cell.appendChild( divElement );
    }

    Lang lang;
}
