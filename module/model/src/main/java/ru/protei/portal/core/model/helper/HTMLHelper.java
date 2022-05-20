package ru.protei.portal.core.model.helper;

public class HTMLHelper {

    public static final char CLOSE_START_TAG = '>';
    public static final char OPEN_START_TAG = '<';
    public static final String OPEN_END_TAG = "</";
    public static final char CLOSE_END_TAG = '>';

    static String wrap(String tag, String content) {
        return new StringBuilder()
                .append(OPEN_START_TAG).append(tag).append(CLOSE_START_TAG)
                .append(content)
                .append(OPEN_END_TAG).append(tag).append(CLOSE_END_TAG).toString();
    }

    public static String wrapDiv(String content) {
        return wrap("div", content);
    }
}