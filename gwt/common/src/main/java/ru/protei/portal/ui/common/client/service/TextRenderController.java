package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.struct.TextWithMarkup;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/TextRenderController")
public interface TextRenderController extends RemoteService {

    String render(String text, En_TextMarkup textMarkup) throws RequestFailedException;

    List<String> render(List<TextWithMarkup> elements) throws RequestFailedException;
}
