package ru.protei.portal.ui.common.client.util;

public class ClipboardUtils {
    public static native boolean copyToClipboard(String text) /*-{
        var textArea = document.createElement("textarea");
        textArea.style.position = "fixed";
        document.body.appendChild(textArea);
        textArea.value = text
        textArea.focus()
        textArea.select()
        try {
            document.execCommand('copy');
            return true;
        } catch (err) {
            return false;
        } finally {
            textArea.remove();
        }
    }-*/;
}