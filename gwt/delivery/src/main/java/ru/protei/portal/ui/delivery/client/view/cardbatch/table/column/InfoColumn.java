package ru.protei.portal.ui.delivery.client.view.cardbatch.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.ui.common.shared.util.HtmlUtils.sanitizeHtml;

public class InfoColumn extends ClickColumn<CardBatch> {

    @Inject
    public InfoColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName(CLASS_NAME);
        columnHeader.setInnerText(lang.cardBatchColumnInfo());
    }

    @Override
    public void fillColumnValue(Element cell, CardBatch card) {
        if (card == null) {
            return;
        }

        cell.addClassName(CLASS_NAME);

        com.google.gwt.dom.client.Element root = DOM.createDiv();
        StringBuilder sb = new StringBuilder();

        String typeName = card.getTypeName();
        if (StringUtils.isNotEmpty(typeName)) {
            sb.append("<b>").append(lang.cardBatchType())
                            .append(":</b> ")
                            .append(sanitizeHtml(typeName))
                            .append("<br/>");
        }

        sb.append("<b>").append(lang.cardBatchArticle())
                .append(":</b> ")
                .append(sanitizeHtml(card.getArticle()));

        root.setInnerHTML(sb.toString());
        cell.appendChild(root);
    }

    Lang lang;
    private static final String CLASS_NAME = "info";
}
