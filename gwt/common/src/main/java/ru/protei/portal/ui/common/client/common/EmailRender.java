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
        String emailsHtml = showComments ? renderToHtml(stream) : renderToHtml(stream, false);

        Element span = DOM.createSpan();
        span.setInnerHTML(emailsHtml);
        div.appendChild(span);
        return div;
    }

    public static String renderToHtml(Stream <ContactItem> stream){
        if (stream == null)
            return null;

        return stream.map(
                e -> isNotEmpty(e.value()) ? "<a href='mailto:" + e.value() + "'>" + e.value() + (isNotEmpty(e.comment()) ? " (" + e.comment() + ")</a>" : "</a>") : ""
        ).collect(Collectors.joining("<span>, </span>"));
    }

    public static String renderToHtml(Stream <ContactItem> stream, boolean showComments){
        if (stream == null)
            return null;

        if (showComments) return renderToHtml(stream);

        return stream.map(
                e -> isNotEmpty(e.value()) ? "<a href='mailto:" + e.value() + "'>" + e.value() +  "</a>" : ""
        ).collect(Collectors.joining("<span>, </span>"));
    }

}
