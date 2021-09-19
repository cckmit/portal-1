package ru.protei.portal.ui.delivery.client.view.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dto.ProjectInfo;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.Date;

import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.joining;

/**
 * продукты, наименование, описание, дата отправки
 */
public class InfoColumn extends ClickColumn<Delivery>{

    @Inject
    public InfoColumn(Lang lang ) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "info" );
        columnHeader.setInnerText( lang.deliveryColumnInfo() );
    }

    @Override
    public void fillColumnValue( Element cell, Delivery delivery ) {

        if ( delivery == null ) {
            return;
        }

        cell.addClassName( "info" );

        Element divElement = DOM.createDiv();

        Element productElement = DOM.createLabel();
        ProjectInfo projectInfo = ProjectInfo.fromProject(delivery.getProject());
        if (projectInfo != null && isNotEmpty(projectInfo.getProducts())){
            productElement.setInnerText( joining(projectInfo.getProducts(), ", ", ProductShortView::getName) );
            divElement.appendChild( productElement );
        }

        Date delivered = delivery.getDepartureDate();
        if ( delivered != null ) {
            Element groupElement = DOM.createElement( "p" );
            groupElement.addClassName( "text-semimuted pull-right" );

            Element i = DOM.createElement( "i" );
            i.addClassName( "fas fa-shipping-fast" );
            groupElement.appendChild( i );

            Element createdElement = DOM.createSpan();
            createdElement.setInnerText( " " + DateFormatter.formatDateOnly( delivered ) );
            groupElement.appendChild( createdElement );

            divElement.appendChild( groupElement );
        }

        Element nameElement = DOM.createElement( "p" );
        nameElement.setInnerText( delivery.getName() == null ? "" : delivery.getName() );
        divElement.appendChild( nameElement );

        Element descElement = DOM.createElement( "p" );
        descElement.setInnerText( delivery.getDescription()== null? "" : delivery.getDescription() );
        divElement.appendChild( descElement );

        cell.appendChild( divElement );
    }

    Lang lang;
}
