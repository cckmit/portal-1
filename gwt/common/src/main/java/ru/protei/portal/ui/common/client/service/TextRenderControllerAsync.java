package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_TextMarkup;

import java.util.List;

public interface TextRenderControllerAsync {

    void render(String text, En_TextMarkup textMarkup, AsyncCallback<String> async);

    void render(En_TextMarkup textMarkup, List<String> textList, AsyncCallback<List<String>> async);

    void render(String text, En_TextMarkup textMarkup, boolean needReplace, AsyncCallback<String> async);
}
