package ru.protei.portal.ui.delivery.client.view.pcborder.table.column;

import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.Date;

import static ru.protei.portal.ui.common.client.common.DateFormatter.formatDateOnly;

public class ReadyDateColumn extends ClickColumn<PcbOrder> {

    @Inject
    public ReadyDateColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("ready-date");
        columnHeader.setInnerText(lang.pcbOrderReadyDate());
    }

    @Override
    public void fillColumnValue(Element cell, PcbOrder pcbOrder) {
        cell.addClassName("ready-date");
        if (pcbOrder == null) {
            return;
        }

        Date date = pcbOrder.getReadyDate();
        if (date == null) {
            return;
        }

        cell.setInnerHTML(formatDateOnly(date));
    }

    Lang lang;
}
