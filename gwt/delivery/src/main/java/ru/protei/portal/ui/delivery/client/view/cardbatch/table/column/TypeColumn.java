package ru.protei.portal.ui.delivery.client.view.cardbatch.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.CardStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.core.model.helper.StringUtils.firstUppercaseChar;
import static ru.protei.portal.ui.common.client.util.ColorUtils.makeContrastColor;
import static ru.protei.portal.ui.common.shared.util.HtmlUtils.sanitizeHtml;

public class TypeColumn extends ClickColumn<CardBatch> {

    @Inject
    public TypeColumn(Lang lang, CardStateLang stateLang) {
        this.lang = lang;
        this.stateLang = stateLang;
        setStopPropogationElementClassName(CLASS_NAME);
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName(CLASS_NAME);
        columnHeader.setInnerText(lang.cardBatchType());
    }

    @Override
    public void fillColumnValue(Element cell, CardBatch card) {
        if (card == null) {
            return;
        }

        cell.addClassName("type");
        com.google.gwt.dom.client.Element divElement = DOM.createDiv();

        if (card.getImportance() != null) {
            com.google.gwt.dom.client.Element importanceElement = DOM.createElement("i");
            importanceElement.addClassName("case-importance");
            importanceElement.setInnerText(firstUppercaseChar(String.valueOf(card.getImportanceCode())));
            importanceElement.getStyle().setBackgroundColor(card.getImportanceColor());
            importanceElement.getStyle().setColor(makeContrastColor(card.getImportanceColor()));
            divElement.appendChild(importanceElement);
        }


        com.google.gwt.dom.client.Element typeElement = DOM.createElement("p");
        typeElement.addClassName("type-size");
        StringBuilder sb = new StringBuilder();
        sb.append(sanitizeHtml(card.getTypeName())).append(" - ")
          .append(sanitizeHtml(card.getCode()));
        typeElement.setInnerText(sb.toString());
        divElement.appendChild(typeElement);


        com.google.gwt.dom.client.Element stateElement = DOM.createElement("p");
        stateElement.addClassName("label");
        CaseState state = card.getState();
        stateElement.getStyle().setBackgroundColor(state == null ? null : state.getColor());
        stateElement.setInnerText(state == null ? null : stateLang.getStateName(state));
        divElement.appendChild(stateElement);

        cell.appendChild(divElement);
    }

    Lang lang;
    CardStateLang stateLang;
    private static final String CLASS_NAME = "type";
}
