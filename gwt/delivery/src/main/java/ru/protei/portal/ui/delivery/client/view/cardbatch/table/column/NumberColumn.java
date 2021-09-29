package ru.protei.portal.ui.delivery.client.view.cardbatch.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.CardStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;

public class NumberColumn extends ClickColumn<CardBatch> {

    @Inject
    public NumberColumn(Lang lang, CardStateLang stateLang) {
        this.lang = lang;
        this.stateLang = stateLang;
        setStopPropogationElementClassName(CLASS_NAME);
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName(CLASS_NAME);
        columnHeader.setInnerText(lang.cardBatchColumnNumber());
    }

    @Override
    public void fillColumnValue(Element cell, CardBatch card) {
        if (card == null) {
            return;
        }

        cell.addClassName(CLASS_NAME);
        com.google.gwt.dom.client.Element numberElement = DOM.createDiv();
        numberElement.setInnerText(String.valueOf(card.getNumber()));
        cell.appendChild(numberElement);
    }

    Lang lang;
    CardStateLang stateLang;
    private static final String CLASS_NAME = "number";
}
