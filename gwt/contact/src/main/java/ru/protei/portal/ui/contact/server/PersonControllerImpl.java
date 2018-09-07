package ru.protei.portal.ui.contact.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.PersonService;
import ru.protei.portal.core.service.PolicyService;
import ru.protei.portal.ui.common.client.service.PersonController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Реализация сервиса по работе с person
 */
@Service( "PersonController" )
public class PersonControllerImpl implements PersonController {

    public List<PersonShortView> getPersonViewList( PersonQuery query ) throws RequestFailedException {

        log.debug( "getPersonViewList(): searchPattern={} | companyId={} | sortField={} | sortDir={}",
                query.getSearchString(), query.getSortField(), query.getSortDir() );

        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        CoreResponse< List< PersonShortView > > result = personService.shortViewList( authToken, query );

        log.debug( "result status: {}, data-amount: {}", result.getStatus(), result.isOk() ? result.getDataAmountTotal() : 0 );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public Map<Long, String> getPersonNames(Collection<Long> ids) throws RequestFailedException {
        log.debug( "getPersonName: ids={}", ids );


        CoreResponse<Map<Long, String>> result = personService.getPersonNames(ids);

        log.debug( "result status: {}, data-amount: {}", result.getStatus(), result.isOk() ? result.getDataAmountTotal() : 0 );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Autowired
    private PersonService personService;
    @Autowired
    SessionService sessionService;

    @Autowired
    PolicyService policyService;

    @Autowired
    HttpServletRequest httpServletRequest;

    private static final Logger log = LoggerFactory.getLogger(PersonControllerImpl.class);
}
