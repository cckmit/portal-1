package ru.protei.portal.ui.delivery.client.view.cardbatch.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.CardStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;

public class StateColumn extends ClickColumn<CardBatch> {

    @Inject
    public StateColumn(Lang lang, CardStateLang stateLang) {
        this.lang = lang;
        this.stateLang = stateLang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("manager");
        columnHeader.setInnerText(lang.cardBatchColumnState());
    }

    @Override
    public void fillColumnValue(Element cell, CardBatch card) {
        if (card == null) {
            return;
        }

        cell.addClassName("number");
        com.google.gwt.dom.client.Element root = DOM.createDiv();
        com.google.gwt.dom.client.Element numberElement = DOM.createElement("p");
        numberElement.addClassName("number-size");
        root.appendChild(numberElement);

        com.google.gwt.dom.client.Element stateElement = DOM.createElement("p");
        stateElement.addClassName("label");
        CaseState state = card.getState();
        stateElement.getStyle().setBackgroundColor(state == null ? null : state.getColor());
        stateElement.setInnerText(state == null ? null : stateLang.getStateName(state));

        root.appendChild(stateElement);
        cell.appendChild(root);
    }

    Lang lang;
    CardStateLang stateLang;
    private static final String CLASS_NAME = "info";
}
