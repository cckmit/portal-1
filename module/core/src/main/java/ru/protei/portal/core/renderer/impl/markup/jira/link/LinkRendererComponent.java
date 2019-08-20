package ru.protei.portal.core.renderer.impl.markup.jira.link;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.embedded.EmbeddedResource;
import com.atlassian.renderer.embedded.EmbeddedResourceParser;
import com.atlassian.renderer.links.Link;
import com.atlassian.renderer.links.LinkResolver;
import com.atlassian.renderer.util.UrlUtil;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.components.AbstractEmbeddedRendererComponent;
import com.atlassian.renderer.v2.components.RendererComponent;
import com.atlassian.renderer.v2.components.link.LinkDecorator;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @see com.atlassian.renderer.v2.components.LinkRendererComponent
 */
public class LinkRendererComponent extends AbstractEmbeddedRendererComponent implements RendererComponent {
    private LinkResolver linkResolver;
    public static final char START_LINK_CHAR = '[';
    private static final char ESCAPE_CHAR = '\\';
    private static final char END_LINK_CHAR = ']';
    private static final char NEW_LINE_CHAR = '\n';
    // Portal-jira-changed: AbstractEmbeddedRendererComponent for this piece if shit
    // Reason - com.atlassian.renderer.v2.components.LinkRendererComponent:147
    private static final Pattern PATTERN_WITH_EMBED;

    public LinkRendererComponent(LinkResolver linkResolver) {
        this.linkResolver = linkResolver;
    }

    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.renderLinks();
    }

    public String render(String wiki, RenderContext context) {
        if (wiki != null && wiki.length() >= 3) {
            InsideLinkPatternCache cache = new InsideLinkPatternCache();

            int end;
            for(int start = findLinkStart(wiki, 0, cache); start != -1; start = findLinkStart(wiki, -end, cache)) {
                end = findLinkEnd(wiki, start);
                if (end == 0) {
                    break;
                }

                if (end >= 0) {
                    return this.renderHeavy(wiki, context, start, end, cache);
                }
            }

            return wiki;
        } else {
            return wiki;
        }
    }

    @Override
    protected EmbeddedResource findResource(RenderContext renderContext, EmbeddedResourceParser embeddedResourceParser, String s) { return null; }

    private String renderHeavy(String wiki, RenderContext context, int start, int end, InsideLinkPatternCache cache) {
        int len = wiki.length();
        StringBuilder sb = new StringBuilder(len / 2 * 3 + 1);
        sb.append(wiki, 0, start);
        this.appendLink(sb, context, wiki.substring(start + 1, end));
        int mark = end + 1;
        start = findLinkStart(wiki, mark, cache);

        while(start != -1) {
            end = findLinkEnd(wiki, start);
            if (end == 0) {
                break;
            }

            if (end < 0) {
                start = findLinkStart(wiki, -end, cache);
            } else {
                sb.append(wiki, mark, start);
                this.appendLink(sb, context, wiki.substring(start + 1, end));
                mark = end + 1;
                start = findLinkStart(wiki, mark, cache);
            }
        }

        return sb.append(wiki, mark, len).toString();
    }

    private void appendLink(StringBuilder stringBuffer, RenderContext context, String linkText) {
        Link link = this.linkResolver.createLink(context, linkText);
        stringBuffer.append(context.getRenderedContentStore().addInline(new LinkDecorator(link)));
    }

    private static boolean isInsideAnyLinkPattern(String wiki, int index, InsideLinkPatternCache cache) {
        return cache.initializeIfNecessary(wiki).isIndexInsidePattern(index);
    }

    private static int findLinkStart(String wiki, int startIndex, InsideLinkPatternCache cache) {
        int len = wiki.length();

        int index;
        for(index = wiki.indexOf(91, startIndex); index != -1; index = wiki.indexOf(91, index + 1)) {
            // Portal-jira-changed: begin - skip link to user profile
            if (index + 1 < wiki.length() && wiki.charAt(index + 1) == '~') {
                continue;
            }
            // Portal-jira-changed: end
            if (index <= 0 || wiki.charAt(index - 1) != '\\') {
                int nextIndex = index + 1;
                if (nextIndex == len) {
                    return -1;
                }

                char nextChar = wiki.charAt(nextIndex);
                if (nextChar != ']' && !Character.isWhitespace(nextChar) && !isInsideAnyLinkPattern(wiki, index, cache)) {
                    break;
                }
            }
        }

        return index;
    }

    private static int findLinkEnd(String wiki, int startIndex) {
        int len = wiki.length();
        char prev = 0;

        for(int index = startIndex + 1; index < len; ++index) {
            char c = wiki.charAt(index);
            switch(c) {
                case '\n':
                    return -(index + 1);
                case '[':
                    if (prev != '\\') {
                        return -index;
                    }
                    break;
                case ']':
                    if (prev != '\\') {
                        return index;
                    }
            }

            prev = c;
        }

        return 0;
    }

    private class InsideLinkPatternCache {
        private NavigableMap<Integer, Integer> indices = null;

        InsideLinkPatternCache() {
        }

        InsideLinkPatternCache initializeIfNecessary(String wiki) {
            if (this.indices == null) {
                this.indices = new TreeMap();
                Matcher matcher = PATTERN_WITH_EMBED.matcher(wiki);

                while(matcher.find()) {
                    this.indices.put(matcher.start(), matcher.end());
                }
            }

            return this;
        }

        boolean isIndexInsidePattern(int index) {
            if (this.indices.isEmpty()) {
                return false;
            } else {
                Map.Entry<Integer, Integer> entry = this.indices.floorEntry(index - 1);
                return entry != null && (Integer)entry.getValue() > index;
            }
        }
    }

    static {
        StringBuilder sb = new StringBuilder("(?<![\\p{Alnum}])((");
        Iterator i$ = UrlUtil.URL_PROTOCOLS.iterator();

        while(i$.hasNext()) {
            String protocol = (String)i$.next();
            sb.append(protocol).append('|');
        }

        sb.setLength(sb.length() - 1);
        sb.append(")([-_.!~*';/?:@#&=%+$,\\p{Alnum}\\[\\]\\(\\)\\\\])+)");
        String regex = sb.toString();
        PATTERN_WITH_EMBED = Pattern.compile(regex + "|" + AbstractEmbeddedRendererComponent.buildPhraseRegExp("\\!", "\\!"));
    }
}
