package ru.protei.portal.ui.delivery.client.view.rfidlabels.table.column;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.RFIDDevice;
import ru.protei.portal.core.model.ent.RFIDLabel;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

import static ru.protei.portal.ui.common.shared.util.HtmlUtils.sanitizeHtml;

public class DeviceColumn extends ClickColumn<RFIDLabel> {

    @Inject
    public DeviceColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("device");
        columnHeader.setInnerText(lang.RFIDLabelsDevice());
    }

    @Override
    public void fillColumnValue(Element cell, RFIDLabel item) {
        if (item == null || item.getRfidDevice() == null) {
            return;
        }

        RFIDDevice rfidDevice = item.getRfidDevice();
        cell.addClassName("device");

        com.google.gwt.dom.client.Element root = DOM.createDiv();
        StringBuilder sb = new StringBuilder();
        sb.append("<b>")
                .append(sanitizeHtml(rfidDevice.getReaderId()))
                .append("</b>")
                .append("</br>")
                .append(sanitizeHtml(rfidDevice.getName()))
                .append(". ")
                .append(sanitizeHtml(rfidDevice.getInfo()));

        root.setInnerHTML(sb.toString());
        cell.appendChild(root);
    }

    Lang lang;
}