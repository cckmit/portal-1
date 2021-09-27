package ru.protei.portal.ui.delivery.client.view.cardbatch.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;

public class BatchNumberColumn extends ClickColumn<CardBatch> {

    @Inject
    public BatchNumberColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName(CLASS_NAME);
        columnHeader.setInnerText(lang.cardBatchColumnBatchNumber());
    }

    @Override
    public void fillColumnValue(Element cell, CardBatch card) {
        if (card == null) {
            return;
        }

        cell.addClassName(CLASS_NAME);

        String number = card.getNumber();
        if (isNotEmpty(number)) {
            com.google.gwt.dom.client.Element root = DOM.createDiv();
            root.setInnerHTML(number);
            cell.appendChild(root);
        }
    }

    Lang lang;
    private static final String CLASS_NAME = "info";
}
