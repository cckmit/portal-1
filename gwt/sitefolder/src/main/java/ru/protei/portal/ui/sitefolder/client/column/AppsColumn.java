package ru.protei.portal.ui.sitefolder.client.column;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.lang.Lang;

public class AppsColumn extends ClickColumn<Server> {
    @Inject
    public AppsColumn(Lang lang) {
        this.lang = lang;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {
        columnHeader.setClassName("server-apps");
        columnHeader.setInnerText(lang.siteFolderApps());
    }

    @Override
    public void fillColumnValue(Element cell, Server value) {
        Element element = DOM.createDiv();

        element.setInnerText((value.getApplicationsCount() == null ? "0" : String.valueOf(value.getApplicationsCount())) + " " +lang.amountShort());
        AnchorElement a = DOM.createAnchor().cast();
        a.setHref("#");
        a.addClassName("fa fa-share cell-inline-icon");
        a.setTitle(lang.siteFolderApps());
        element.appendChild(a);

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

