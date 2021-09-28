package ru.protei.portal.ui.delivery.client.view.cardbatch.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

public class AmountColumn extends ClickColumn<CardBatch> {

    @Inject
    public AmountColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName(CLASS_NAME);
        columnHeader.setInnerText(lang.cardBatchColumnAmount());
    }

    @Override
    public void fillColumnValue(Element cell, CardBatch card) {
        if (card == null) {
            return;
        }

        cell.addClassName(CLASS_NAME);

        com.google.gwt.dom.client.Element root = DOM.createDiv();
        StringBuilder sb = new StringBuilder();

        Integer amount = card.getAmount();
        sb.append("<b>").append(lang.cardBatchColumnAmountOrdered())
                        .append(":</b> ")
                        .append(amount != null ? amount : "-")
                        .append("<br/>");

        Long manufacturedAmount = card.getManufacturedAmount();
        sb.append("<b>")
          .append(lang.cardBatchColumnAmountManufactured())
          .append(":</b> ")
          .append(manufacturedAmount != null ? manufacturedAmount : "-")
          .append("<br/>");

        Long freeAmount = card.getFreeAmount();
        sb.append("<b>").append(lang.cardBatchColumnAmountFree())
                        .append(":</b> ")
                        .append(freeAmount != null ? freeAmount : "-");

        root.setInnerHTML(sb.toString());
        cell.appendChild(root);
    }

    Lang lang;
    private static final String CLASS_NAME = "amount";
}
