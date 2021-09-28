package ru.protei.portal.ui.delivery.client.view.cardbatch.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

public class InstallParamsColumn extends ClickColumn<CardBatch> {

    @Inject
    public InstallParamsColumn(Lang lang) {
        this.lang = lang;
        setStopPropogationElementClassName(CLASS_NAME);
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName(CLASS_NAME);
        columnHeader.setInnerText(lang.cardBatchColumnInstallParams());
    }

    @Override
    public void fillColumnValue(Element cell, CardBatch card) {
        if (card == null) {
            return;
        }

        cell.addClassName(CLASS_NAME);

        com.google.gwt.dom.client.Element root = DOM.createDiv();
        root.setInnerHTML(card.getParams());
        cell.appendChild(root);
    }

    Lang lang;
    private static final String CLASS_NAME = "install_params";
}
