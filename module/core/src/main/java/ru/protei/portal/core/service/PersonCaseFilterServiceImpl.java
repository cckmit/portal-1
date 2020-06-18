package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.PersonCaseFilterEvent;
import ru.protei.portal.core.model.dao.CaseFilterDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.PersonCaseFilterDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class PersonCaseFilterServiceImpl implements PersonCaseFilterService {
    @Autowired
    PersonDAO personDAO;
    @Autowired
    CaseFilterDAO caseFilterDAO;
    @Autowired
    PersonCaseFilterDAO personCaseFilterDAO;
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

        return ok();
    }

    @Override
    public Result<List<CaseFilterShortView>> getCaseFilterByPersonId(AuthToken authToken, Long personId) {
        if (personId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        List<CaseFilter> list = caseFilterDAO.getByPersonId(personId);
        List< CaseFilterShortView > result = list.stream().map( CaseFilter::toShortView ).collect( Collectors.toList() );

        return ok(result);
    }

    @Override
    public Result<Void> changePersonToCaseFilter(AuthToken authToken, Long personId, Long oldCaseFilterId, Long newCaseFilterID ) {
        if (personId == null || (oldCaseFilterId == null && newCaseFilterID == null)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (oldCaseFilterId == null && newCaseFilterID != null) {
            if (!personCaseFilterDAO.checkExistsByCondition("person_id = ? and case_filter_id = ?",
                    personId, newCaseFilterID)) {
                personCaseFilterDAO.persist(createPersonToCaseFilter(personId, newCaseFilterID));
            }
        } else if (oldCaseFilterId != null && newCaseFilterID == null) {
            personCaseFilterDAO.removeByCondition("person_id = ? and case_filter_id = ?", personId, oldCaseFilterId);
        } else {
            PersonToCaseFilter personToCaseFilter = personCaseFilterDAO
                    .getByCondition("person_id = ? and case_filter_id = ?", personId, oldCaseFilterId);
            if (personToCaseFilter == null) {
                personToCaseFilter = createPersonToCaseFilter(personId, newCaseFilterID);
            } else {
                personToCaseFilter.setCaseFilterId(newCaseFilterID);
            }
            personCaseFilterDAO.merge(personToCaseFilter);
        }

        return ok();
    }

    private PersonToCaseFilter createPersonToCaseFilter(Long personId, Long caseFilterId) {
        PersonToCaseFilter personToCaseFilter = new PersonToCaseFilter();
        personToCaseFilter.setPersonId(personId);
        personToCaseFilter.setCaseFilterId(caseFilterId);
        return personToCaseFilter;
    }
}
