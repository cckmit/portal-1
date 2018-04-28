package ru.protei.portal.ui.contact.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.service.PersonService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Реализация сервиса по работе с person
 */
@Service( "PersonService" )
public class PersonServiceImpl implements PersonService {

    public List<PersonShortView> getPersonViewList( PersonQuery query ) throws RequestFailedException {

        log.debug( "getPersonViewList(): searchPattern={} | companyId={} | sortField={} | sortDir={}",
                query.getSearchString(), query.getSortField(), query.getSortDir() );

        CoreResponse< List< PersonShortView > > result = personService.shortViewList( query );

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

    @Override
    public Person getPerson(Long id) throws RequestFailedException {
        log.debug("getPerson: id={}", id);

        CoreResponse<Person> result = personService.getPerson(id);

        log.debug("result status: {}", result.getStatus());

        if (result.isError()) {
            throw new RequestFailedException(result.getStatus());
        }

        return result.getData();
    }

    @Autowired
    ru.protei.portal.core.service.PersonService personService;

    private static final Logger log = LoggerFactory.getLogger("web");
}
