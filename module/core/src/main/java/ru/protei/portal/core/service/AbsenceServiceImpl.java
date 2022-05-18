package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.AbsenceNotificationEvent;
import ru.protei.portal.core.event.EventAction;
import ru.protei.portal.core.model.api.ApiAbsence;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.AbsenceUtils;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.AbsenceApiQuery;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.core.utils.DateUtils;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

public class AbsenceServiceImpl implements AbsenceService {

    @Autowired
    PersonAbsenceDAO personAbsenceDAO;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    PersonNotifierDAO personNotifierDAO;
    @Autowired
    WorkerEntryDAO workerEntryDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    ReportControlService reportControlService;
    @Autowired
    CompanyGroupHomeDAO companyGroupHomeDAO;

    private static Logger log = LoggerFactory.getLogger(AbsenceServiceImpl.class);

    @Override
    public Result<SearchResult<PersonAbsence>> getAbsences(AuthToken token, AbsenceQuery query) {
        return ok(personAbsenceDAO.getSearchResultByQuery(query));
    }

    @Override
    public Result<PersonAbsence> getAbsence(AuthToken token, Long id) {
        PersonAbsence absence = personAbsenceDAO.get(id);

        if (absence == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        return ok(absence);
    }

    @Override
    @Transactional
    public Result<Long> createAbsenceFromPortal(AuthToken token, PersonAbsence absence) {
        absence.setCreatedFrom1C(false);
        return createAbsence( token, absence);
    }

    private Result<Long> createAbsence(AuthToken token, PersonAbsence absence) {
        if (!validateFields(absence)) {
            return error( En_ResultStatus.INCORRECT_PARAMS);
        }

        if (hasAbsenceIntersections(absence.getPersonId(), absence.getFromTime(), absence.getTillTime(), absence.getId())) {
            return error(En_ResultStatus.ABSENCE_HAS_INTERSECTIONS);
        }

        absence.setCreated(new Date());
        absence.setCreatorId(token.getPersonId());

        Long absenceId = personAbsenceDAO.persist(absence);

        if (absenceId == null) {
            return error(En_ResultStatus.NOT_CREATED);
        }

        Person initiator = personDAO.get(token.getPersonId());
        jdbcManyRelationsHelper.fill(initiator, Person.Fields.CONTACT_ITEMS);
        PersonAbsence newState = personAbsenceDAO.get(absenceId);

        return ok(absenceId)
                .publishEvent(new AbsenceNotificationEvent(
                        this,
                        EventAction.CREATED,
                        initiator,
                        null,
                        newState,
                        getAbsenceNotifiers(newState)));
    }

    @Override
    @Transactional
    public Result<Long> updateAbsence(AuthToken token, PersonAbsence absence) {

        if (!validateFields(absence) || absence.getId() == null) {
            return error( En_ResultStatus.INCORRECT_PARAMS);
        }

        PersonAbsence oldState = personAbsenceDAO.get(absence.getId());
        if (oldState == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (hasAbsenceIntersections(absence.getPersonId(), absence.getFromTime(), absence.getTillTime(), absence.getId())) {
            return error(En_ResultStatus.ABSENCE_HAS_INTERSECTIONS);
        }

        if (!personAbsenceDAO.partialMerge(absence, "from_time", "till_time", "user_comment", "schedule")) {
            return error(En_ResultStatus.NOT_UPDATED);
        }

        Person initiator = personDAO.get(token.getPersonId());
        jdbcManyRelationsHelper.fill(initiator, Person.Fields.CONTACT_ITEMS);
        PersonAbsence newState = personAbsenceDAO.get(absence.getId());

        return ok(absence.getId())
                .publishEvent(new AbsenceNotificationEvent(
                        this,
                        EventAction.UPDATED,
                        initiator,
                        oldState,
                        newState,
                        getAbsenceNotifiers(newState)));
    }

    @Override
    @Transactional
    public Result<Long> removeAbsence(AuthToken token, PersonAbsence absence) {

        if (absence == null || absence.getId() == null) {
            return error( En_ResultStatus.INCORRECT_PARAMS);
        }

        PersonAbsence storedAbsence = personAbsenceDAO.get(absence.getId());
        if (storedAbsence == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (!personAbsenceDAO.removeByKey(storedAbsence.getId())) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        Person initiator = personDAO.get(token.getPersonId());
        jdbcManyRelationsHelper.fill(initiator, Person.Fields.CONTACT_ITEMS);

        return ok(absence.getId()).publishEvent(new AbsenceNotificationEvent(
                this,
                EventAction.REMOVED,
                initiator,
                null,
                storedAbsence,
                getAbsenceNotifiers(storedAbsence)));
    }

    @Override
    @Transactional
    public Result<Boolean> completeAbsence(AuthToken token, PersonAbsence absence) {

        if (absence == null || absence.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        PersonAbsence oldState = personAbsenceDAO.get(absence.getId());
        if (oldState == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (!isCurrentAbsence(oldState)) {
            return error(En_ResultStatus.NOT_CURRENT_ABSENCE);
        }

        PersonAbsence newState = new PersonAbsence(oldState);
        newState.setTillTime(DateUtils.resetSeconds(new Date()));

        if (!personAbsenceDAO.partialMerge(newState, "till_time")) {
            return error(En_ResultStatus.NOT_UPDATED);
        }

        Person initiator = personDAO.get(token.getPersonId());
        jdbcManyRelationsHelper.fill(initiator, Person.Fields.CONTACT_ITEMS);

        return ok(true)
                .publishEvent(new AbsenceNotificationEvent(
                        this,
                        EventAction.UPDATED,
                        initiator,
                        oldState,
                        newState,
                        getAbsenceNotifiers(newState)));
    }

    @Override
    public Result<Void> createReport(AuthToken token, String name, AbsenceQuery query) {

        if (query == null || query.getDateRange() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Person initiator = personDAO.get(token.getPersonId());
        jdbcManyRelationsHelper.fill(initiator, Person.Fields.CONTACT_ITEMS);

        reportControlService.processAbsenceReport(initiator, name, query);

        return ok();
    }

    /**
     * Получение списка отсутствий по запросу от 1С.
     * @param authToken токен авторизации
     * @param apiQuery  фильтр по отсутствиям
     * @return Список отсутствий, с предобработкой расписаний (после реализации PORTAL-1312).
     * Так как метод используется для 1С – обогощаем список отсутствий внешними идентификаторами сотрудников в 1С (worker_entry#worker_extId).
     */
    @Override
    public Result<List<ApiAbsence>> getAbsencesByApiQuery(AuthToken authToken, AbsenceApiQuery apiQuery) {
        if (apiQuery == null || !apiQuery.isValid()) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        CompanyHomeGroupItem groupCompany = companyGroupHomeDAO.getByExternalCode(apiQuery.getCompanyCode().trim());
        if (groupCompany == null || groupCompany.getCompanyId() == null) {
            return Result.error(En_ResultStatus.INCORRECT_PARAMS);
        }
        Long companyId = groupCompany.getCompanyId();

        AbsenceQuery query = new AbsenceQuery();
        query.setDateRange(new DateRange(En_DateIntervalType.FIXED, apiQuery.getFrom(), apiQuery.getTo()));
        query.setReasons(apiQuery.getReasons());

        boolean searchBySeparateWorkers = CollectionUtils.isNotEmpty(apiQuery.getWorkerExtIds());
        List<WorkerEntry> workerEntries = null;
        if (searchBySeparateWorkers) {
            workerEntries = workerEntryDAO.partialGetByExternalIds(apiQuery.getWorkerExtIds(), companyId);
            Set<Long> personIds = CollectionUtils.emptyIfNull(workerEntries).stream()
                    .map(WorkerEntry::getPersonId)
                    .collect(Collectors.toSet());
            query.setEmployeeIds(personIds);
        }

        SearchResult<PersonAbsence> searchResult = personAbsenceDAO.getSearchResultByQuery(query);
        List<PersonAbsence> absences = searchResult.getResults();
        if (absences == null) return Result.ok();

        List<PersonAbsence> collectedAbsences = AbsenceUtils.generateAbsencesFromDateRange(absences, apiQuery.getFrom(), apiQuery.getTo());

        if (!searchBySeparateWorkers) {
            List<Long> personIds = collectedAbsences.stream()
                    .map(PersonAbsence::getPersonId)
                    .collect(Collectors.toList());

            workerEntries = workerEntryDAO.partialGetByPersonIds(personIds, companyId);
        }

        // сотрудник может работать по совместительству на разных должностях в _одной_ компании
        // поэтому отсутствия для 1С формируем на основе данных worker_extId
        Map<Long, Set<String>> personIdToExtIdMap = CollectionUtils.emptyIfNull(workerEntries).stream()
                    .collect(Collectors.groupingBy(
                            WorkerEntry::getPersonId, Collectors.mapping(WorkerEntry::getExternalId, Collectors.toSet())));

        List<ApiAbsence> apiAbsences = new ArrayList<>();
        for (PersonAbsence absence : collectedAbsences) {
            Set<String> personExtIds = personIdToExtIdMap.get(absence.getPersonId());
            if (CollectionUtils.isNotEmpty(personExtIds)) {
                for (String extId : personExtIds) {
                    apiAbsences.add(new ApiAbsence(absence).withWorkerId(extId));
                }
            }
        }

        return Result.ok(apiAbsences);
    }

    @Override
    public Result<Long> createAbsenceByApi(AuthToken authToken, ApiAbsence apiAbsence) {
        return apiAbsenceCrudAction(authToken, apiAbsence,
                absence -> absence == null || !absence.isValid(),
                (token, absence) -> {
                    absence.setCreatedFrom1C(true);
                    return createAbsence(authToken, absence);
                });
    }

    @Override
    public Result<Long> updateAbsenceByApi(AuthToken authToken, ApiAbsence apiAbsence) {
        return apiAbsenceCrudAction(authToken, apiAbsence,
                absence -> absence == null || absence.getId() == null || !absence.isValid(),
                (token, absence) -> updateAbsence(authToken, absence));
    }

    @Override
    public Result<Long> removeAbsenceByApi(AuthToken authToken, ApiAbsence apiAbsence) {
        return apiAbsenceCrudAction(authToken, apiAbsence,
                absence -> absence == null || absence.getId() == null
                        || ((StringUtils.isEmpty(absence.getCompanyCode()) || absence.getWorkerExtId() == null) && absence.getPersonId() == null),
                (token, absence) -> removeAbsence(authToken, absence));
    }

    private Result<Long> apiAbsenceCrudAction(AuthToken token, ApiAbsence apiAbsence, Function<ApiAbsence, Boolean> incorrectParamCheck,
                                              BiFunction<AuthToken, PersonAbsence, Result<Long>> crudAction) {
        if (incorrectParamCheck.apply(apiAbsence)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Long personId = getPersonIdByApiAbsence(apiAbsence);
        if (personId == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        PersonAbsence personAbsence = new PersonAbsence(apiAbsence.getId(), personId, apiAbsence.getReason(), apiAbsence.getFromTime(), apiAbsence.getTillTime());
        return crudAction.apply(token, personAbsence);
    }

    private Long getPersonIdByApiAbsence(ApiAbsence apiAbsence) {
        if (apiAbsence.getPersonId() != null) {
            return apiAbsence.getPersonId();
        }

        return getPersonIdByWorkerId(apiAbsence.getWorkerExtId(), apiAbsence.getCompanyCode());
    }

    private Long getPersonIdByWorkerId(String workerId, String companyCode) {
        CompanyHomeGroupItem groupCompany = companyGroupHomeDAO.getByExternalCode(companyCode.trim());
        if (groupCompany == null || groupCompany.getCompanyId() == null) {
            return null;
        }
        WorkerEntry workerEntry = workerEntryDAO.getByExternalId(workerId, groupCompany.getCompanyId());
        return workerEntry == null ? null : workerEntry.getPersonId();
    }

    private boolean validateFields(PersonAbsence absence) {
        if (absence == null) {
            return false;
        }
        if (absence.getReason() == null) {
            return false;
        }
        if (absence.getPersonId() == null) {
            return false;
        }
        if (absence.getFromTime() == null) {
            return false;
        }
        if (absence.getTillTime() == null) {
            return false;
        }
        if (absence.getFromTime().after(absence.getTillTime())) {
            return false;
        }
        if (absence.getFromTime().equals(absence.getTillTime())) {
            return false;
        }

        return true;
    }

    private boolean isCurrentAbsence(PersonAbsence absence) {
        Date now = new Date();
        return now.after(absence.getFromTime()) && now.before(absence.getTillTime());
    }

    private boolean hasAbsenceIntersections(Long employeeId, Date dateFrom, Date dateTill, Long excludeId) {
        return stream(personAbsenceDAO.listByEmployeeAndDateBounds(
                employeeId,
                dateFrom,
                dateTill
        )).anyMatch(r -> !Objects.equals(excludeId, r.getId()));
    }

    private Set<Person> getAbsenceNotifiers(PersonAbsence absence) {
        Set<Person> notifiers = stream(new ArrayList<Person>() {{
            addAll(personNotifierDAO.getByPersonId(absence.getPersonId()).stream().map(PersonNotifier::getNotifier).collect(Collectors.toSet()));
            add(personDAO.get(absence.getCreatorId()));
            add(personDAO.get(absence.getPersonId()));
        }}).collect(Collectors.toSet());
        jdbcManyRelationsHelper.fill(notifiers, Person.Fields.CONTACT_ITEMS);
        return notifiers;
    }
}
