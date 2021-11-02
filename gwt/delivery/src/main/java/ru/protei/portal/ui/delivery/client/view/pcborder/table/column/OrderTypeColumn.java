package ru.protei.portal.ui.delivery.client.view.pcborder.table.column;

import com.google.gwt.user.client.Element;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.En_PcbOrderTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;

public class OrderTypeColumn extends ClickColumn<PcbOrder> {

    public OrderTypeColumn(Lang lang, En_PcbOrderTypeLang typeLang) {
        this.lang = lang;
        this.typeLang = typeLang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.setInnerText(lang.pcbOrderOrderType());
    }

    @Override
    public void fillColumnValue(Element cell, PcbOrder pcbOrder) {
        if (pcbOrder == null) {
            return;
        }

        cell.setInnerHTML(typeLang.getName(pcbOrder.getType()));
    }

    Lang lang;
    En_PcbOrderTypeLang typeLang;
}
