package ru.protei.portal.ui.delivery.client.view.pcborder.table.column;

import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.Date;

import static ru.protei.portal.ui.common.client.common.DateFormatter.formatDateOnly;

public class OrderDateColumn extends ClickColumn<PcbOrder> {

    @Inject
    public OrderDateColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("order-date");
        columnHeader.setInnerText(lang.pcbOrderOrderDate());
    }

    @Override
    public void fillColumnValue(Element cell, PcbOrder pcbOrder) {
        cell.addClassName("order-date");
        if (pcbOrder == null) {
            return;
        }

        Date date = pcbOrder.getOrderDate();
        if (date == null) {
            return;
        }

        cell.setInnerHTML(formatDateOnly(date));
    }

    Lang lang;
}
