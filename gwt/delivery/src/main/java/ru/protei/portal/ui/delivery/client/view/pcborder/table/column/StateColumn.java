package ru.protei.portal.ui.delivery.client.view.pcborder.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import ru.protei.portal.core.model.dict.En_PcbOrderState;
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

        com.google.gwt.dom.client.Element stateElement = DOM.createElement("p");
        stateElement.addClassName("label");
        En_PcbOrderState state = pcbOrder.getState();
        stateElement.getStyle().setBackgroundColor(state == null ? null : state.getColor());
        stateElement.setInnerText(state == null ? null : stateLang.getStateName(state));
        cell.appendChild(stateElement);
    }

    Lang lang;
    En_PcbOrderStateLang stateLang;
}
