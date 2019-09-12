package ru.protei.portal.ui.common.client.common;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTMLPanel;

public class ClickHTMLPanel extends HTMLPanel implements HasClickHandlers {

    public ClickHTMLPanel(String html) {
        super(html);
    }

    public ClickHTMLPanel(SafeHtml safeHtml) {
        super(safeHtml);
    }

    public ClickHTMLPanel(String tag, String html) {
        super(tag, html);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }
}
