package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис управления person
 */
public class PersonServiceImpl implements PersonService {

    @Autowired
    PersonDAO personDAO;

    @Override
    public CoreResponse< List< PersonShortView > > shortViewList() {
        List<Person> list = personDAO.getPersonsAll();

        if ( list == null )
            new CoreResponse< List< PersonShortView > >().error( En_ResultStatus.GET_DATA_ERROR );

        List< PersonShortView > result = list.stream().map( Person::toPersonView ).collect( Collectors.toList() );

        return new CoreResponse< List< PersonShortView > >().success( result,result.size() );
    }
}
