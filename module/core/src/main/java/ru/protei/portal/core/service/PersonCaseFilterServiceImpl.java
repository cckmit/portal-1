package ru.protei.portal.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.PersonCaseFilterEvent;
import ru.protei.portal.core.model.dao.CaseFilterDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.PersonCaseFilterDAO;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;

public class PersonCaseFilterServiceImpl implements PersonCaseFilterService {
    private static Logger log = LoggerFactory.getLogger( PersonCaseFilterServiceImpl.class );

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
    @Autowired
    ObjectMapper objectMapper;

    @Async(BACKGROUND_TASKS)
    @Override
    public Result<Void> processMailNotification() {
        PersonQuery personWithCaseFilters = new PersonQuery();
        personWithCaseFilters.setHasCaseFilter(true);

        List<Person> persons = personDAO.getPersons(personWithCaseFilters);
        jdbcManyRelationsHelper.fill(persons, Person.Fields.CONTACT_ITEMS);
        for (Person person : persons) {
            List<CaseFilter> personToCaseFilter = caseFilterDAO.getByPersonIdAndTypes(person.getId(), En_CaseFilterType.getTypesByClass(CaseQuery.class));
            for (CaseFilter caseFilter : personToCaseFilter) {
                CaseQuery caseQuery;
                try {
                    caseQuery = objectMapper.readValue(caseFilter.getParams(), CaseQuery.class);
                } catch (IOException e) {
                    log.warn("processMailNotification: cannot read filter params. caseFilter={}", caseFilter);
                    e.printStackTrace();
                    continue;
                }
                SearchResult<CaseObject> result = caseObjectDAO.getSearchResult(caseQuery);
                publisherService.publishEvent(new PersonCaseFilterEvent(this, result.getResults(), person));
            }
        }

        return ok();
    }

    @Override
    public Result<List<FilterShortView>> getCaseFilterByPersonId(AuthToken authToken, Long personId) {
        if (personId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        List<CaseFilter> list = caseFilterDAO.getByPersonId(personId);
        List<FilterShortView> result = list.stream().map(CaseFilter::toShortView).collect(Collectors.toList());

        return ok(result);
    }

    @Override
    @Transactional
    public Result<Boolean> addPersonToCaseFilter(AuthToken authToken, Long personId, Long caseFilterId) {
        if (personId == null || caseFilterId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (!personCaseFilterDAO.isExist(personId, caseFilterId)) {
            personCaseFilterDAO.persist(createPersonToCaseFilter(personId, caseFilterId));
        }

        return ok(true);
    }

    @Override
    @Transactional
    public Result<Long> removePersonToCaseFilter(AuthToken authToken, Long personId, Long caseFilterId) {
        if (personId == null || caseFilterId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!personCaseFilterDAO.removeByPersonIdAndCaseFilterId(personId, caseFilterId)) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        return ok(caseFilterId);
    }

    @Override
    @Transactional
    public Result<Boolean> changePersonToCaseFilter(AuthToken authToken, Long personId, Long oldCaseFilterId, Long newCaseFilterID ) {
        if (personId == null || (oldCaseFilterId == null && newCaseFilterID == null)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        PersonToCaseFilter personToCaseFilter = personCaseFilterDAO.getByPersonIdAndCaseFilterId(personId, oldCaseFilterId);
        if (personToCaseFilter == null) {
            personToCaseFilter = createPersonToCaseFilter(personId, newCaseFilterID);
        } else {
            personToCaseFilter.setCaseFilterId(newCaseFilterID);
        }

        personCaseFilterDAO.merge(personToCaseFilter);

        return ok(true);
    }

    private PersonToCaseFilter createPersonToCaseFilter(Long personId, Long caseFilterId) {
        PersonToCaseFilter personToCaseFilter = new PersonToCaseFilter();
        personToCaseFilter.setPersonId(personId);
        personToCaseFilter.setCaseFilterId(caseFilterId);
        return personToCaseFilter;
    }
}
