package ru.protei.portal.ui.delivery.client.view.rfidlabels.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.RFIDLabel;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

public class InfoColumn extends ClickColumn<RFIDLabel>{

    @Inject
    public InfoColumn( Lang lang ) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "info" );
        columnHeader.setInnerText( lang.RFIDLabelsInfo() );
    }

    @Override
    public void fillColumnValue( Element cell, RFIDLabel item ) {

        if ( item == null ) {
            return;
        }

        cell.addClassName( "info" );

        com.google.gwt.dom.client.Element root = DOM.createDiv();
        root.setInnerText(item.getInfo());
        cell.appendChild(root);
    }

    Lang lang;
}
