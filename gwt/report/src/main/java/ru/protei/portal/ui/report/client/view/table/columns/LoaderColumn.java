package ru.protei.portal.ui.report.client.view.table.columns;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import ru.protei.portal.core.model.dto.ReportDto;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.StaticColumn;

public class LoaderColumn<T> extends ClickColumn<T> {
    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.setInnerText("");
    }

    @Override
    protected void fillColumnValue(Element cell, T value) {
        com.google.gwt.dom.client.Element parentDiv = DOM.createDiv();
        parentDiv.setClassName("report-loader");
        com.google.gwt.dom.client.Element firstChildDiv = DOM.createDiv();
        parentDiv.appendChild(firstChildDiv);
        com.google.gwt.dom.client.Element secondChildDiv = DOM.createDiv();
        parentDiv.appendChild(secondChildDiv);
        com.google.gwt.dom.client.Element thirdChildDiv = DOM.createDiv();
        parentDiv.appendChild(thirdChildDiv);
        cell.appendChild(parentDiv);
    }
}
