package ru.protei.portal.app.portal.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.app.portal.client.service.PersonSubscriptionController;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.struct.PersonSubscriptionChangeRequest;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.PersonSubscriptionService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;
import static ru.protei.portal.ui.common.server.ServiceUtils.getAuthToken;

@Service( "PersonSubscriptionController" )
public class PersonSubscriptionControllerImpl implements PersonSubscriptionController {

    @Override
    public Set<PersonShortView> getPersonSubscriptions() throws RequestFailedException {
        log.info("getPersonSubscriptions()");
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result<Set<PersonShortView>> result = personSubscriptionService.getPersonSubscriptions(token);
        log.info("getPersonSubscriptions(): result={}", result.isOk() ? "ok" : result.getStatus());
        return checkResultAndGetData(result);
    }

    @Override
    public Set<PersonShortView> updatePersonSubscriptions(PersonSubscriptionChangeRequest changeRequest) throws RequestFailedException {
        log.info("updatePersonSubscriptions(): changeRequest={}", changeRequest);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        Result<Set<PersonShortView>> result = personSubscriptionService.updatePersonSubscriptions(token, changeRequest);
        log.info("updatePersonSubscriptions(): result={}", result.isOk() ? "ok" : result.getStatus());
        return checkResultAndGetData(result);
    }

    @Autowired
    PersonSubscriptionService personSubscriptionService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger(PersonSubscriptionControllerImpl.class);
}
