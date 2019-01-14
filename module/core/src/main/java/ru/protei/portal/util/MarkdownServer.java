package ru.protei.portal.util;

import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import ru.protei.portal.core.model.util.MarkdownCore;

import java.util.ArrayList;
import java.util.List;

/**
 * Server-side implementation of markdown
 * @see ru.protei.portal.core.model.util.MarkdownCore
 * @see ru.protei.portal.ui.common.client.util.MarkdownClient
 */
public class MarkdownServer extends MarkdownCore {

    @Override
    protected String doConvert(String text) {
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
