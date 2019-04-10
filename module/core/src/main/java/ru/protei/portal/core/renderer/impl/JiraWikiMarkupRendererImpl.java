package ru.protei.portal.core.renderer.impl;

import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.renderer.JiraWikiMarkupRenderer;

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
        // TODO bukh implement markup convert
        return text;
    }
}
