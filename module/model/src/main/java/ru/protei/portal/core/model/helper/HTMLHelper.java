package ru.protei.portal.core.model.helper;

import ru.protei.portal.core.model.dict.En_TextMarkup;

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

    public static final String THREE_BACKTICKS_ELEMENT = "```";
    public static final String WIKI_MARKUP_CODE_BLOCK = "{code"; // code block ex: {code} {code:java} {code:title=Bar.java|borderStyle=solid} and even more

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
        return replaceHtmlEscapeChars(s);
    }

    public static String htmlEscapeWOCodeBlock(String s, En_TextMarkup textMarkup) {
        if (textMarkup == null) {
            return replaceHtmlEscapeChars(s);
        }
        switch (textMarkup) {
            case MARKDOWN: return htmlEscapeWOCodeBlocks(s, THREE_BACKTICKS_ELEMENT);
            case JIRA_WIKI_MARKUP: return htmlEscapeWOCodeBlocks(s, WIKI_MARKUP_CODE_BLOCK);
        }
        return replaceHtmlEscapeChars(s);
    }

    private static String htmlEscapeWOCodeBlocks(String s, String codeBlockElement) {
        StringBuilder sb = new StringBuilder();
        boolean openElement = false;
        int start = 0;
        while (true) {
            int end = s.indexOf(codeBlockElement, openElement ? start + codeBlockElement.length() : start);
            if (end == -1) {
                sb.append(replaceHtmlEscapeChars(s.substring(start)));
                break;
            }
            sb.append(openElement ? s.substring(start, end += codeBlockElement.length()) : replaceHtmlEscapeChars(s.substring(start, end)));
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
