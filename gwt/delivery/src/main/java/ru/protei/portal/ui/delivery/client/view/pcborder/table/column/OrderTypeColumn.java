package ru.protei.portal.ui.delivery.client.view.pcborder.table.column;

import com.google.gwt.user.client.Element;
import ru.protei.portal.core.model.dict.En_PcbOrderType;
import ru.protei.portal.core.model.dict.En_StencilType;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.En_PcbOrderStencilTypeLang;
import ru.protei.portal.ui.common.client.lang.En_PcbOrderTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.Objects;

public class OrderTypeColumn extends ClickColumn<PcbOrder> {

    public OrderTypeColumn(Lang lang, En_PcbOrderTypeLang typeLang, En_PcbOrderStencilTypeLang stencilTypeLang) {
        this.lang = lang;
        this.typeLang = typeLang;
        this.stencilTypeLang = stencilTypeLang;
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

        if (Objects.equals(pcbOrder.getType(), En_PcbOrderType.STENCIL)){
            cell.setInnerHTML(typeLang.getName(pcbOrder.getType()) + " / " + stencilTypeLang.getName(pcbOrder.getStencilType()));
            return;
        }

        cell.setInnerHTML(typeLang.getName(pcbOrder.getType()));
    }

    Lang lang;
    En_PcbOrderTypeLang typeLang;
    En_PcbOrderStencilTypeLang stencilTypeLang;
}
