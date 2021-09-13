package ru.protei.portal.ui.common.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.YoutrackProject;
import ru.protei.portal.core.service.YoutrackService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.YoutrackController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;

@Service("YoutrackController")
public class YoutrackControllerImpl implements YoutrackController {

    @Override
    public List<YoutrackProject> getProjects(int offset, int limit) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(youtrackService.getProjects(authToken, offset, limit));
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    YoutrackService youtrackService;

    private static final Logger log = LoggerFactory.getLogger(YoutrackControllerImpl.class);
}
