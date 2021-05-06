package ru.protei.portal.ui.delivery.client.view.table.column;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.util.TransliterationUtils;
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
    public void fillColumnValue( Element cell, Delivery value ) {
        cell.addClassName( "contacts" );

        com.google.gwt.dom.client.Element divElement = DOM.createDiv();

        //TODO remove stub
//        String company = value == null ? null : transliteration(value.getInitiatorCompanyName());
        String company = "Министерство обороны";

        com.google.gwt.dom.client.Element companyElement= DOM.createLabel();
        companyElement.setInnerText( company == null ? "" : company );
        divElement.appendChild( companyElement );

        //TODO remove stub
//        String initiator = value == null ? null : transliteration(value.getInitiatorName());
        String initiator = "Иванов Иван Иванович";

        com.google.gwt.dom.client.Element initiatorElement = DOM.createElement( "p" );
        initiatorElement.setInnerText( initiator == null ? "" : initiator );
        divElement.appendChild( initiatorElement );

        cell.appendChild( divElement );
    }

    private String transliteration(String input) {
        return TransliterationUtils.transliterate(input, LocaleInfo.getCurrentLocale().getLocaleName());
    }

    Lang lang;
}
