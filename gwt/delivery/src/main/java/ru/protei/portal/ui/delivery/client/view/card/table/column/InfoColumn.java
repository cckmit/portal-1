package ru.protei.portal.ui.delivery.client.view.card.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.ui.common.shared.util.HtmlUtils.sanitizeHtml;

/**
 * Тип платы, артикул, примечание
 */
public class InfoColumn extends ClickColumn<Card>{

    @Inject
    public InfoColumn( Lang lang ) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader( Element columnHeader ) {
        columnHeader.addClassName( "info" );
        columnHeader.setInnerText( lang.cardColumnInfo() );
    }

    @Override
    public void fillColumnValue( Element cell, Card card ) {

        if ( card == null ) {
            return;
        }

        cell.addClassName( "info" );

        com.google.gwt.dom.client.Element root = DOM.createDiv();
        StringBuilder sb = new StringBuilder();
        sb.append("<b>")
                .append(lang.cardType())
                .append(":</b> ")
                .append(sanitizeHtml(card.getTypeName()))
                .append("<br/>");
        if (StringUtils.isEmpty(card.getArticle())) {
            sb.append("<b>")
                    .append(lang.cardArticle())
                    .append(":</b> ")
                    .append(sanitizeHtml(card.getArticle()))
                    .append("<br/>");
        }
        if (StringUtils.isEmpty(card.getNote())) {
            sb.append("<b>")
                    .append(lang.cardNote())
                    .append(":</b> ")
                    .append(sanitizeHtml(card.getNote()))
                    .append("<br/>");
        }

        root.setInnerHTML(sb.toString());
        cell.appendChild(root);
    }

    Lang lang;
}
