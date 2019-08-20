package ru.protei.portal.core.renderer.impl.markup.custom.userlink;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.components.RendererComponent;

/**
 * Custom render component to render user links
 */
public class UserLinkRendererComponent implements RendererComponent {

    private static final char START_LINK_CHAR = '[';
    private static final char START_USER_LINK_CHAR = '~';
    private static final char END_LINK_CHAR = ']';
    private static final char NEW_LINE_CHAR = '\n';
    private static final char ESCAPE_CHAR = '\\';
    private static final String USER_LINK_TEMPLATE = "(%s ^user^)";

    @Override
    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.renderLinks();
    }

    @Override
    public String render(String wiki, RenderContext context) {

        if (wiki == null || wiki.length() < 3) {
            return wiki;
        }

        int end;
        for (int start = findLinkStart(wiki, 0); start != -1; start = findLinkStart(wiki, -end)) {

            end = findLinkEnd(wiki, start);
            if (end == 0) {
                break;
            }

            if (end >= 0) {
                return renderHeavy(wiki, context, start, end);
            }
        }

        return wiki;
    }

    private void appendUserLink(StringBuilder sb, RenderContext context, String linkText) {

        if (linkText == null || linkText.isEmpty()) {
            sb.append(linkText);
            return;
        }

        if (linkText.charAt(0) == START_USER_LINK_CHAR) {
            linkText = linkText.substring(1);
        }

        sb.append(String.format(USER_LINK_TEMPLATE, linkText));
    }

    private int findLinkStart(String wiki, int startIndex) {
        int len = wiki.length();
        int index;
        for (index = wiki.indexOf(START_LINK_CHAR, startIndex); index != -1; index = wiki.indexOf(START_LINK_CHAR, index + 1)) {
            if (index <= 0 || wiki.charAt(index - 1) != ESCAPE_CHAR) {
                int nextIndex = index + 1;
                if (nextIndex == len) {
                    return -1;
                }
                char nextChar = wiki.charAt(nextIndex);

                if (nextChar != START_USER_LINK_CHAR) {
                    continue;
                }

                int next2Index = index + 2;
                if (next2Index == len) {
                    return -1;
                }
                char next2Char = wiki.charAt(next2Index);

                if (next2Char != END_LINK_CHAR && !Character.isWhitespace(next2Char)) {
                    break;
                }
            }
        }
        return index;
    }

    private int findLinkEnd(String wiki, int startIndex) {
        int len = wiki.length();
        char prev = 0;

        for (int index = startIndex + 1; index < len; ++index) {
            char c = wiki.charAt(index);
            switch (c) {
                case NEW_LINE_CHAR:
                    return -(index + 1);
                case START_LINK_CHAR:
                    if (prev != ESCAPE_CHAR) {
                        return -index;
                    }
                    break;
                case END_LINK_CHAR:
                    if (prev != ESCAPE_CHAR) {
                        return index;
                    }
            }

            prev = c;
        }

        return 0;
    }

    private String renderHeavy(String wiki, RenderContext context, int start, int end) {
        int len = wiki.length();
        StringBuilder sb = new StringBuilder(len / 2 * 3 + 1);
        sb.append(wiki, 0, start);
        this.appendUserLink(sb, context, wiki.substring(start + 1, end));
        int mark = end + 1;
        start = findLinkStart(wiki, mark);

        while (start != -1) {
            end = findLinkEnd(wiki, start);
            if (end == 0) {
                break;
            }
            if (end < 0) {
                start = findLinkStart(wiki, -end);
            } else {
                sb.append(wiki, mark, start);
                this.appendUserLink(sb, context, wiki.substring(start + 1, end));
                mark = end + 1;
                start = findLinkStart(wiki, mark);
            }
        }

        return sb.append(wiki, mark, len).toString();
    }
}
