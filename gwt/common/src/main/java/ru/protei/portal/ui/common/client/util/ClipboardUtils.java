package ru.protei.portal.ui.common.client.util;

public class ClipboardUtils {
    public static native boolean copyToClipboard(String text) /*-{
        var textArea = document.createElement("textarea");
        textArea.style.position = "fixed";
        document.body.appendChild(textArea);

        textArea.value = text;
        textArea.focus();
        textArea.select();

        try {
            return document.execCommand('copy');
        } catch (err) {
            console.error('Fallback: Oops, unable to copy', err);
            return false;
        } finally {
            textArea.remove();
        }
    }-*/;
}