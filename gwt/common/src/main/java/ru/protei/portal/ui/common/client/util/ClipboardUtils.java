package ru.protei.portal.ui.common.client.util;

public class ClipboardUtils {
    public static String generateOnclick(String text) {
        return
                "            var textArea = document.createElement(\"textarea\");" +
                        "            textArea.style.position = \"fixed\";" +
                        "            document.body.appendChild(textArea);" +
                        "            textArea.value = \"" + text + "\";" +
                        "            textArea.focus();" +
                        "            textArea.select();" +
                        "             try {" +
                        "                  document.execCommand('copy');" +
                        "                  fireSuccessCopyNotify();" +
                        "             } catch (err) {" +
                        "                  fireErrorCopyNotify();" +
                        "             } finally {" +
                        "                  textArea.remove();" +
                        "             }" ;
    }
}