package ru.protei.portal.ui.delivery.client.view.cardbatch.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;

public class ArticleColumn extends ClickColumn<CardBatch> {

    @Inject
    public ArticleColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName(CLASS_NAME);
        columnHeader.setInnerText(lang.cardBatchColumnArticle());
    }

    @Override
    public void fillColumnValue(Element cell, CardBatch card) {
        if (card == null) {
            return;
        }

        cell.addClassName(CLASS_NAME);

        String article = card.getArticle();
        if (isNotEmpty(article)) {
            com.google.gwt.dom.client.Element root = DOM.createDiv();
            root.setInnerHTML(article);
            cell.appendChild(root);
        }
    }

    Lang lang;
    private static final String CLASS_NAME = "info";
}
