package ru.protei.portal.ui.issue.client.view.table.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.Date;

/**
 * Колонка "Описание"
 */
public class InfoColumn extends ClickColumn< CaseObject >{

    @Inject
    public InfoColumn( Lang lang, DateFormatter dateFormatter ) {
        this.lang = lang;
        this.dateFormatter = dateFormatter;
    }

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "info" );
        columnHeader.setInnerText( lang.issueInfo() );
    }

    @Override
    public void fillColumnValue( Element cell, CaseObject value ) {
        cell.addClassName( "info" );

        com.google.gwt.dom.client.Element divElement = DOM.createDiv();

        com.google.gwt.dom.client.Element productElement = DOM.createLabel();
        productElement.setInnerText( value == null ? "" : value.getProduct() == null ? "" : value.getProduct().getName() );
        divElement.appendChild( productElement );

        Date created = value == null ? null : value.getCreated();
        if ( created != null ) {
            com.google.gwt.dom.client.Element groupElement = DOM.createElement( "p" );
            groupElement.addClassName( "text-semimuted" );

            com.google.gwt.dom.client.Element i = DOM.createElement( "i" );
            i.addClassName( "fa fa-clock-o" );
            groupElement.appendChild( i );

            com.google.gwt.dom.client.Element createdElement = DOM.createSpan();
            createdElement.setInnerText( " " + dateFormatter.formatDateTime( created ) );
            groupElement.appendChild( createdElement );

            divElement.appendChild( groupElement );
        }

        com.google.gwt.dom.client.Element infoElement = DOM.createElement( "p" );
        infoElement.addClassName( "issue-description" );
        infoElement.setInnerText( value == null ? "" : value.getInfo() );
        divElement.appendChild( infoElement );

        cell.appendChild( divElement );
    }

    Lang lang;

    DateFormatter dateFormatter;
}
