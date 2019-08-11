package ru.protei.portal.core.renderer.impl.markup.jira.phrase;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.components.AbstractRendererComponent;
import com.atlassian.renderer.v2.components.block.LineWalker;

import java.util.regex.Pattern;

/**
 * @see com.atlassian.renderer.v2.components.phrase.NewLineRendererComponent
 */
public class NewLineRendererComponent extends AbstractRendererComponent {

    private static final Pattern STARTS_WITH_BLOCK = Pattern.compile(TokenType.BLOCK.getTokenPatternString() + ".*");

    public NewLineRendererComponent() {}

    @Override
    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.renderLinebreaks();
    }

    @Override
    public String render(String wiki, RenderContext context) {

        if (!wiki.contains("\n")) {
            return wiki;
        }

        StringBuilder out = new StringBuilder(wiki.length());
        LineWalker walker = new LineWalker(wiki);

        while (true) {
            String line;
            do {
                if (!walker.hasNext()) {
                    return out.toString();
                }

                line = walker.next();
                out.append(line);
            } while (!walker.hasNext());

            String nextLine = walker.peek();
            if (!nextLine.trim().startsWith("<br") &&
                !line.trim().endsWith("<br class=\"atl-forced-newline\" />") &&
                !isLinesTwoTableRows(line, nextLine) && // Portal-jira-changed: check if two lines is not two table rows
                !STARTS_WITH_BLOCK.matcher(nextLine).matches()
            ) {
                out.append("<br/>\n");
            } else {
                out.append("\n");
            }
        }
    }

    // Portal-jira-changed: added method
    private boolean isLinesTwoTableRows(String line1, String line2) {
        return line1.trim().endsWith("|") || line2.trim().startsWith("|");
    }
}
