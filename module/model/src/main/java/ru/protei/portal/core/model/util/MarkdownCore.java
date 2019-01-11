package ru.protei.portal.core.model.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Core implementation of markdown (client & server)
 * @see ru.protei.portal.util.MarkdownServer
 * @see ru.protei.portal.ui.common.client.util.MarkdownClient
 */
public class MarkdownCore {

    /**
     * Список разрешенных тегов
     * Ключ - тег для замены
     * Значение - замененный тег
     * Пример: для преобразования [quote][/quote] в <blockquote></blockquote>, необходимо put("quote", "blockquote")
     */
    private static final Map<String, String> allowedTagsMap = new LinkedHashMap<>();

    static {
        allowedTagsMap.put("quote", "blockquote");
        allowedTagsMap.put("kbd", "kbd");
    }

    public static String replaceAllowedTags(String text) {
        for (Map.Entry<String, String> entry : allowedTagsMap.entrySet()) {
            text = text.replaceAll("\\[" + entry.getKey() + "\\]", "<" + entry.getValue() + ">");
            text = text.replaceAll("\\[/" + entry.getKey() + "\\]", "</" + entry.getValue() + ">");
        }
        return text;
    }
}
