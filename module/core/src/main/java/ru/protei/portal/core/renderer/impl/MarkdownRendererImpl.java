package ru.protei.portal.core.renderer.impl;

import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Image;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.renderer.MarkdownRenderer;

import java.util.*;

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

    /**
     * Адрес с файлами
     */
    private String DOWNLOAD_PATH;

    public MarkdownRendererImpl() {
        allowedTagsMap.put("quote", "blockquote");
        allowedTagsMap.put("kbd", "kbd");

        extensions.add(TablesExtension.create());
        extensions.add(AutolinkExtension.create());
        extensions.add(StrikethroughExtension.create());
    }

    @Autowired
    public void onInit(PortalConfig config) {
        DOWNLOAD_PATH = config.data().getCommonConfig().getCrmUrlFiles() + "springApi/files/";
    }

    @Override
    public String plain2html(String text) {
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

    private String doConvert(String text) {
        Parser parser = Parser.builder()
                .extensions(extensions)
                .build();
        HtmlRenderer renderer = HtmlRenderer.builder()
                .attributeProviderFactory(context -> new ImageAttributeProvider())
                .softbreak("<br/>")
                .escapeHtml(true)
                .extensions(extensions)
                .build();

        Node document = parser.parse(text);
        return renderer.render(document);
    }

    class ImageAttributeProvider implements AttributeProvider {
        @Override
        public void setAttributes(Node node, String tagName, Map<String, String> attributes) {
            if (node instanceof Image) {
                attributes.put("src", DOWNLOAD_PATH + attributes.get("src") + "?" + new Date().getTime());
            }
        }
    }
}
