package ru.protei.portal.ui.common.client.util;

import ru.protei.portal.core.model.helper.HTMLHelper;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.util.MarkdownCore;

/**
 * Client-side implementation of markdown
 * @see ru.protei.portal.core.model.util.MarkdownCore
 * @see ru.protei.portal.util.MarkdownServer
 */
public class MarkdownClient {

    public static String plain2escaped2markdown(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        text = HTMLHelper.htmlEscape(text);
        text = plain2markdown(text);
        return text;
    }

    public static String plain2markdown(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        text = marked(text);
        text = MarkdownCore.replaceAllowedTags(text);
        return text;
    }

    private static native String marked(String text)/*-{
        if (!$wnd.marked) {
            return text;
        }
        return $wnd.marked(text);
    }-*/;
}
