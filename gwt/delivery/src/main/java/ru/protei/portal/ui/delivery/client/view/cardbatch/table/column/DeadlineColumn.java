package ru.protei.portal.ui.delivery.client.view.cardbatch.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.Date;

import static ru.protei.portal.ui.common.client.common.DateFormatter.formatDateOnly;

public class DeadlineColumn extends ClickColumn<CardBatch> {

    @Inject
    public DeadlineColumn(Lang lang) {
        this.lang = lang;
        setStopPropogationElementClassName(CLASS_NAME);
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName(CLASS_NAME);
        columnHeader.setInnerText(lang.cardBatchColumnDeadline());
    }

    @Override
    public void fillColumnValue(Element cell, CardBatch card) {
        if (card == null) {
            return;
        }

        cell.addClassName(CLASS_NAME);

        Long deadline = card.getDeadline();
        if (deadline != null) {
            com.google.gwt.dom.client.Element root = DOM.createDiv();
            String dateTime = formatDateOnly(new Date(deadline));
            root.setInnerHTML(dateTime);
            cell.appendChild(root);
        }
    }

    Lang lang;
    private static final String CLASS_NAME = "deadline";
}
