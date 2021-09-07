package ru.protei.portal.ui.common.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.dict.En_ReportYoutrackWorkType;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.YoutrackReportDictionary;
import ru.protei.portal.core.service.YoutrackReportDictionaryService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.YoutrackReportDictionaryController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;

@Service("YoutrackReportDictionaryController")
public class YoutrackReportDictionaryControllerImpl implements YoutrackReportDictionaryController {

    @Override
    public List<YoutrackReportDictionary> getDictionaries(En_ReportYoutrackWorkType type) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(youtrackReportDictionaryService.getDictionaries(authToken, type));
    }

    @Override
    public YoutrackReportDictionary createDictionary(YoutrackReportDictionary dictionary) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(youtrackReportDictionaryService.createDictionary(authToken, dictionary));
    }

    @Override
    public YoutrackReportDictionary updateDictionary(YoutrackReportDictionary dictionary) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(youtrackReportDictionaryService.updateDictionary(authToken, dictionary));
    }

    @Override
    public YoutrackReportDictionary removeDictionary(YoutrackReportDictionary dictionary) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(youtrackReportDictionaryService.removeDictionary(authToken, dictionary));
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    YoutrackReportDictionaryService youtrackReportDictionaryService;

    private static final Logger log = LoggerFactory.getLogger(YoutrackReportDictionaryControllerImpl.class);
}
