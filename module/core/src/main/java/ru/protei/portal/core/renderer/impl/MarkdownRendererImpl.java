package ru.protei.portal.core.renderer.impl;

import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import ru.protei.portal.core.renderer.MarkdownRenderer;
import ru.protei.portal.core.model.helper.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MarkdownRendererImpl implements MarkdownRenderer {

    /**
     * Список разрешенных тегов
     * Ключ - тег для замены
     * Значение - замененный тег
     * Пример: для преобразования [quote][/quote] в <blockquote></blockquote>, необходимо put("quote", "blockquote")
     */
    private final Map<String, String> allowedTagsMap = new LinkedHashMap<>();

    /**
     * Список commonmark расширений
     */
    private final List<Extension> extensions = new ArrayList<>();

    public MarkdownRendererImpl() {

        allowedTagsMap.put("quote", "blockquote");
        allowedTagsMap.put("kbd", "kbd");

        extensions.add(TablesExtension.create());
        extensions.add(AutolinkExtension.create());
        extensions.add(StrikethroughExtension.create());
    }

    @Override
    public String plain2html(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        text = doConvert(text);
        text = addBreakOnCaretReturn(text);
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

    private String addBreakOnCaretReturn(String text) {
        return text.trim().replaceAll("((?<!<br />)[\r\n|\n|\r])", "<br/>");
    }

    private String doConvert(String text) {
        Parser parser = Parser.builder()
                .extensions(extensions)
                .build();
        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(extensions)
                .build();

        Node document = parser.parse(text);
        return renderer.render(document);
    }
}
