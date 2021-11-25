package ru.protei.portal.ui.delivery.client.view.rfidlabels.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.RFIDLabel;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;

public class LastScanDateColumn extends ClickColumn<RFIDLabel> {

    @Inject
    public LastScanDateColumn(Lang lang ) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "last-scan-date" );
        columnHeader.setInnerText( lang.RFIDLabelsLastScanDate() );
    }

    @Override
    public void fillColumnValue( Element cell, RFIDLabel item ) {
        if ( item == null ) {
            return;
        }

        cell.addClassName( "last-scan-date" );

        if (item.getLastScanDate() != null) {
            com.google.gwt.dom.client.Element date = DOM.createDiv();
            date.setInnerText( DateFormatter.formatDateTime(item.getLastScanDate()) );
            cell.appendChild( date );
        }
    }

    Lang lang;
}
