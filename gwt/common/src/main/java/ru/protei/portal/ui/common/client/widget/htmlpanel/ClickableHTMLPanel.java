package ru.protei.portal.ui.common.client.widget.htmlpanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTMLPanel;

public class ClickableHTMLPanel extends HTMLPanel implements HasClickHandlers {
    public ClickableHTMLPanel(String html) {
        super(html);
    }

    public ClickableHTMLPanel(SafeHtml safeHtml) {
        super(safeHtml);
    }

    public ClickableHTMLPanel(String tag, String html) {
        super(tag, html);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }
}
