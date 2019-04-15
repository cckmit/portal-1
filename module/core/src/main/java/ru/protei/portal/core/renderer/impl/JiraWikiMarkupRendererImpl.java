package ru.protei.portal.core.renderer.impl;

import com.atlassian.renderer.*;
import com.atlassian.renderer.embedded.DefaultEmbeddedResourceRenderer;
import com.atlassian.renderer.links.GenericLinkParser;
import com.atlassian.renderer.links.Link;
import com.atlassian.renderer.links.LinkResolver;
import com.atlassian.renderer.links.UrlLink;
import com.atlassian.renderer.v2.*;
import com.atlassian.renderer.v2.components.*;
import com.atlassian.renderer.v2.components.block.*;
import com.atlassian.renderer.v2.components.list.ListBlockRenderer;
import com.atlassian.renderer.v2.components.phrase.DashRendererComponent;
import com.atlassian.renderer.v2.components.phrase.ForceNewLineRendererComponent;
import com.atlassian.renderer.v2.components.phrase.NewLineRendererComponent;
import com.atlassian.renderer.v2.components.phrase.PhraseRendererComponent;
import com.atlassian.renderer.v2.components.table.TableBlockRenderer;
import com.atlassian.renderer.v2.macro.DefaultMacroManager;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.v2.macro.MacroManager;
import com.atlassian.renderer.v2.macro.code.CodeMacro;
import com.atlassian.renderer.v2.macro.code.formatter.AbstractFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.renderer.JiraWikiMarkupRenderer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JiraWikiMarkupRendererImpl implements JiraWikiMarkupRenderer {

    @Override
    public String plain2html(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        text = doConvert(text);
        return text;
    }

    private String doConvert(String text) {
        RenderContext context = makeRenderContext();
        V2RendererFacade renderer = getRendererFacade();
        String content = renderer.convertWikiToXHtml(context, text);
        return restoreContentFromContentStore(context, content);
    }

    private String restoreContentFromContentStore(RenderContext context, String content) {
        for (TokenType tokenType : TokenType.values()) {
            Pattern p = Pattern.compile(".*(" + tokenType.getTokenPatternString() + ").*", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(content);
            if (m.find() && m.groupCount() > 0) {
                String token = m.group(1);
                content = content.replace(token, renderFromContentStore(context, token));
                return restoreContentFromContentStore(context, content);
            }
        }
        return content;
    }

    private String renderFromContentStore(RenderContext context, String token) {
        RenderedContentStore store = context.getRenderedContentStore();
        Object obj = store.get(token);
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof Renderable) {
            StringBuffer sb = new StringBuffer();
            ((Renderable) obj).render(getSubRenderer(), context, sb);
            return sb.toString();
        }
        return "";
    }

    private List<RendererComponent> getComponents() {
        if (CollectionUtils.isNotEmpty(components)) {
            return components;
        }
        // Order does matter! Components will be executed one by one. Components may conflict with each other.
        components = new ArrayList<>();
        components.add(new BackslashEscapeRendererComponent());
        components.add(new LinkRendererComponent(makeLinkResolver()));
        components.add(new UrlRendererComponent(makeLinkResolver()));
        components.add(new EmbeddedImageRendererComponent());
        components.add(new EmbeddedUnembeddableRendererComponent());
        components.add(new EmbeddedObjectRendererComponent());
        components.add(PhraseRendererComponent.getDefaultRenderer("citation"));
        components.add(PhraseRendererComponent.getDefaultRenderer("strong"));
        components.add(PhraseRendererComponent.getDefaultRenderer("superscript"));
        components.add(PhraseRendererComponent.getDefaultRenderer("subscript"));
        components.add(PhraseRendererComponent.getDefaultRenderer("emphasis"));
        components.add(PhraseRendererComponent.getDefaultRenderer("deleted"));
        components.add(PhraseRendererComponent.getDefaultRenderer("inserted"));
        components.add(PhraseRendererComponent.getDefaultRenderer("monospaced"));
        components.add(new ForceNewLineRendererComponent());
        components.add(new MacroRendererComponent(getMacroManager(), getSubRenderer()));
        components.add(new DashRendererComponent());
        components.add(new NewLineRendererComponent()); /* Remove if new line on "\n" no needed */
        List<BlockRenderer> blockRendererList = new ArrayList<>();
        blockRendererList.add(new HeadingBlockRenderer());
        blockRendererList.add(new TableBlockRenderer());
        blockRendererList.add(new BlockquoteBlockRenderer());
        blockRendererList.add(new ListBlockRenderer());
        blockRendererList.add(new HorizontalRuleBlockRenderer());
        components.add(new BlockRendererComponent(getSubRenderer(), blockRendererList));
        return components;
    }

    private Renderer getRenderer() {
        if (renderer != null) {
            return renderer;
        }
        return renderer = new V2Renderer(getComponents());
    }

    private V2SubRenderer getSubRenderer() {
        if (subRenderer != null) {
            return subRenderer;
        }
        return subRenderer = new V2SubRenderer(getRenderer());
    }

    private V2RendererFacade getRendererFacade() {
        if (rendererFacade != null) {
            return rendererFacade;
        }
        rendererFacade = new V2RendererFacade();
        rendererFacade.setRenderer(getRenderer());
        return rendererFacade;
    }

    private MacroManager getMacroManager() {
        if (macroManager != null) {
            return macroManager;
        }
        DefaultMacroManager macroManager = new DefaultMacroManager(getSubRenderer());
        Macro codeMacro = new CodeMacro(getSubRenderer(), Collections.singletonList(new JiraWikiMarkupMacroNoneFormatter()));
        macroManager.unregisterMacro("code");
        macroManager.registerMacro("code", codeMacro);
        return this.macroManager = macroManager;
    }

    private RenderContext makeRenderContext() {
        RenderContext renderContext = new RenderContext();
        renderContext.setSiteRoot(config.data().getCommonConfig().getCrmUrlCurrent());
        renderContext.setImagePath(renderContext.getSiteRoot() + "images");
        renderContext.setLinkRenderer(new V2LinkRenderer(getSubRenderer(), makeIconManager(), makeRendererConfiguration()));
        renderContext.setEmbeddedResourceRenderer(new DefaultEmbeddedResourceRenderer());
        renderContext.setCharacterEncoding(CHARACTER_ENCODING);
        renderContext.setRenderingForWysiwyg(false);
        return renderContext;
    }

    private LinkResolver makeLinkResolver() {
        return new LinkResolver() {
            @Override
            public Link createLink(RenderContext renderContext, String s) {
                return new UrlLink(new GenericLinkParser(s));
            }
            /* UrlRendererComponent and LinkRendererComponent dont use following methods */
            @Override
            public List extractLinkTextList(String s) { return null; }
            @Override
            public List extractLinks(RenderContext renderContext, String s) { return null; }
            @Override
            public String removeLinkBrackets(String s) { return null; }
        };
    }

    private IconManager makeIconManager() {
        return new IconManager() {
            @Override
            public Icon getLinkDecoration(String s) {
                switch (s) {
                    case "mailto": return Icon.makeRenderIcon("mail_small.png", 1, 10, 10);
                    case "external": return Icon.makeRenderIcon("link_small.png", 1, 10, 10);
                }
                return null;
            }
            /* Following methods dont matter without EmoticonRendererComponent */
            @Override
            public Icon getEmoticon(String s) { return null; }
            @Override
            public String[] getEmoticonSymbols() { return new String[0]; }
        };
    }

    private RendererConfiguration makeRendererConfiguration() {
        return new RendererConfiguration() {
            @Override
            public String getWebAppContextPath() {
                return config.data().getCommonConfig().getCrmUrlCurrent();
            }
            @Override
            public boolean isNofollowExternalLinks() { return true; }
            @Override
            public boolean isAllowCamelCase() { return false; }
            @Override
            public String getCharacterEncoding() { return CHARACTER_ENCODING; }
        };
    }

    @Autowired
    private PortalConfig config;
    private List<RendererComponent> components;
    private Renderer renderer;
    private V2SubRenderer subRenderer;
    private V2RendererFacade rendererFacade;
    private MacroManager macroManager;
    private static final String CHARACTER_ENCODING = StandardCharsets.UTF_8.name();

    /**
     * "java" language used by default when no language defined
     * @see com.atlassian.renderer.v2.macro.code.formatter.NoneFormatter
     * @see com.atlassian.renderer.v2.macro.code.CodeMacro#getLanguage(java.util.Map)
     */
    private class JiraWikiMarkupMacroNoneFormatter extends AbstractFormatter {
        private final String[] SUPPORTED_LANGUAGES = new String[]{"none", "java"};
        @Override
        public String[] getSupportedLanguages() {
            return SUPPORTED_LANGUAGES;
        }
    }
}
