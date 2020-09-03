package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/TextRenderController")
public interface TextRenderController extends RemoteService {

    String render(String text, En_TextMarkup textMarkup) throws RequestFailedException;

    String render(String text, En_TextMarkup textMarkup, boolean needReplace) throws RequestFailedException;

    List<String> render(En_TextMarkup textMarkup, List<String> textList) throws RequestFailedException;

    List<String> render(En_TextMarkup textMarkup, List<String> textList, boolean needReplaceLogin) throws RequestFailedException;
}
