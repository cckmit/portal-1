package ru.protei.portal.core.renderer;

import ru.protei.portal.core.model.dict.En_TextMarkup;

public interface HTMLRenderer {

    String plain2html(String text, En_TextMarkup textMarkup);

    String plain2html(String text, En_TextMarkup textMarkup, boolean renderIcons);
}
