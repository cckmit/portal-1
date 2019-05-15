package ru.protei.portal.core.renderer;

/**
 * Markdown (commonmark) renderer
 */
public interface MarkdownRenderer {

    String plain2html(String text);
}
