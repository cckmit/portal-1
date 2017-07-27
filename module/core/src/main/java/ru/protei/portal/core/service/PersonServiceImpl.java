package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис управления person
 */
public class PersonServiceImpl implements PersonService {

    @Autowired
    PersonDAO personDAO;

    @Override
    public CoreResponse< List< PersonShortView > > shortViewList( PersonQuery query ) {
        List<Person> list = personDAO.getPersons( query );

        if ( list == null )
            return new CoreResponse< List< PersonShortView > >().error( En_ResultStatus.GET_DATA_ERROR );

        List< PersonShortView > result = list.stream().map( Person::toFullNameShortView ).collect( Collectors.toList() );

        return new CoreResponse< List< PersonShortView > >().success( result,result.size() );
    }

    @Override
    public CoreResponse<Map<Long, String>> getPersonNames(Collection<Long> ids) {
        Collection<Person> list = personDAO.partialGetListByKeys(ids, "id", "displayname");

        if ( list == null )
            return new CoreResponse().error( En_ResultStatus.GET_DATA_ERROR );

        Map<Long, String> names = new HashMap<>(list.size());
        list.forEach(a -> names.put(a.getId(), a.getDisplayName()));
        return new CoreResponse<Map<Long, String>>().success( names );
    }
}
