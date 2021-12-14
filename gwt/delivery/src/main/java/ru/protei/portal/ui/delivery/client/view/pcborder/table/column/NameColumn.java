package ru.protei.portal.ui.delivery.client.view.pcborder.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.En_PcbOrderPromptness;
import ru.protei.portal.core.model.dict.En_PcbOrderState;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.En_PcbOrderPromptnessLang;
import ru.protei.portal.ui.common.client.lang.En_PcbOrderStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.ui.common.shared.util.HtmlUtils.sanitizeHtml;

public class NameColumn extends ClickColumn<PcbOrder> {

    @Inject
    public NameColumn(Lang lang, En_PcbOrderStateLang stateLang, En_PcbOrderPromptnessLang promptnessLang) {
        this.lang = lang;
        this.stateLang = stateLang;
        this.promptnessLang = promptnessLang;
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

        com.google.gwt.dom.client.Element stateElement = DOM.createElement("i");
        stateElement.addClassName("label");
        En_PcbOrderState state = pcbOrder.getState();
        stateElement.getStyle().setBackgroundColor(state == null ? null : state.getColor());
        stateElement.setInnerText(state == null ? null : stateLang.getStateName(state));
        cell.appendChild(stateElement);

        com.google.gwt.dom.client.Element nameElement = DOM.createElement("p");
        nameElement.addClassName("name-size");
        StringBuilder sb = new StringBuilder();
        sb.append(sanitizeHtml(pcbOrder.getCardTypeName()))
                .append("(")
                .append(sanitizeHtml(pcbOrder.getCardTypeCode()))
                .append(")");
        nameElement.setInnerText(sb.toString());
        cell.appendChild(nameElement);

        com.google.gwt.dom.client.Element promptnessElement = DOM.createElement("p");
        promptnessElement.addClassName("label");
        En_PcbOrderPromptness promptness = pcbOrder.getPromptness();
        promptnessElement.getStyle().setBackgroundColor(promptness == null ? null : promptness.getColor());
        promptnessElement.setInnerText(promptness == null ? null : promptnessLang.getName(promptness));
        cell.appendChild(promptnessElement);

    }

    Lang lang;
    En_PcbOrderStateLang stateLang;
    En_PcbOrderPromptnessLang promptnessLang;
}
