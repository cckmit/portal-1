package ru.protei.portal.ui.delivery.client.view.pcborder.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.ui.common.shared.util.HtmlUtils.sanitizeHtml;

public class NameColumn extends ClickColumn<PcbOrder> {

    @Inject
    public NameColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("name");
        columnHeader.setInnerText(lang.pcbOrderName());
    }

    @Override
    public void fillColumnValue(Element cell, PcbOrder pcbOrder) {
        cell.addClassName("name");
        if (pcbOrder == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(sanitizeHtml(pcbOrder.getCardTypeName()))
                .append("(")
                .append(sanitizeHtml(pcbOrder.getCardTypeCode()))
                .append(")");

        cell.setInnerHTML(sb.toString());
    }

    Lang lang;
}
