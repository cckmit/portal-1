package ru.protei.portal.ui.common.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.renderer.HTMLRenderer;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.service.AccountService;
import ru.protei.portal.core.service.CaseCommentService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.TextRenderController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;

@Service("TextRenderController")
public class TextRenderControllerImpl implements TextRenderController {

    @Override
    public String render(String text, En_TextMarkup textMarkup) throws RequestFailedException {
        return render(text, textMarkup, false);
    }

    @Override
    public String render(String text, En_TextMarkup textMarkup, boolean needReplaceLoginWithUsername) throws RequestFailedException {
        return CollectionUtils.getFirst(render(textMarkup, Collections.singletonList(text), needReplaceLoginWithUsername));
    }

    @Override
    public List<String> render(En_TextMarkup textMarkup, List<String> textList) throws RequestFailedException {
        return render(textMarkup, textList, false);
    }

    @Override
    public List<String> render(En_TextMarkup textMarkup, List<String> textList, boolean needReplaceLoginWithUsername) throws RequestFailedException {
        if (needReplaceLoginWithUsername) {
            AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
            textList = ServiceUtils.checkResultAndGetData(caseCommentService.replaceLoginWithUsername(token, textList));
        }

        List<String> rendered = new ArrayList<>();
        for (String text : CollectionUtils.emptyIfNull(textList)) {
            rendered.add(htmlRenderer.plain2html(text, textMarkup));
        }
        return rendered;
    }

    @Autowired
    HTMLRenderer htmlRenderer;

    @Autowired
    CaseCommentService caseCommentService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;
}
