package ru.protei.portal.ui.common.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.service.PersonCaseFilterService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.PersonCaseFilterController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service( "PersonCaseFilterController" )
public class PersonCaseFilterControllerImpl implements PersonCaseFilterController {
    @Override
    public List<CaseFilterShortView> getCaseFilterByPersonId(Long personId) throws RequestFailedException {

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        log.info( "getCaseFilterByPersonId(): personId={}", personId);

        Result< List< CaseFilterShortView > > response = personCaseFilterService.getCaseFilterByPersonId( token, personId );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }
        return response.getData();
    }

    @Override
    public void changePersonToCaseFilter(Long personId, Long oldCaseFilterId, Long newCaseFilterId) throws RequestFailedException {
        log.info("changePersonToCaseFilter, personId: {}, oldCaseFilterId: {}, newCaseFilterId: {}", personId, oldCaseFilterId, newCaseFilterId);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Void> response = personCaseFilterService.changePersonToCaseFilter(token, personId, oldCaseFilterId, newCaseFilterId);

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
    }

    @Autowired
    PersonCaseFilterService personCaseFilterService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    SessionService sessionService;

    private static final Logger log = LoggerFactory.getLogger(PersonCaseFilterControllerImpl.class);
}
