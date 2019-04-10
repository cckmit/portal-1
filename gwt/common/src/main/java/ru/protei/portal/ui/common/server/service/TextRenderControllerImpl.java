package ru.protei.portal.ui.common.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.renderer.HTMLRenderer;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.TextWithMarkup;
import ru.protei.portal.ui.common.client.service.TextRenderController;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.ArrayList;
import java.util.List;

@Service("TextRenderController")
public class TextRenderControllerImpl implements TextRenderController {

    @Override
    public String render(String text, En_TextMarkup textMarkup) throws RequestFailedException {
        return htmlRenderer.plain2html(text, textMarkup);
    }

    @Override
    public List<String> render(List<TextWithMarkup> elements) throws RequestFailedException {
        List<String> rendered = new ArrayList<>();
        for (TextWithMarkup element : CollectionUtils.emptyIfNull(elements)) {
            rendered.add(htmlRenderer.plain2html(element.getText(), element.getTextMarkup()));
        }
        return rendered;
    }

    @Autowired
    HTMLRenderer htmlRenderer;
}
