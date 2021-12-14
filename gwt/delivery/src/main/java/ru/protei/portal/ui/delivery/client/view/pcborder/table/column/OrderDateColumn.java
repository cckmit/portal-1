package ru.protei.portal.ui.delivery.client.view.pcborder.table.column;

import com.google.gwt.user.client.DOM;
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
        columnHeader.addClassName("date");
        columnHeader.setInnerText(lang.pcbOrderDate());
    }

    @Override
    public void fillColumnValue(Element cell, PcbOrder pcbOrder) {
        cell.addClassName("date");
        if (pcbOrder == null) {
            return;
        }

        com.google.gwt.dom.client.Element orderDateElement = DOM.createElement("p");
        orderDateElement.addClassName("order-date");
        Date orderDate = pcbOrder.getOrderDate();
        orderDateElement.setInnerText(ORDER_DATE + formatDateOnly(orderDate));
        cell.appendChild(orderDateElement);

        com.google.gwt.dom.client.Element orderReadyDateElement = DOM.createElement("p");
        orderReadyDateElement.addClassName("ready-date");
        Date readyDate = pcbOrder.getReadyDate();
        orderReadyDateElement.setInnerText(READY_DATE + formatDateOnly(readyDate));
        cell.appendChild(orderReadyDateElement);

        com.google.gwt.dom.client.Element orderReceiptDateElement = DOM.createElement("p");
        orderReceiptDateElement.addClassName("receipt-date");
        Date receiptDate = pcbOrder.getReceiptDate();
        orderReceiptDateElement.setInnerText(RECEIPT_DATE + formatDateOnly(receiptDate));
        cell.appendChild(orderReceiptDateElement);
    }

    Lang lang;
    private final String ORDER_DATE = "Дата заказа: ";
    private final String READY_DATE = "Дата готовности: ";
    private final String RECEIPT_DATE = "Дата получения: ";

}
