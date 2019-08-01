package ru.protei.portal.ui.common.client.common;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import ru.protei.portal.core.model.struct.ContactItem;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.protei.portal.core.model.helper.HelperFunc.isNotEmpty;

/**
 * Оформление почтовых адресов
 */
public class EmailRender {

    public static Element renderToElement(String icon, Stream<ContactItem> stream, String className) {
        Element root = DOM.createDiv();
        if (stream == null)
            return root;

        Element div = DOM.createDiv();
        div.addClassName(className);
        if (icon != null) {
            Element i = DOM.createElement("i");
            i.setClassName(icon);
            div.appendChild(i);
        }
        String emailsHtml = renderToHtml(stream);

        Element span = DOM.createSpan();
        span.setInnerHTML(emailsHtml);
        div.appendChild(span);
        root.appendChild(div);
        return root;
    }

    public static String renderToHtml(Stream <ContactItem> stream){
        if (stream == null)
            return null;

        return stream.map(
                e -> "<a href='mailto:" + e.value() + "'>" + e.value() + (isNotEmpty(e.comment()) ? " (" + e.comment() + ")</a>" : "</a>")
        ).collect(Collectors.joining("<span>, </span>"));
    }
}
