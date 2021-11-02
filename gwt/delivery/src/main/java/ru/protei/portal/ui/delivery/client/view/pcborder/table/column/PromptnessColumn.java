package ru.protei.portal.ui.delivery.client.view.pcborder.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.Date;

import static ru.protei.portal.ui.common.client.common.DateFormatter.formatDateOnly;

public class PromptnessColumn extends ClickColumn<PcbOrder> {

    @Inject
    public PromptnessColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.setInnerText(lang.pcbOrderPromptness());
    }

    @Override
    public void fillColumnValue(Element cell, PcbOrder pcbOrder) {
        if (pcbOrder == null) {
            return;
        }

        Date orderDate = pcbOrder.getOrderDate();
        if (orderDate == null) {
            return;
        }
        com.google.gwt.dom.client.Element root = DOM.createDiv();
        root.setInnerHTML(formatDateOnly(orderDate));
        cell.appendChild(root);
    }

    Lang lang;
}
