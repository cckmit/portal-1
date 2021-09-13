package ru.protei.portal.ui.common.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.dict.En_YoutrackWorkType;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.YoutrackWorkDictionary;
import ru.protei.portal.core.service.YoutrackWorkDictionaryService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.YoutrackWorkDictionaryController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;

@Service("YoutrackWorkDictionaryController")
public class YoutrackWorkDictionaryControllerImpl implements YoutrackWorkDictionaryController {

    @Override
    public List<YoutrackWorkDictionary> getDictionaries(En_YoutrackWorkType type) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(youtrackWorkDictionaryService.getDictionaries(authToken, type));
    }

    @Override
    public YoutrackWorkDictionary createDictionary(YoutrackWorkDictionary dictionary) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(youtrackWorkDictionaryService.createDictionary(authToken, dictionary));
    }

    @Override
    public YoutrackWorkDictionary updateDictionary(YoutrackWorkDictionary dictionary) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(youtrackWorkDictionaryService.updateDictionary(authToken, dictionary));
    }

    @Override
    public YoutrackWorkDictionary removeDictionary(YoutrackWorkDictionary dictionary) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(youtrackWorkDictionaryService.removeDictionary(authToken, dictionary));
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    YoutrackWorkDictionaryService youtrackWorkDictionaryService;

    private static final Logger log = LoggerFactory.getLogger(YoutrackWorkDictionaryControllerImpl.class);
}
