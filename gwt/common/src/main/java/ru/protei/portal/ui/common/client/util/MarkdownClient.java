package ru.protei.portal.ui.common.client.util;

import ru.protei.portal.core.model.util.MarkdownCore;

/**
 * Client-side implementation of markdown
 * @see ru.protei.portal.core.model.util.MarkdownCore
 * @see ru.protei.portal.util.MarkdownServer
 */
public class MarkdownClient extends MarkdownCore {

    @Override
    protected native String doConvert(String text)/*-{
        if (!$wnd.marked) {
            return text;
        }
        return $wnd.marked(text);
    }-*/;
}
