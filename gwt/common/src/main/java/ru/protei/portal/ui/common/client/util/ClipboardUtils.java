package ru.protei.portal.ui.common.client.util;

public class ClipboardUtils {
    public static native int copyToClipboard(String text) /*-{
        var textArea = document.createElement("textarea");
        textArea.style.position = "fixed";
        document.body.appendChild(textArea);

        textArea.value = text;
        textArea.focus();
        textArea.select();

        try {
            var successful = document.execCommand('copy');
            var msg = successful ? 'successful' : 'unsuccessful';
            console.log('Fallback: Copying text command was ' + msg);
            return 0;
        } catch (err) {
            console.error('Fallback: Oops, unable to copy', err);
            return 1;
        } finally {
            textArea.remove();
        }
    }-*/;
}