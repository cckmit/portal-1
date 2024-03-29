package ru.protei.portal.ui.delivery.client.view.pcborder.table.column;

import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

public class ModificationColumn extends ClickColumn<PcbOrder> {

    @Inject
    public ModificationColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("modification");
        columnHeader.setInnerText(lang.pcbOrderModification());
    }

    @Override
    public void fillColumnValue(Element cell, PcbOrder pcbOrder) {
        cell.addClassName("modification");
        if (pcbOrder == null) {
            return;
        }

        cell.setInnerHTML(pcbOrder.getModification());
    }

    Lang lang;
}
