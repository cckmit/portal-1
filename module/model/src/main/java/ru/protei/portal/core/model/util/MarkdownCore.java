package ru.protei.portal.core.model.util;

import ru.protei.portal.core.model.helper.HTMLHelper;
import ru.protei.portal.core.model.helper.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Core implementation of markdown (client & server)
 * @see ru.protei.portal.util.MarkdownServer
 * @see ru.protei.portal.ui.common.client.util.MarkdownClient
 */
public abstract class MarkdownCore {

    /**
     * Список разрешенных тегов
     * Ключ - тег для замены
     * Значение - замененный тег
     * Пример: для преобразования [quote][/quote] в <blockquote></blockquote>, необходимо put("quote", "blockquote")
     */
    private final Map<String, String> allowedTagsMap = new LinkedHashMap<>();

    public MarkdownCore() {
        allowedTagsMap.put("quote", "blockquote");
        allowedTagsMap.put("kbd", "kbd");
    }

    public String plain2escaped2markdown(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        text = HTMLHelper.htmlEscape(text);
        text = plain2markdown(text);
        return text;
    }

    public String plain2markdown(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        text = doConvert(text);
        text = replaceAllowedTags(text);
        return text;
    }

    private String replaceAllowedTags(String text) {
        for (Map.Entry<String, String> entry : allowedTagsMap.entrySet()) {
            text = text.replaceAll("\\[" + entry.getKey() + "\\]", "<" + entry.getValue() + ">");
            text = text.replaceAll("\\[/" + entry.getKey() + "\\]", "</" + entry.getValue() + ">");
        }
        return text;
    }

    protected abstract String doConvert(String text);
}
