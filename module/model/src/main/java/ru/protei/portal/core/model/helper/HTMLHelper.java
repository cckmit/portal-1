package ru.protei.portal.core.model.helper;

import java.util.HashMap;
import java.util.Map;

public class HTMLHelper {
    public static final String START_COMMENT = "<!--";
    public static final String END_COMMENT = "-->";

    public static final char CLOSE_START_TAG = '>';
    public static final char OPEN_START_TAG = '<';
    public static final String OPEN_END_TAG = "</";
    public static final char CLOSE_END_TAG = '>';

    public static final String CLOSE_EMPTY_ELEMENT = "/>";

    public static final String THREE_BACKTICKET_ELEMENT = "```";

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
        StringBuilder sb = new StringBuilder();
        boolean openElement = false;
        int start = 0;
        while (true) {
            int end = s.indexOf(THREE_BACKTICKET_ELEMENT, openElement? start + THREE_BACKTICKET_ELEMENT.length() : start);
            if (end == -1) {
                sb.append(replaceHtmlEscapeChars(s.substring(start)));
                break;
            }
            sb.append(openElement? s.substring(start, end += THREE_BACKTICKET_ELEMENT.length()) : replaceHtmlEscapeChars(s.substring(start, end)));
            openElement = !openElement;
            start = end;
        }
        return sb.toString();
    }
    static private String replaceHtmlEscapeChars(String s){
        for (Map.Entry<String, String> entry: htmlEscapeChars.entrySet()) {
            if (s.contains(entry.getKey())) s = s.replaceAll(entry.getKey(), entry.getValue());
        }
        return s;
    }
    static private Map<String, String> htmlEscapeChars = new HashMap<>();
    static {
        htmlEscapeChars.put("&", "&amp;");
        htmlEscapeChars.put("<", "&lt;");
        htmlEscapeChars.put("\"", "&quot;");
        htmlEscapeChars.put("'", "&#39;");
    }
}
