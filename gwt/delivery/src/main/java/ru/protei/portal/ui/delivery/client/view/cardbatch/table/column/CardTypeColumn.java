package ru.protei.portal.ui.delivery.client.view.cardbatch.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.ui.common.shared.util.HtmlUtils.sanitizeHtml;

public class CardTypeColumn extends ClickColumn<CardBatch> {

    @Inject
    public CardTypeColumn(Lang lang) {
        this.lang = lang;
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

        cell.addClassName(CLASS_NAME);
        com.google.gwt.dom.client.Element root = DOM.createDiv();
        root.addClassName("card-type-size");

        StringBuilder sb = new StringBuilder();
        sb.append("<b>")
                .append(sanitizeHtml(card.getTypeName()))
                .append(" (")
                .append(sanitizeHtml(card.getCode()))
                .append(")")
                .append("</b>");

        if (StringUtils.isNotEmpty(card.getArticle())) {
            sb.append("</br>").append(sanitizeHtml(card.getArticle()));
        }

        root.setInnerHTML(sb.toString());
        cell.appendChild(root);
    }

    Lang lang;
    private static final String CLASS_NAME = "card-type";
}
