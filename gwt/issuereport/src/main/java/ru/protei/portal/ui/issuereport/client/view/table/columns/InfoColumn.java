package ru.protei.portal.ui.issuereport.client.view.table.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.Date;

public class InfoColumn extends ClickColumn<Report> {

    @Inject
    public InfoColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("info");
        columnHeader.setInnerText(lang.issueReportsInfo());
    }

    @Override
    public void fillColumnValue(Element cell, Report value) {
        cell.addClassName("info");

        Element divElement = DOM.createDiv();

        Element locale = DOM.createElement("p");
        locale.addClassName("fa fa-fw locale-box");
        locale.setInnerText(value == null ? "" : value.getLocale() == null ? "" : value.getLocale());
        divElement.appendChild(locale);

        Element title = DOM.createLabel();
        title.addClassName("report-title");
        title.setInnerText(value == null ? "" : value.getName() == null ? "" : value.getName());
        divElement.appendChild(title);

        Element created = getDateElement(value == null ? null : value.getCreated());
        if (created != null) {
            divElement.appendChild(created);
        }
//        Element modified = getDateElement(value == null ? null : value.getModified());
//        if (modified != null) {
//            divElement.appendChild(modified);
//        }

        cell.appendChild(divElement);
    }

    private Element getDateElement(Date date) {
        if (date == null) {
            return null;
        }

        Element group = DOM.createElement("p");
        group.addClassName("text-semimuted");

        Element clock = DOM.createElement("i");
        clock.addClassName("fa fa-clock-o");
        group.appendChild(clock);

        Element dateSpan = DOM.createSpan();
        dateSpan.setInnerText(" " + DateFormatter.formatDateTime(date));
        group.appendChild(dateSpan);

        return group;
    }

    private Lang lang;
}
