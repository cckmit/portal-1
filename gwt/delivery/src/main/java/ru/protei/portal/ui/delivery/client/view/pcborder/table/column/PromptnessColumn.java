package ru.protei.portal.ui.delivery.client.view.pcborder.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import ru.protei.portal.core.model.dict.En_PcbOrderPromptness;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.En_PcbOrderPromptnessLang;
import ru.protei.portal.ui.common.client.lang.Lang;

public class PromptnessColumn extends ClickColumn<PcbOrder> {

    public PromptnessColumn(Lang lang, En_PcbOrderPromptnessLang promptnessLang) {
        this.lang = lang;
        this.promptnessLang = promptnessLang;
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

        com.google.gwt.dom.client.Element stateElement = DOM.createElement("p");
        stateElement.addClassName("label");
        En_PcbOrderPromptness promptness = pcbOrder.getPromptness();
        stateElement.getStyle().setBackgroundColor(promptness == null ? null : promptness.getColor());
        stateElement.setInnerText(promptness == null ? null : promptnessLang.getName(promptness));
        cell.appendChild(stateElement);
    }

    Lang lang;
    En_PcbOrderPromptnessLang promptnessLang;
}
