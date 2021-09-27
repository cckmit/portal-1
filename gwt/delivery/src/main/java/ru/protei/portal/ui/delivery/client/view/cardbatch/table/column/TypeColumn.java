package ru.protei.portal.ui.delivery.client.view.cardbatch.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.ui.common.shared.util.HtmlUtils.sanitizeHtml;

public class TypeColumn extends ClickColumn<CardBatch> {

    @Inject
    public TypeColumn(Lang lang) {
        this.lang = lang;
        setStopPropogationElementClassName(CLASS_NAME);
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName(CLASS_NAME);
        columnHeader.setInnerText(lang.cardBatchColumnType());
    }

    @Override
    public void fillColumnValue(Element cell, CardBatch card) {
        if (card == null) {
            return;
        }

        cell.addClassName(CLASS_NAME);

        com.google.gwt.dom.client.Element root = DOM.createDiv();
        StringBuilder sb = new StringBuilder();
        sb.append("<b>").append(lang.cardType())
                        .append(":</b> ")
                        .append(sanitizeHtml(card.getTypeName()))
                        .append("<br/>").append("<b>")
                        .append(lang.id()).append(":</b> ")
                        .append(card.getId()).append("<br/>");

        root.setInnerHTML(sb.toString());
        cell.appendChild(root);
    }

    Lang lang;
    private static final String CLASS_NAME = "info";
}
