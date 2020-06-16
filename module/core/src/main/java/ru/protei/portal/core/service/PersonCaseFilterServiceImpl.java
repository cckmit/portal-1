package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.PersonCaseFilterEvent;
import ru.protei.portal.core.model.dao.CaseFilterDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.List;

public class PersonCaseFilterServiceImpl implements PersonCaseFilterService {
    @Autowired
    PersonDAO personDAO;
    @Autowired
    CaseFilterDAO caseFilterDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    EventPublisherService publisherService;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Override
    public Result<Void> processMailNotification() {
        PersonQuery personWithCaseFilters = new PersonQuery();
        personWithCaseFilters.setHasCaseFilter(true);

        List<Person> persons = personDAO.getPersons(personWithCaseFilters);
        for (Person person : persons) {
            List<CaseFilter> personToCaseFilter = caseFilterDAO.getByPersonId(person.getId());
            for (CaseFilter caseFilter : personToCaseFilter) {
                SearchResult<CaseObject> result = caseObjectDAO.getSearchResult(caseFilter.getParams());
                publisherService.publishEvent(new PersonCaseFilterEvent(this, result.getResults(), person));
            }
        }

        return Result.ok();
    }
}
