package ru.protei.portal.util;

import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import ru.protei.portal.core.model.helper.HTMLHelper;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.util.MarkdownCore;

import java.util.ArrayList;
import java.util.List;

/**
 * Server-side implementation of markdown
 * @see ru.protei.portal.core.model.util.MarkdownCore
 * @see ru.protei.portal.ui.common.client.util.MarkdownClient
 */
public class MarkdownServer {

    public static String plain2escaped2markdown(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        text = HTMLHelper.htmlEscape(text);
        text = plain2markdown(text);
        return text;
    }

    public static String plain2markdown(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        text = commonmark(text);
        text = MarkdownCore.replaceAllowedTags(text);
        return text;
    }

    private static String commonmark(String text) {

        List<Extension> extensions = new ArrayList<>();
        extensions.add(TablesExtension.create());
        extensions.add(AutolinkExtension.create());
        extensions.add(StrikethroughExtension.create());

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
