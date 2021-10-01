package ru.protei.portal.ui.issue.client.view.table.columns;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.inject.Inject;
import ru.protei.portal.core.model.util.MarkupUtils;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.Date;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

/**
 * Колонка "Описание"
 */
public class InfoColumn extends ClickColumn<CaseShortView>{

    @Inject
    public InfoColumn( Lang lang ) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "info" );
        columnHeader.setInnerText( lang.issueInfo() );
    }

    @Override
    public void fillColumnValue( Element cell, CaseShortView value ) {
        cell.addClassName( "info" );

        Element divElement = DOM.createDiv();

        if ( value.isPrivateCase() ) {
            Element lock = DOM.createElement( "i" );
            lock.addClassName( "fa fa-fw fa-lock text-danger" );
            lock.setAttribute( DEBUG_ID_ATTRIBUTE, DebugIds.TABLE.ISSUE.PRIVACY );
            divElement.appendChild( lock );
        }

        Element productElement = DOM.createLabel();
        productElement.setInnerText( value == null ? "" : value.getProductName() == null ? "" : value.getProductName() );
        productElement.setAttribute( DEBUG_ID_ATTRIBUTE, DebugIds.TABLE.ISSUE.PRODUCT );
        divElement.appendChild( productElement );

        Date created = value == null ? null : value.getCreated();
        if ( created != null ) {
            Element groupElement = DOM.createElement( "p" );
            groupElement.addClassName( "text-semimuted pull-right" );

            Element i = DOM.createElement( "i" );
            i.addClassName( "fa fa-clock-o" );
            groupElement.appendChild( i );

            Element createdElement = DOM.createSpan();
            createdElement.setInnerText( " " + DateFormatter.formatDateTime( created ) );
            createdElement.setAttribute( DEBUG_ID_ATTRIBUTE, DebugIds.TABLE.ISSUE.CREATION_DATE );
            groupElement.appendChild( createdElement );

            divElement.appendChild( groupElement );
        }

        Element nameElement = DOM.createElement( "p" );
        nameElement.setInnerText( value == null ? "" : value.getName() == null ? "" : value.getName() );
        nameElement.setAttribute( DEBUG_ID_ATTRIBUTE, DebugIds.TABLE.ISSUE.NAME );
        divElement.appendChild( nameElement );

        Element infoElement = DOM.createElement( "p" );
        infoElement.addClassName( "issue-description" );
        infoElement.setInnerText( value == null ? "" : value.getInfo() == null? "" : MarkupUtils.removePictureTag(value.getInfo()) );
        infoElement.setAttribute( DEBUG_ID_ATTRIBUTE, DebugIds.TABLE.ISSUE.DESCRIPTION );
        divElement.appendChild( infoElement );

        cell.appendChild( divElement );
    }

    Lang lang;
}
