package ru.protei.portal.ui.common.client.common;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Widget;
import ru.protei.portal.core.model.struct.ContactItem;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.protei.portal.core.model.helper.HelperFunc.isNotEmpty;

/**
 * Оформление почтовых адресов
 */
public class EmailRender {

    public static Element renderToElement(String icon, Stream<ContactItem> stream, String className, boolean showComments) {
        Element div = DOM.createDiv();
        if (stream == null) {
            return div;
        }

        div.addClassName(className);

        if (icon != null) {
            Element i = DOM.createElement("i");
            i.setClassName(icon);
            div.appendChild(i);
        }
        String emailsHtml = showComments ? renderToHtml(stream) : renderToHtml(stream);

        Element span = DOM.createSpan();
        span.setInnerHTML(emailsHtml);
        div.appendChild(span);
        return div;
    }

    public static String renderToHtml(Stream <ContactItem> stream){
        if (stream == null)
            return null;

        return stream
                .map(e -> isNotEmpty(e.value()) ? "<a href='mailto:" + e.value() + "'>" + e.value() + "</a>" : "")
                .collect(Collectors.joining("<span>, </span>"));
    }

    public static List<Widget> renderToHtmlWidget(Stream <ContactItem> stream){
        if (stream == null)
            return null;

        return stream
                .map(EmailRender::createMailLinkWithComments)
                .collect(Collectors.toList());
    }

    public static Widget createMailLinkWithComments(ContactItem item) {
        Anchor a = new Anchor();
        a.setHref("mailto:" + item.value());
        a.setText(item.value());
        return a;
    }
}
