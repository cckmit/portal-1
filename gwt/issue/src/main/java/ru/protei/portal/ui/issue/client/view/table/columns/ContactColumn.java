package ru.protei.portal.ui.issue.client.view.table.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.ui.common.client.util.ClientTransliterationUtils.transliteration;

/**
 * Колонка "Клиент"
 */
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

        String company = value == null ? null : transliteration(value.getInitiatorCompanyName());
        com.google.gwt.dom.client.Element companyElement= DOM.createLabel();
        companyElement.setInnerText( company == null ? "" : company );
        divElement.appendChild( companyElement );

        String initiator = value == null ? null : transliteration(value.getInitiatorName());
        com.google.gwt.dom.client.Element initiatorElement = DOM.createElement( "p" );
        initiatorElement.setInnerText( initiator == null ? "" : initiator );
        divElement.appendChild( initiatorElement );

        cell.appendChild( divElement );
    }

    Lang lang;
}
