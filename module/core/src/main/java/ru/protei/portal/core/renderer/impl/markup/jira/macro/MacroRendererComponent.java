package ru.protei.portal.core.renderer.impl.markup.jira.macro;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.components.*;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.renderer.v2.macro.MacroManager;
import com.atlassian.util.profiling.UtilTimerStack;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom macro component for jira wiki markup
 * Created to deal with content that begins with { symbol and actually not macro
 *
 * All changes marked by comments starts with "Portal-jira-changed"
 *
 * @see com.atlassian.renderer.v2.components.MacroRendererComponent
 */
public class MacroRendererComponent extends AbstractRendererComponent {

    private static final Logger log = LoggerFactory.getLogger(MacroRendererComponent.class);
    private final CustomMacroManager macroManager;
    private final SubRenderer subRenderer;
//    Portal-jira-changed: wysiwyg not supported
//    private final WysiwygMacroHelper wysiwygMacroHelper;
    private static final Logger timeLogger = LoggerFactory.getLogger("macroLogger");

    public MacroRendererComponent(CustomMacroManager macroManager, SubRenderer subRenderer) {
        this.macroManager = macroManager;
        this.subRenderer = subRenderer;
//        Portal-jira-changed: wysiwyg not supported
//        this.wysiwygMacroHelper = new WysiwygMacroHelper(this);
    }

    @Override
    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.renderMacros();
    }

    @Override
    public String render(String wiki, RenderContext context) {
//        Portal-jira-changed: change WikiMarkupParser and WikiContentRendererHandler realization
//        WikiMarkupParser parser = new WikiMarkupParser(this.macroManager, new WikiContentRendererHandler(this, context));
//        return parser.parse(wiki);
        WikiMarkupParser parser = new WikiMarkupParser(this.macroManager, new WikiContentRendererHandler(this, context));
        return parser.parse(wiki);
    }

    public void makeMacro(StringBuffer buffer, MacroTag startTag, String body, RenderContext context) {
        Date now = new Date();
        Macro macro = this.getMacroByName(startTag.command);
        Map<String, Object> params = this.makeParams(startTag.argString);
        if ((Boolean) RssThreadLocal.inRss.get() && ((List)RssThreadLocal.blackListedMacros.get()).contains(startTag.command)) {
            buffer.append("Macro " + startTag.command + " cannot be used within RSS requests");
        } else {
//            Portal-jira-changed: wysiwyg not supported
//            if (context.isRenderingForWysiwyg()) {
//                this.wysiwygMacroHelper.renderMacro(startTag, macro, body, params, context, buffer);
//            } else
            if (macro != null) {
                this.processMacro(startTag.command, macro, body, params, context, buffer);
            } else {
                this.handleUnknownMacroTag(buffer, startTag, body, context);
            }

            Date after = new Date();
            if ((Boolean)RssThreadLocal.inRss.get() && timeLogger.isDebugEnabled()) {
                long entityId = -1L;
                timeLogger.debug(startTag + "=" + (after.getTime() - now.getTime()) + ":" + entityId);
            }

        }
    }

    private void handleUnknownMacroTag(StringBuffer buffer, MacroTag startTag, String body, RenderContext context) {
        if (!context.getRenderMode().renderMacroErrorMessages()) {
            HtmlEscapeRendererComponent htmlEscapeRendererComponent = new HtmlEscapeRendererComponent();
            StringBuffer errorBuffer = new StringBuffer();
            errorBuffer.append(htmlEscapeRendererComponent.render(startTag.originalText, context));
            if (StringUtils.isNotBlank(body)) {
                errorBuffer.append(this.subRenderer.render(body, context, context.getRenderMode().and(RenderMode.suppress(257L))));
                errorBuffer.append("{").append(htmlEscapeRendererComponent.render(startTag.command, context)).append("}");
            }

            buffer.append(context.addRenderedContent(errorBuffer.toString()));
        } else {
            buffer.append(this.makeMacroError(context, "Unknown macro: {" + startTag.command + "}", body));
        }

    }

    private Macro getMacroByName(String name) {
        return name == null ? null : this.macroManager.getEnabledMacro(name.toLowerCase());
    }

    private Map<String, Object> makeParams(String paramString) {
        Map<String, Object> params = new HashMap();
        params.put(": = | RAW | = :", paramString == null ? "" : paramString);
        if (StringUtils.isEmpty(paramString)) {
            return params;
        } else {
            String[] paramStrs = paramString.split("\\|");

            for(int i = 0; i < paramStrs.length; ++i) {
                String paramStr = paramStrs[i];
                int idx;
                if ((idx = paramStr.indexOf("=")) != -1) {
                    if (idx == paramStr.length() - 1) {
                        params.put(paramStr.substring(0, idx).trim(), "");
                    } else {
                        params.put(paramStr.substring(0, idx).trim(), paramStr.substring(idx + 1).trim());
                    }
                } else {
                    params.put(String.valueOf(i), paramStr);
                }
            }

            return params;
        }
    }

    public void processMacro(String command, Macro macro, String body, Map<String, Object> params, RenderContext context, StringBuffer buffer) {
        String renderedBody = body;

        try {
            if (StringUtils.isNotEmpty(body) && macro.getBodyRenderMode() != null && !macro.getBodyRenderMode().renderNothing()) {
                RenderMode macroMode = macro.getBodyRenderMode();
                if (context.isRenderingForWysiwyg() && macroMode.renderParagraphs()) {
                    renderedBody = RenderUtils.trimInitialNewline(renderedBody);
                }

                renderedBody = this.subRenderer.render(renderedBody, context, macroMode);
            }

            String macroResult = this.executeMacro(command, macro, params, renderedBody, context);
            if (macro.getBodyRenderMode() == null) {
                buffer.append(this.subRenderer.render(macroResult, context, RenderMode.MACROS_ONLY));
            } else {
                buffer.append(context.addRenderedContent(macroResult, macro.getTokenType(params, body, context)));
            }
        } catch (MacroException var9) {
            log.info("Error formatting macro: " + command + ": " + var9, var9);
            buffer.append(this.makeMacroError(context, command + ": " + var9.getMessage(), body));
        } catch (Throwable var10) {
            log.error("Unexpected error formatting macro: " + command, var10);
            buffer.append(this.makeMacroError(context, "Error formatting macro: " + command + ": " + var10.toString(), body));
        }

    }

    protected String executeMacro(String command, Macro macro, Map<String, Object> params, String renderedBody, RenderContext context) throws MacroException {
        String profilingName = "Rendering macro: {" + command + "}";
        long startTime = System.currentTimeMillis();

        String var9;
        try {
            UtilTimerStack.push(profilingName);
            var9 = macro.execute(params, renderedBody, context);
        } finally {
//            Portal-jira-changed
//            log.debug("Rendering macro \\{{}} took {} ms with parameters: {}", new Object[]{command, System.currentTimeMillis() - startTime, params});
            log.debug("Rendering macro {} took {} ms with parameters: {}", command, System.currentTimeMillis() - startTime, params);
            UtilTimerStack.pop(profilingName);
        }

        return var9;
    }

    private String makeMacroError(RenderContext context, String errorMessage, String body) {
        return context.addRenderedContent(RenderUtils.blockError(errorMessage, this.renderErrorBody(body, context)));
    }

    private String renderErrorBody(String body, RenderContext context) {
        return context.addRenderedContent(this.subRenderer.render(body, context, (RenderMode)null));
    }

    public SubRenderer getSubRenderer() {
        return this.subRenderer;
    }
}
