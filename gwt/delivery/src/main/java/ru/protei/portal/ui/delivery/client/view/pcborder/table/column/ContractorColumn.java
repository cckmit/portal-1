package ru.protei.portal.ui.delivery.client.view.pcborder.table.column;

import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

public class ContractorColumn extends ClickColumn<PcbOrder> {

    @Inject
    public ContractorColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.setInnerText(lang.pcbOrderContractor());
    }

    @Override
    public void fillColumnValue(Element cell, PcbOrder pcbOrder) {
        if (pcbOrder == null) {
            return;
        }

        cell.setInnerHTML(pcbOrder.getCompanyName());
    }

    Lang lang;
}
