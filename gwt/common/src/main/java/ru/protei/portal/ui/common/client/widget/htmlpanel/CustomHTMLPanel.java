package ru.protei.portal.ui.common.client.widget.htmlpanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTMLPanel;

public class CustomHTMLPanel extends HTMLPanel implements HasClickHandlers {
    public CustomHTMLPanel(String html) {
        super(html);
    }

    public CustomHTMLPanel(SafeHtml safeHtml) {
        super(safeHtml);
    }

    public CustomHTMLPanel(String tag, String html) {
        super(tag, html);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }
}
