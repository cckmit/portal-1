package ru.protei.portal.ui.common.client.columns;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.Element;

import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;
import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class ActionIconClickColumn<T> extends ClickColumn<T> {

    public ActionIconClickColumn(String icon, String title, String columnClassName) {
        this.icon = icon;
        this.title = title;
        this.columnClassName = columnClassName;
        this.disabledClassName = "link-disabled";
        this.debugId = null;
    }

    public ActionIconClickColumn(String icon, String title, String columnClassName, String debugId) {
        this.icon = icon;
        this.title = title;
        this.columnClassName = columnClassName;
        this.disabledClassName = "link-disabled";
        this.debugId = debugId;
    }

    public ActionIconClickColumn(String icon, String title, String columnClassName, String disabledClassName, String debugId) {
        this.icon = icon;
        this.title = title;
        this.columnClassName = columnClassName;
        this.disabledClassName = disabledClassName;
        this.debugId = debugId;
    }

    @Override
    protected String getColumnClassName() {
        return columnClassName;
    }

    @Override
    protected void fillColumnHeader(Element columnHeader) {}

    @Override
    public void fillColumnValue(Element cell, T value) {
        cell.appendChild(makeIconNode(value));
    }

    private Node makeIconNode(T value) {
        AnchorElement anchor = Document.get().createAnchorElement();
        anchor.setHref("#");
        if (isNotEmpty(icon)) anchor.addClassName(icon);
        if (isNotEmpty(title)) anchor.setTitle(title);
        if (isNotEmpty(debugId)) anchor.setAttribute(DEBUG_ID_ATTRIBUTE, debugId);
        if (isNotEmpty(disabledClassName)) {
            if (enabledPredicate == null || enabledPredicate.isEnabled(value)) {
                anchor.removeClassName(disabledClassName);
            } else {
                anchor.addClassName(disabledClassName);
            }
        }
        return anchor;
    }

    private final String icon;
    private final String title;
    private final String columnClassName;
    private final String disabledClassName;
    private final String debugId;
}
