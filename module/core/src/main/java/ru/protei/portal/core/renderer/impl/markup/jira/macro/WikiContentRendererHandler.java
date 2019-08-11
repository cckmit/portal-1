package ru.protei.portal.core.renderer.impl.markup.jira.macro;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.components.MacroTag;
import com.atlassian.renderer.v2.components.WikiContentHandler;

/**
 * Changed MacroRendererComponent
 *
 * @see com.atlassian.renderer.v2.components.WikiContentRendererHandler
 */
public class WikiContentRendererHandler implements WikiContentHandler {
    private MacroRendererComponent macroRendererComponent;
    private RenderContext context;

    // Portal-jira-changed: custom MacroRendererComponent
    public WikiContentRendererHandler(MacroRendererComponent macroRendererComponent, RenderContext context) {
        this.macroRendererComponent = macroRendererComponent;
        this.context = context;
    }

    public void handleMacro(StringBuffer buffer, MacroTag macroTag, String body) {
        this.macroRendererComponent.makeMacro(buffer, macroTag, body, this.context);
    }

    public void handleText(StringBuffer buffer, String s) {
        buffer.append(s);
    }
}
