package ru.protei.portal.ui.delivery.client.view.pcborder.table.column;

import com.google.gwt.user.client.Element;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.En_PcbOrderStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;

public class StateColumn extends ClickColumn<PcbOrder> {

    public StateColumn(Lang lang, En_PcbOrderStateLang stateLang) {
        this.lang = lang;
        this.stateLang = stateLang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.setInnerText(lang.pcbOrderState());
    }

    @Override
    public void fillColumnValue(Element cell, PcbOrder pcbOrder) {
        if (pcbOrder == null) {
            return;
        }

        cell.setInnerHTML(stateLang.getStateName(pcbOrder.getState()));
    }

    Lang lang;
    En_PcbOrderStateLang stateLang;
}
