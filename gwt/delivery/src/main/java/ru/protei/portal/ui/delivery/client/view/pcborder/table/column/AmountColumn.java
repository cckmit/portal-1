package ru.protei.portal.ui.delivery.client.view.pcborder.table.column;

import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

public class AmountColumn extends ClickColumn<PcbOrder> {

    @Inject
    public AmountColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("amount");
        columnHeader.setInnerText(lang.pcbOrderAmount());
    }

    @Override
    public void fillColumnValue(Element cell, PcbOrder pcbOrder) {
        cell.addClassName("amount");
        if (pcbOrder == null) {
            return;
        }

        cell.setInnerHTML(String.valueOf(pcbOrder.getAmount()));
    }

    Lang lang;
}
