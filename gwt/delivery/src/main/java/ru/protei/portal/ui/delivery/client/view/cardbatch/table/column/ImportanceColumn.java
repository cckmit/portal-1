package ru.protei.portal.ui.delivery.client.view.cardbatch.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.core.model.helper.StringUtils.firstUppercaseChar;
import static ru.protei.portal.ui.common.client.util.ColorUtils.makeContrastColor;
import static ru.protei.portal.ui.common.shared.util.HtmlUtils.sanitizeHtml;

public class ImportanceColumn extends ClickColumn<CardBatch> {
    @Inject
    public ImportanceColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("info");
        columnHeader.setInnerText(lang.cardBatchColumnImportance());
    }

    @Override
    public void fillColumnValue(Element cell, CardBatch card) {

        if (card == null) {
            return;
        }

//        cell.addClassName("info");
//
//        com.google.gwt.dom.client.Element root = DOM.createDiv();
//        StringBuilder sb = new StringBuilder();
//        sb.append("<b>")
//                .append(lang.cardType())
//                .append(":</b> ")
//                .append(sanitizeHtml(card.get()))
//                .append("<br/>");
//
//        Long importance = card.getImportance();
//        if (importance != null) {
//            com.google.gwt.dom.client.Element i = DOM.createElement( "i" );
//            i.addClassName("case-importance");
//            i.setInnerText(String.valueOf(importance));
//            i.getStyle().setBackgroundColor(value.getImportanceColor());
//            i.getStyle().setColor(makeContrastColor(value.getImportanceColor()));
//            divElement.appendChild( i );
//
//            sb.append("<b>")
//                    .append(lang.cardArticle())
//                    .append(":</b> ")
//                    .append(sanitizeHtml(card.getArticle()))
//                    .append("<br/>");
//        }

//        root.setInnerHTML(sb.toString());
//        cell.appendChild(root);
    }

    Lang lang;
}
