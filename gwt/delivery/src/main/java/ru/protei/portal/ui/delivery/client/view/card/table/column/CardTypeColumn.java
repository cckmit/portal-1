package ru.protei.portal.ui.delivery.client.view.card.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.ui.common.shared.util.HtmlUtils.sanitizeHtml;

public class CardTypeColumn extends ClickColumn<Card> {

    @Inject
    public CardTypeColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("card-type");
        columnHeader.setInnerText(lang.cardColumnCardType());
    }

    @Override
    public void fillColumnValue(Element cell, Card card) {
        if (card == null) {
            return;
        }

        cell.addClassName("card-type");

        com.google.gwt.dom.client.Element root = DOM.createDiv();
        StringBuilder sb = new StringBuilder();
        sb.append("<b>")
                .append(sanitizeHtml(card.getCardType().getName()))
                .append("</b>")
                .append("</br>")
                .append(sanitizeHtml(card.getArticle()));

        root.setInnerHTML(sb.toString());
        cell.appendChild(root);
    }

    Lang lang;
}