package ru.protei.portal.core.model.helper;

public class HTMLHelper {
    public static final String START_COMMENT = "<!--";
    public static final String END_COMMENT = "-->";

    public static final char CLOSE_START_TAG = '>';
    public static final char OPEN_START_TAG = '<';
    public static final String OPEN_END_TAG = "</";
    public static final char CLOSE_END_TAG = '>';

    public static final String CLOSE_EMPTY_ELEMENT = "/>";

    public static String wrap(String tag, String content) {
        return new StringBuilder()
                .append(OPEN_START_TAG).append(tag).append(CLOSE_START_TAG)
                .append(content)
                .append(OPEN_END_TAG).append(tag).append(CLOSE_END_TAG).toString();
    }

    public static String wrapDiv(String content) {
        return wrap("div", content);
    }

    public static String htmlEscape(String s) {
        if (s.indexOf("&") != -1) {
            s = s.replaceAll("&", "&amp;");
        }

        if (s.indexOf("<") != -1) {
            s = s.replaceAll("<", "&lt;");
        }

        if (s.indexOf(">") != -1) {
            s = s.replaceAll(">", "&gt;");
        }

        if (s.indexOf("\"") != -1) {
            s = s.replaceAll("\"", "&quot;");
        }

        if (s.indexOf("'") != -1) {
            s = s.replaceAll("'", "&#39;");
        }

        return s;
    }

    public static String prewrapMessage( String message ) {
//        return message.replaceAll("<(.*?)(\\n\\r|$)+", "<blockquote>$1</blockquote>");
        return message.replaceAll("\\[quote\\]", "<blockquote>")
                .replaceAll("\\[/quote\\]", "</blockquote>");
    }

}
