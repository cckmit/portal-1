package ru.protei.portal.core.renderer;

/**
 * Wiki markup (JIRA) converter
 */
public interface JiraWikiMarkupRenderer {

    String plain2html(String text);

    String plain2html(String text, boolean renderIcons);
}
