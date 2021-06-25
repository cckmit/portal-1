package ru.protei.portal.core.renderer.impl;

import com.atlassian.renderer.*;
import com.atlassian.renderer.attachments.RendererAttachment;
import com.atlassian.renderer.attachments.RendererAttachmentManager;
import com.atlassian.renderer.embedded.DefaultEmbeddedResourceRenderer;
import com.atlassian.renderer.embedded.EmbeddedImage;
import com.atlassian.renderer.embedded.EmbeddedResource;
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
import com.atlassian.renderer.v2.components.phrase.PhraseRendererComponent;
import com.atlassian.renderer.v2.components.table.TableBlockRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.renderer.JiraWikiMarkupRenderer;
import ru.protei.portal.core.renderer.impl.markup.custom.userlink.UserLinkRendererComponent;
import ru.protei.portal.core.renderer.impl.markup.jira.link.LinkRendererComponent;
import ru.protei.portal.core.renderer.impl.markup.jira.macro.CustomMacroManager;
import ru.protei.portal.core.renderer.impl.markup.jira.macro.MacroRendererComponent;
import ru.protei.portal.core.renderer.impl.markup.jira.phrase.NewLineRendererComponent;
import ru.protei.portal.core.service.AttachmentService;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JiraWikiMarkupRendererImpl implements JiraWikiMarkupRenderer {

    public JiraWikiMarkupRendererImpl(PortalConfig config) {
        // Order does matter! Components will be executed one by one. Components may conflict with each other.
        // special NewLineRendererComponent
        List<RendererComponent> renderComponents = new ArrayList<>();
        renderComponents.add(new BackslashEscapeRendererComponent());
        renderComponents.add(new LinkRendererComponent(makeLinkResolver()));
        renderComponents.add(new UrlRendererComponent(makeLinkResolver()));
        renderComponents.add(new UserLinkRendererComponent());
        renderComponents.add(new EmbeddedImageRendererComponent());
        renderComponents.add(new EmbeddedUnembeddableRendererComponent());
        renderComponents.add(new EmbeddedObjectRendererComponent());
        renderComponents.add(PhraseRendererComponent.getDefaultRenderer("citation"));
        renderComponents.add(PhraseRendererComponent.getDefaultRenderer("strong"));
        renderComponents.add(PhraseRendererComponent.getDefaultRenderer("superscript"));
        renderComponents.add(PhraseRendererComponent.getDefaultRenderer("subscript"));
        renderComponents.add(PhraseRendererComponent.getDefaultRenderer("emphasis"));
        renderComponents.add(PhraseRendererComponent.getDefaultRenderer("deleted"));
        renderComponents.add(PhraseRendererComponent.getDefaultRenderer("inserted"));
        renderComponents.add(PhraseRendererComponent.getDefaultRenderer("monospaced"));
        renderComponents.add(new ForceNewLineRendererComponent());
        V2SubRenderer macroSubRenderer = new V2SubRenderer(new V2Renderer(renderComponents));
        MacroRendererComponent macroRendererComponent = new MacroRendererComponent(new CustomMacroManager(macroSubRenderer), macroSubRenderer);

        List<RendererComponent> renderComponentsAfterMacro = new ArrayList<>(renderComponents);
        renderComponentsAfterMacro.add(macroRendererComponent);
        renderComponentsAfterMacro.add(new DashRendererComponent());
        renderComponentsAfterMacro.add(new NewLineRendererComponent());

        List<BlockRenderer> blockRendererList = new ArrayList<>();
        blockRendererList.add(new TableBlockRenderer());
        blockRendererList.add(new HeadingBlockRenderer());
        blockRendererList.add(new BlockquoteBlockRenderer());
        blockRendererList.add(new ListBlockRenderer());
        blockRendererList.add(new HorizontalRuleBlockRenderer());
        BlockRendererComponent blockRendererComponent = new BlockRendererComponent(
                new V2SubRenderer(new V2Renderer(renderComponents)),
                blockRendererList);

        List<RendererComponent> renderComponentsAfterBlock = new ArrayList<>(renderComponentsAfterMacro);
        renderComponentsAfterBlock.add(blockRendererComponent);

        subRenderer = new V2SubRenderer(new V2Renderer(renderComponentsAfterBlock));
        rendererFacade = new V2RendererFacade();
        rendererFacade.setRenderer(new V2Renderer(renderComponentsAfterBlock));

        iconManager = new JiraIconManager();

        renderContext = makeRenderContext(config, subRenderer, iconManager);
    }

    @Override
    public String plain2html(String text) {
        return plain2html(text, true);
    }

    @Override
    public String plain2html(String text, boolean renderIcons) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        text = doConvert(text, renderIcons);
        return text;
    }

    private String doConvert(String text, boolean renderIcons) {
        RenderContext renderContext = getRenderContext(renderIcons);
        String content = rendererFacade.convertWikiToXHtml(renderContext, text);
        return restoreContentFromContentStore(renderContext, content);
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
            ((Renderable) obj).render(subRenderer, context, sb);
            return sb.toString();
        }
        return "";
    }

    private RenderContext getRenderContext(boolean renderIcons) {
        iconManager.setRenderIcons(renderIcons);
        return renderContext;
    }

    private RenderContext makeRenderContext(PortalConfig config, SubRenderer subRenderer, IconManager iconManager) {
        RenderContext renderContext = new RenderContext();
        renderContext.setSiteRoot(config.data().getCommonConfig().getCrmUrlCurrent());
        renderContext.setImagePath(renderContext.getSiteRoot() + "images");
        renderContext.setLinkRenderer(new V2LinkRenderer(subRenderer, iconManager, makeRendererConfiguration(config)));
        renderContext.setEmbeddedResourceRenderer(new DefaultEmbeddedResourceRenderer(makeRendererAttachmentManager(config)));
        renderContext.setCharacterEncoding(CHARACTER_ENCODING);
        renderContext.setRenderingForWysiwyg(false);
        return renderContext;
    }

    private RendererAttachmentManager makeRendererAttachmentManager(PortalConfig config) {
        return new RendererAttachmentManager() {
            private final String DOWNLOAD_PATH = config.data().getCommonConfig().getCrmUrlFiles() + "springApi/files/";
            @Override
            public RendererAttachment getAttachment(RenderContext renderContext, EmbeddedResource embeddedResource) {
                Result<Attachment> result = attachmentService.getAttachmentByExtLink(embeddedResource.getFilename());
                if (result.isOk()) {
                    Attachment attachment = result.getData();
                    return new RendererAttachment(
                            attachment.getId(),
                            attachment.getFileName(),
                            attachment.getMimeType(),
                            attachment.getCreatorId() != null ? attachment.getCreatorId().toString() : null,
                            attachment.getLabelText(),
                            DOWNLOAD_PATH + attachment.getExtLink(),
                            null,
                            null,
                            attachment.getCreated() != null ? new Timestamp(attachment.getCreated().getTime()) : null
                    );
                }
                return null;
            }
            @Override
            public RendererAttachment getThumbnail(RendererAttachment rendererAttachment, RenderContext renderContext, EmbeddedImage embeddedImage) { return null; }
            @Override
            public boolean systemSupportsThumbnailing() { return false; }
        };
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

    static class JiraIconManager implements IconManager {
        private boolean renderIcons;

        public boolean isRenderIcons() {
            return renderIcons;
        }

        public void setRenderIcons(boolean renderIcons) {
            this.renderIcons = renderIcons;
        }

        @Override
        public Icon getLinkDecoration(String s) {
            int size = isRenderIcons() ? 10 : 0;
            switch (s) {
                case "mailto": return Icon.makeRenderIcon("mail_small.png", 1, size, size);
                case "external": return Icon.makeRenderIcon("link_small.png", 1, size, size);
            }
            return null;
        }
        /* Following methods dont matter without EmoticonRendererComponent */
        @Override
        public Icon getEmoticon(String s) { return null; }
        @Override
        public String[] getEmoticonSymbols() { return new String[0]; }
    }

    private RendererConfiguration makeRendererConfiguration(PortalConfig config) {
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
    private AttachmentService attachmentService;

    private final V2RendererFacade rendererFacade;
    private V2SubRenderer subRenderer;
    private final JiraIconManager iconManager;
    private final RenderContext renderContext;

    private static final String CHARACTER_ENCODING = StandardCharsets.UTF_8.name();
}
