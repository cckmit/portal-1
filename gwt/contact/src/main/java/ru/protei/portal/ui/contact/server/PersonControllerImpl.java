package ru.protei.portal.ui.contact.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.PersonService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.portal.ui.common.client.service.PersonController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

/**
 * Реализация сервиса по работе с person
 */
@Service( "PersonController" )
public class PersonControllerImpl implements PersonController {

    @Override
    public List<PersonShortView> getPersonViewList( PersonQuery query ) throws RequestFailedException {

        log.info( "getPersonViewList(): searchPattern={} | companyId={} | sortField={} | sortDir={}",
                query.getSearchString(), query.getSortField(), query.getSortDir() );

        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result< List< PersonShortView > > result = personService.shortViewList( authToken, query );

        log.info( "result status: {}, data-amount: {}", result.getStatus(), size(result.getData()) );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public Map<Long, String> getPersonNames(Collection<Long> ids) throws RequestFailedException {
        log.info( "getPersonName: ids={}", ids );

        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        Result<Map<Long, String>> result = personService.getPersonNames(authToken, ids);

        log.info( "result status: {}, data-amount: {}", result.getStatus(), size(result.getData()) );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public List<Person> getPersonsByIds(Collection<Long> ids) throws RequestFailedException {
        log.info( "getPersonsByIds: ids={}", ids );
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(personService.getPersonsByIds(authToken, ids));
    }

    @Override
    public PersonShortView getPersonShortView(Long id) throws RequestFailedException {
        log.info("getPersonShortView: id={}", id);

        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<PersonShortView> result = personService.getPersonShortView(authToken, id);

        log.info("result status: {}, data: {}", result.getStatus(), result.getData());

        if (result.isError())
            throw new RequestFailedException(result.getStatus());

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
