package ru.protei.portal.ui.delivery.client.view.cardbatch.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.ent.CaseMember;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.List;

public class ExecutorsColumn extends ClickColumn<CardBatch> {

    @Inject
    public ExecutorsColumn(Lang lang) {
        this.lang = lang;
        setStopPropogationElementClassName(CLASS_NAME);
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName(CLASS_NAME);
        columnHeader.setInnerText(lang.cardBatchColumnExecutors());
    }

    @Override
    public void fillColumnValue(Element cell, CardBatch card) {
        if (card == null) {
            return;
        }

        cell.addClassName(CLASS_NAME);

        StringBuilder sb = new StringBuilder();

        List<CaseMember> members = card.getMembers();
        if (members != null) {
            sb.append("TEST");
            for (CaseMember member: members) {
                sb.append(member.getMember().getDisplayName())
                  .append("<br/>");
            }
        }

        com.google.gwt.dom.client.Element root = DOM.createDiv();
        root.setInnerHTML(sb.toString());
        cell.appendChild(root);
    }

    Lang lang;
    private static final String CLASS_NAME = "executors";
}
