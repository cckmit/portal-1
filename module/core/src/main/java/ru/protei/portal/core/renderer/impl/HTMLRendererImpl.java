package ru.protei.portal.core.renderer.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.renderer.JiraWikiMarkupRenderer;
import ru.protei.portal.core.renderer.MarkdownRenderer;
import ru.protei.portal.core.renderer.HTMLRenderer;
import ru.protei.portal.core.model.dict.En_TextMarkup;

public class HTMLRendererImpl implements HTMLRenderer {

    @Override
    public String plain2html(String text, En_TextMarkup textMarkup) {
        if (textMarkup == null) {
            return text;
        }
        switch (textMarkup) {
            case MARKDOWN: return markdownRenderer.plain2html(text);
            case JIRA_WIKI_MARKUP: return jiraWikiMarkupRenderer.plain2html(text);
            default: return text;
        }
    }

    @Autowired
    MarkdownRenderer markdownRenderer;
    @Autowired
    JiraWikiMarkupRenderer jiraWikiMarkupRenderer;
}
