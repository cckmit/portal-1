package ru.protei.portal.ui.common.client.widget.markdown;

import ru.protei.portal.core.model.helper.HTMLHelper;

import java.util.HashMap;
import java.util.Map;

public class Markdown {

    /**
     * Список разрешенных тегов
     * Ключ - тег для замены
     * Значение - замененный тег
     * Пример: для преобразования [quote][/quote] в <blockquote></blockquote>, необходимо put("quote", "blockquote")
     */
    private static final Map<String, String> allowedTagsMap = new HashMap<>();

    static {
        allowedTagsMap.put("quote", "blockquote");
        allowedTagsMap.put("kbd", "kbd");
    }

    public static String plain2escaped2markdown(String text) {
        text = HTMLHelper.htmlEscape(text);
        text = plain2markdown(text);
        return text;
    }

    public static String plain2markdown(String text) {
        text = marked(text);
        for (Map.Entry<String, String> entry : allowedTagsMap.entrySet()) {
            text = text.replaceAll("\\[" + entry.getKey() + "\\]", "<" + entry.getValue() + ">");
            text = text.replaceAll("\\[/" + entry.getKey() + "\\]", "</" + entry.getValue() + ">");
        }
        return text;
    }

    private static native String marked(String text)/*-{
        if (!$wnd.marked) {
            return text;
        }
        return $wnd.marked(text);
    }-*/;
}
