package ru.protei.portal.ui.report.client.view.table.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dto.ReportDto;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.ui.common.client.columns.StaticColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.En_ReportScheduledTypeLang;
import ru.protei.portal.ui.common.client.lang.En_ReportTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.Date;

public class InfoColumn extends StaticColumn<ReportDto> {

    @Inject
    public InfoColumn(Lang lang, En_ReportTypeLang reportTypeLang, En_ReportScheduledTypeLang scheduledTypeLang) {
        this.lang = lang;
        this.reportTypeLang = reportTypeLang;
        this.scheduledTypeLang = scheduledTypeLang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("info");
        columnHeader.setInnerText(lang.issueReportsInfo());
    }

    @Override
    public void fillColumnValue(Element cell, ReportDto value) {
        Report report = value != null
                ? value.getReport()
                : null;

        cell.addClassName("info");

        Element divElement = DOM.createDiv();

        Element locale = DOM.createElement("p");
        locale.addClassName("fa locale-box");
        locale.setInnerText(report == null ? "" : report.getLocale() == null ? "" : report.getLocale());
        divElement.appendChild(locale);

        Element title = DOM.createLabel();
        title.addClassName("report-title");
        title.setInnerText(report == null ? "" : report.getName() == null ? "" : report.getName());
        divElement.appendChild(title);

        Element created = renderDate(report == null ? null : report.getCreated());
        if (created != null) {
            divElement.appendChild(created);
        }
        divElement.appendChild(DOM.createElement("p"));
        Element modified = renderDate(report == null ? null : report.getModified());
        if (modified != null) {
            divElement.appendChild(modified);
        }

        Element type = DOM.createElement("p");
        type.setInnerText(report == null ? "" : lang.type() + ": " + reportTypeLang.getType(report.getReportType()));
        divElement.appendChild(type);

        Element scheduledType = DOM.createElement("p");
        scheduledType.setInnerText(report == null ? "" : lang.reportScheduledType() + ": " + scheduledTypeLang.getType(report.getScheduledType()));
        divElement.appendChild(scheduledType);

        cell.appendChild(divElement);
    }

    private Element renderDate(Date date) {
        if (date == null) {
            return null;
        }

        Element group = DOM.createElement("p");
        group.addClassName("text-semimuted pull-right");

        Element clock = DOM.createElement("i");
        clock.addClassName("fa fa-clock-o");
        group.appendChild(clock);

        Element dateSpan = DOM.createSpan();
        dateSpan.setInnerText(" " + DateFormatter.formatDateTime(date));
        group.appendChild(dateSpan);

        return group;
    }

    private Lang lang;
    private En_ReportTypeLang reportTypeLang;
    private En_ReportScheduledTypeLang scheduledTypeLang;
}
