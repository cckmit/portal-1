package ru.protei.portal.ui.delivery.client.view.rfidlabels.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.RFIDLabel;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.ui.common.shared.util.HtmlUtils.sanitizeHtml;

public class EpcAndNameColumn extends ClickColumn<RFIDLabel> {

    @Inject
    public EpcAndNameColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "epc" );
        columnHeader.setInnerText( lang.RFIDLabelsEpc() );
    }

    @Override
    public void fillColumnValue( Element cell, RFIDLabel item ) {
        if ( item == null ) {
            return;
        }

        cell.addClassName("epc");
        com.google.gwt.dom.client.Element root = DOM.createDiv();
        StringBuilder sb = new StringBuilder();
        sb.append("<b>")
                .append(sanitizeHtml(item.getEpc()))
                .append("</b>")
                .append("</br>")
                .append(sanitizeHtml(item.getName()));

        root.setInnerHTML(sb.toString());
        cell.appendChild(root);
    }

    private Lang lang;
}
