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
 * Колонка "Менеджер"
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
    public void fillColumnValue( Element cell, Delivery value ) {
        cell.addClassName( "manager" );

        com.google.gwt.dom.client.Element divElement = DOM.createDiv();

        //TODO remove stub
//        String company = value == null ? null : transliteration(value.getManagerCompanyName());
        String company = "Протей СТ";

        com.google.gwt.dom.client.Element companyElement= DOM.createLabel();
        companyElement.setInnerText( company == null ? "" : company );
        divElement.appendChild( companyElement );

        //TODO remove stub
//        String manager = value == null ? null : transliteration(value.getManagerName());
        String manager = "Какой-то сотрудник Протея";

        com.google.gwt.dom.client.Element managerElement = DOM.createElement( "p" );
        managerElement.setInnerText( manager == null ? "" : manager );
        divElement.appendChild( managerElement );

        cell.appendChild( divElement );
    }

    private String transliteration(String input) {
        return TransliterationUtils.transliterate(input, LocaleInfo.getCurrentLocale().getLocaleName());
    }

    Lang lang;
}
