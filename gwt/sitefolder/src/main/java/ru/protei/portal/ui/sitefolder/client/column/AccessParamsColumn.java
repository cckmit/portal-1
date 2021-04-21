package ru.protei.portal.ui.sitefolder.client.column;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

public class AccessParamsColumn extends ClickColumn<Server> {
    @Inject
    public AccessParamsColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.addClassName("server-access-params column-hidable");
        columnHeader.setInnerText(lang.serverAccessParamsColumn());
    }

    @Override
    public void fillColumnValue(Element cell, Server value) {
        Element element = DOM.createDiv();

        element.addClassName("column-hidable access-params");
        element.setInnerText(value.getParams());

        if (isCursorAuto) {
            cell.getStyle().setCursor(Style.Cursor.AUTO);
        }

        cell.appendChild(element);
    }

    public void setCursorAuto(boolean isCursorAuto) {
        this.isCursorAuto = isCursorAuto;
    }

    private boolean isCursorAuto;
    private final Lang lang;
}
