package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.struct.TextWithMarkup;

import java.util.List;

public interface TextRenderControllerAsync {

    void render(String text, En_TextMarkup textMarkup, AsyncCallback<String> async);

    void render(List<TextWithMarkup> elements, AsyncCallback<List<String>> async);
}
