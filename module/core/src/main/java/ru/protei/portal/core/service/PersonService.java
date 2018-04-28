package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Сервис управления person
 */
public interface PersonService {
    CoreResponse< List< PersonShortView > > shortViewList( PersonQuery query );
    CoreResponse<Map<Long, String>> getPersonNames(Collection<Long> ids);
    CoreResponse<Person> getPerson(Long id);
}
