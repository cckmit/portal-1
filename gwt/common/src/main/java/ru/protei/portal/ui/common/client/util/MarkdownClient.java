package ru.protei.portal.ui.common.client.util;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONBoolean;
import ru.protei.portal.core.model.util.MarkdownCore;

/**
 * Client-side implementation of markdown
 * @see ru.protei.portal.core.model.util.MarkdownCore
 * @see ru.protei.portal.util.MarkdownServer
 */
public class MarkdownClient extends MarkdownCore {

    public JavaScriptObjectBuilder options = new JavaScriptObjectBuilder();
    {
        options.put("breaks", JSONBoolean.getInstance(true));
    }

    public static native void setOptions( JavaScriptObject option ) /*-{
        if (!$wnd.marked) {
            return;
        }
        $wnd.marked.setOptions(option);
    }-*/;


    @Override
    protected native String doConvert(String text)/*-{
        if (!$wnd.marked) {
            return text;
        }
        return $wnd.marked(text);
    }-*/;
}
