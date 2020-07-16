package ru.protei.portal.app.portal.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.app.portal.client.service.PersonSubscriptionController;
import ru.protei.portal.core.model.ent.AuthToken;
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
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(personSubscriptionService.getPersonSubscriptions(token));
    }

    @Override
    public Set<PersonShortView> updatePersonSubscriptions(Set<PersonShortView> persons) throws RequestFailedException {
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(personSubscriptionService.updatePersonSubscriptions(token, persons));
    }

    @Autowired
    PersonSubscriptionService personSubscriptionService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpServletRequest;
}
