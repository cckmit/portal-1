package ru.protei.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.protei.portal.core.renderer.HTMLRenderer;
import ru.protei.portal.core.renderer.JiraWikiMarkupRenderer;
import ru.protei.portal.core.renderer.MarkdownRenderer;
import ru.protei.portal.core.renderer.impl.HTMLRendererImpl;
import ru.protei.portal.core.renderer.impl.JiraWikiMarkupRendererImpl;
import ru.protei.portal.core.renderer.impl.MarkdownRendererImpl;
import ru.protei.portal.core.service.AttachmentService;

import static org.mockito.Mockito.mock;

@Configuration
public class RendererTestConfiguration {
    @Bean
    public HTMLRenderer getHTMLRenderer() {
        return new HTMLRendererImpl();
    }

    @Bean
    public MarkdownRenderer getMarkdownRenderer() {
        return new MarkdownRendererImpl();
    }

    @Bean
    public JiraWikiMarkupRenderer getJiraWikiMarkupRenderer(PortalConfig config) {
        return new JiraWikiMarkupRendererImpl(config);
    }

    @Bean
    public AttachmentService getAttachmentService() {
        return  mock(AttachmentService.class);
    }
}
