package ru.protei.portal.ui.report.client.view.table.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dto.ReportDto;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.ui.common.client.columns.StaticColumn;
import ru.protei.portal.ui.common.client.lang.En_ReportStatusLang;
import ru.protei.portal.ui.common.client.lang.Lang;

/**
 * Колонка "Номер"
 */
public class NumberColumn extends StaticColumn<ReportDto> {

    @Inject
    public NumberColumn(Lang lang, En_ReportStatusLang reportStatusLang) {
        this.lang = lang;
        this.reportStatusLang = reportStatusLang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("number");
        columnHeader.setInnerText(lang.issueReportsNumber());
    }

    @Override
    public void fillColumnValue(Element cell, ReportDto value) {
        if (value == null) {
            return;
        }
        Report report = value.getReport();

        cell.addClassName("number");
        com.google.gwt.dom.client.Element divElement = DOM.createDiv();

        com.google.gwt.dom.client.Element numberElement = DOM.createElement("p");
        numberElement.addClassName("number-size");
        numberElement.setInnerText(report.getId().toString());
        divElement.appendChild(numberElement);

        com.google.gwt.dom.client.Element stateElement = DOM.createElement("p");
        stateElement.addClassName("label label-report-" + report.getStatus().toString().toLowerCase());
        stateElement.setInnerText(reportStatusLang.getStateName(report.getStatus()));
        divElement.appendChild(stateElement);

        cell.appendChild(divElement);
    }

    private Lang lang;
    private En_ReportStatusLang reportStatusLang;
}
