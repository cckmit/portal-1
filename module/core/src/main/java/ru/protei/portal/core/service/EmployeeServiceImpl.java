package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.BirthdaysNotificationEvent;
import ru.protei.portal.core.exception.ResultStatusException;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.WorkerEntryQuery;
import ru.protei.portal.core.model.struct.*;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.WorkerEntryShortView;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.utils.DateUtils;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.DateRangeUtils.makeDateWithOffset;


/**
 * Реализация сервиса управления сотрудниками
 */
public class EmployeeServiceImpl implements EmployeeService {

    private static Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

    @Autowired
    CompanyGroupHomeDAO groupHomeDAO;

    @Autowired
    PersonDAO personDAO;

    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    PersonAbsenceDAO absenceDAO;

    @Autowired
    EmployeeShortViewDAO employeeShortViewDAO;

    @Autowired
    WorkerEntryShortViewDAO workerEntryShortViewDAO;

    @Autowired
    WorkerEntryDAO workerEntryDAO;

    @Autowired
    CompanyDepartmentDAO companyDepartmentDAO;

    @Autowired
    UserLoginDAO userLoginDAO;

    @Autowired
    private UserRoleDAO userRoleDAO;

    @Autowired
    PersonAbsenceDAO personAbsenceDAO;

    @Autowired
    ContactItemDAO contactItemDAO;

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Autowired
    PortalConfig portalConfig;

    @Autowired
    YoutrackService youtrackService;

    @Autowired
    LegacySystemDAO migrationManager;

    @Autowired
    AuditObjectDAO auditObjectDAO;

    @Autowired
    EventPublisherService publisherService;

    @Override
    public Result<List<PersonShortView>> shortViewList( EmployeeQuery query) {
        List<Person> list = personDAO.getEmployees(query);

        if (list == null) {
            return Result.error( En_ResultStatus.GET_DATA_ERROR);
        }

        List<PersonShortView> result = list.stream().map( Person::toFullNameShortView ).collect(Collectors.toList());

        return ok(result);
    }

    @Override
    public Result<SearchResult<EmployeeShortView>> employeeList(AuthToken token, EmployeeQuery query) {

        SearchResult<EmployeeShortView> sr = employeeShortViewDAO.getSearchResult(query);
        jdbcManyRelationsHelper.fill(sr.getResults(), EmployeeShortView.Fields.CONTACT_ITEMS);
        sr.setResults(stream(sr.getResults())
                .map(this::removeSensitiveInformation)
                .collect(Collectors.toList()));
        List<EmployeeShortView> results = sr.getResults();

        if (CollectionUtils.isNotEmpty(results)) {
            Set<Long> employeeIds = results.stream().map(e -> e.getId()).collect(Collectors.toSet());
            List<WorkerEntryShortView> workerEntries = workerEntryShortViewDAO.listByPersonIds(employeeIds);
            results.forEach(employee ->
                employee.setWorkerEntries(workerEntries.stream().filter(workerEntry -> workerEntry.getPersonId().equals(employee.getId())).collect(Collectors.toList()))
             );
        }
        return ok(sr);
    }

    @Override
    public Result<SearchResult<EmployeeShortView>> employeeListWithChangedHiddenCompanyNames(AuthToken token, EmployeeQuery query) {

        query.setHomeCompanies(fillHiddenCompaniesIfProteiChosen(query.getHomeCompanies()));

        SearchResult<EmployeeShortView> sr = employeeShortViewDAO.getSearchResult(query);
        jdbcManyRelationsHelper.fill(sr.getResults(), EmployeeShortView.Fields.CONTACT_ITEMS);
        sr.setResults(stream(sr.getResults())
                .map(this::removeSensitiveInformation)
                .collect(Collectors.toList()));
        List<EmployeeShortView> results = sr.getResults();

        if (CollectionUtils.isNotEmpty(results)) {

            Set<Long> employeeIds = results.stream().map(EmployeeShortView::getId).collect(Collectors.toSet());
            List<WorkerEntryShortView> workerEntries = changeCompanyNameIfHidden(workerEntryShortViewDAO.listByPersonIds(employeeIds));

            AbsenceQuery absenceQuery = makeAbsenceQuery(employeeIds);
            List<PersonAbsence> personAbsences = personAbsenceDAO.listByQuery(absenceQuery);

            results.forEach(employee -> {
                employee.setWorkerEntries(workerEntries.stream()
                        .filter(workerEntry -> workerEntry.getPersonId().equals(employee.getId()))
                        .collect(Collectors.toList()));
                employee.setCurrentAbsence(personAbsences.stream()
                        .filter(absence -> absence.getPersonId().equals(employee.getId()))
                        .findFirst().orElse(null));
            });
        }
        return ok(sr);
    }

    @Override
    public Result<EmployeeShortView> getEmployee(AuthToken token, Long employeeId) {

        if (employeeId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        EmployeeShortView employeeShortView = employeeShortViewDAO.get(employeeId);

        if (employeeShortView == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        jdbcManyRelationsHelper.fill(employeeShortView, EmployeeShortView.Fields.CONTACT_ITEMS);
        jdbcManyRelationsHelper.fill(employeeShortView, "workerEntries");
        employeeShortView = removeSensitiveInformation(employeeShortView);

        return ok(employeeShortView);
    }

    @Override
    public Result<List<WorkerEntryShortView>> getWorkerEntryList(AuthToken token, int offset, int limit) {
        SearchResult<WorkerEntryShortView> result = workerEntryShortViewDAO.getAll(offset, limit);
        if (result == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }
        return ok(result.getResults());
    }

    @Override
    public Result<EmployeeShortView> getEmployeeWithChangedHiddenCompanyNames(AuthToken token, Long employeeId) {

        if (employeeId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        EmployeeShortView employeeShortView = employeeShortViewDAO.get(employeeId);

        if (employeeShortView == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        jdbcManyRelationsHelper.fill(employeeShortView, EmployeeShortView.Fields.CONTACT_ITEMS);
        jdbcManyRelationsHelper.fill(employeeShortView, "workerEntries");
        employeeShortView = removeSensitiveInformation(employeeShortView);

        employeeShortView.setWorkerEntries(changeCompanyNameIfHidden(employeeShortView.getWorkerEntries()));
        employeeShortView.setCurrentAbsence(personAbsenceDAO.currentAbsence(employeeId));

        return ok(employeeShortView);
    }

    @Override
    public Result<PersonShortView> getDepartmentHead(AuthToken token, Long departmentId) {
        if (departmentId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        CompanyDepartment department = companyDepartmentDAO.get(departmentId);

        return ok(department == null ? null : (department.getHead() == null ?
                        (department.getParentHead() == null ? null : department.getParentHead().toFullNameShortView()) :
                department.getHead().toFullNameShortView()));
    }

    @Transactional
    @Override
    public Result<Person> createEmployeePerson(AuthToken token, Person person) {
        if (person == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!validatePerson(person)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        String email = new PlainContactInfoFacade(person.getContactInfo()).getEmail();

        if (isEmailExists(person.getId(), email)) {
            return error(En_ResultStatus.EMPLOYEE_EMAIL_ALREADY_EXIST);
        }

        if (isUserLoginExist(email)){
            return error(En_ResultStatus.LOGIN_ALREADY_EXIST);
        }

        if (isEmployeeExist(person)){
            return error(En_ResultStatus.EMPLOYEE_ALREADY_EXIST);
        }

        person.setLocale("ru");
        person.setDisplayName(person.getLastName() + " " + person.getFirstName() + (StringUtils.isNotEmpty(person.getSecondName()) ? " " + person.getSecondName() : ""));
        person.setDisplayShortName(createPersonShortName(person));

        person.setCreated(new Date());
        person.setCreator(token.getPersonDisplayShortName());

        person.setCompanyId(CrmConstants.Company.HOME_COMPANY_ID);

        Long personId = personDAO.persist(person);

        if (personId != null) {

            person.setId(personId);
            contactItemDAO.saveOrUpdateBatch(person.getContactItems());
            jdbcManyRelationsHelper.persist(person, Person.Fields.CONTACT_ITEMS);

            return createLDAPAccount(person)
                    .flatMap(userLogin -> {
                        userLogin.setAdminStateId(En_AdminState.UNLOCKED.getId());
                        if (!saveUserLogin(userLogin, token)) {
                            throw new ResultStatusException(En_ResultStatus.NOT_CREATED);
                        }
                        return ok();
                    }).map(ignore -> person);
        }

        log.warn("createEmployeePerson(): person not created. id = null. person={}, token={}", person, token);
        return error(En_ResultStatus.NOT_CREATED);
    }

    @Override
    @Transactional
    public Result<Boolean> updateEmployeePerson(AuthToken token, Person person, boolean needToChangeAccount) {
        if (person == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Person oldPerson = personDAO.get(person.getId());

        if (oldPerson == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!validatePerson(person) || person.getId() == null) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        String email = new PlainContactInfoFacade(person.getContactInfo()).getEmail();

        if (isEmailExists(person.getId(), email)) {
            return error(En_ResultStatus.EMPLOYEE_EMAIL_ALREADY_EXIST);
        }

        person.setDisplayName(person.getLastName() + " " + person.getFirstName() + (StringUtils.isNotEmpty(person.getSecondName()) ? " " + person.getSecondName() : ""));
        person.setDisplayShortName(createPersonShortName(person));
        person.setCompanyId(CrmConstants.Company.HOME_COMPANY_ID);
        person.setContactInfo(removeSensitiveInformation(person.getContactInfo()));

        boolean success = personDAO.partialMerge(person,  "company_id", "firstname", "lastname", "secondname", "sex", "birthday", "ipaddress", "displayname", "displayShortName");
        if (!success) {
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        contactItemDAO.saveOrUpdateBatch(person.getContactItems());
        jdbcManyRelationsHelper.persist(person, Person.Fields.CONTACT_ITEMS);

        final boolean YOUTRACK_INTEGRATION_ENABLED = portalConfig.data().integrationConfig().isYoutrackEmployeeSyncEnabled();

        if (needToChangeAccount && YOUTRACK_INTEGRATION_ENABLED) {
            createChangeLastNameYoutrackIssueIfNeeded(person.getId(), person.getFirstName(), person.getLastName(), person.getSecondName(), oldPerson.getLastName());
        }

        return ok(true);
    }

    @Override
    @Transactional
    public Result<Boolean> updateEmployeeWorker(AuthToken token, WorkerEntry worker) {
        if (worker == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (isWorkerFrom1C(worker)){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        boolean result = workerEntryDAO.partialMerge(worker, "dep_id", "companyId", "positionId", "active");

        if (result) {
            return ok(true);
        }

        return error(En_ResultStatus.INTERNAL_ERROR);
    }

    @Override
    @Transactional
    public Result<WorkerEntry> createEmployeeWorker(AuthToken token, WorkerEntry worker) {
        if (worker == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (isWorkerFrom1C(worker)){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        worker.setCreated(new Date());
        Long workerId = workerEntryDAO.persist(worker);

        if (workerId != null) {
            worker.setId(workerId);
            return ok(worker);
        }

        return error(En_ResultStatus.INTERNAL_ERROR);
    }

    @Override
    @Transactional
    public Result<Boolean> fireEmployee(AuthToken token, Person person) {

        Person personFromDb = personDAO.getEmployee(person.getId());

        if (personFromDb == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        personFromDb.setFired(new Date());
        personFromDb.setIpAddress(personFromDb.getIpAddress() == null ? null : personFromDb.getIpAddress().replace(".", "_"));

        jdbcManyRelationsHelper.fill(personFromDb, Person.Fields.CONTACT_ITEMS);
        PlainContactInfoFacade contactInfoFacade = new PlainContactInfoFacade(personFromDb.getContactInfo());
        contactInfoFacade.setEmail(null);
        boolean result = personDAO.merge(personFromDb);
        jdbcManyRelationsHelper.persist(personFromDb, Person.Fields.CONTACT_ITEMS);

        if (result) {
            boolean isRemoved = removeWorkerEntriesByPersonId(personFromDb.getId());

            if (!isRemoved){
                throw new ResultStatusException(En_ResultStatus.EMPLOYEE_NOT_FIRED_FROM_THESE_COMPANIES);
            }

            List<UserLogin> userLogins = userLoginDAO.findByPersonId(personFromDb.getId());
            if(isNotEmpty(userLogins)) {
                for (UserLogin userLogin : userLogins) {
                    userLogin.setAdminStateId(En_AdminState.LOCKED.getId());
                    updateAccount(userLogin, token);
                }
            }
        }

        if (portalConfig.data().legacySysConfig().isImportEmployeesEnabled()) {
            if (!fireEmployeeInOldPortal(personFromDb)) {
                log.warn("fireEmployee(): fail to migrate employee to old portal. Person={}", personFromDb);
                return error(En_ResultStatus.EMPLOYEE_MIGRATION_FAILED);
            }
        }

        final boolean YOUTRACK_INTEGRATION_ENABLED = portalConfig.data().integrationConfig().isYoutrackEmployeeSyncEnabled();

        if (YOUTRACK_INTEGRATION_ENABLED) {
            createFireEmployeeYoutrackIssue(personFromDb);
        }

        return ok(result);
    }

    @Override
    @Transactional
    public Result<Boolean> updateEmployeeWorkers(AuthToken token, List<WorkerEntry> newWorkerEntries){
        if (newWorkerEntries == null || newWorkerEntries.isEmpty() || newWorkerEntries.get(0).getPersonId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Long personId = newWorkerEntries.get(0).getPersonId();
        final int TO_CREATE = 1;
        final int TO_REMOVE = 2;

        checkActiveFlag(newWorkerEntries);
        hireEmployeeIfNeed(personId);

        WorkerEntryQuery query = new WorkerEntryQuery(personId);
        List<WorkerEntry> oldWorkerEntries = workerEntryDAO.getWorkers(query);

        Map<WorkerEntry, Integer> finalWorkersMap = new HashMap<>();
        fillMapWorkersToCreate(finalWorkersMap, newWorkerEntries, TO_CREATE);
        fillMapWorkersToRemove(finalWorkersMap, newWorkerEntries, oldWorkerEntries, TO_REMOVE);

        for (Map.Entry<WorkerEntry, Integer> entry : finalWorkersMap.entrySet()) {
            switch (entry.getValue()){
                case TO_CREATE :
                    Result createResult  = createEmployeeWorker(token, entry.getKey());
                    makeAudit(entry.getKey(), En_AuditType.WORKER_CREATE, token);
                    if (createResult.isError()){
                        throw new ResultStatusException(createResult.getStatus(), "Error while worker entry creating");
                    }
                    break;
                case TO_REMOVE :
                    Result removeStatus  = removeWorkerEntry(entry.getKey());
                    makeAudit(entry.getKey(), En_AuditType.WORKER_REMOVE, token);
                    if (removeStatus.isError()){
                        throw new ResultStatusException(removeStatus.getStatus(), "Error while worker entry removing");
                    }
                    break;
            }
        }

        if (portalConfig.data().legacySysConfig().isImportEmployeesEnabled()) {
            if (!updateEmployeeInOldPortal(personId)) {
                log.warn("updateEmployeeWorkers(): fail to migrate employee to old portal. personId={}", personId);
                return error(En_ResultStatus.EMPLOYEE_MIGRATION_FAILED);
            }
        }

        return ok(true);
    }

    @Override
    public Result<EmployeesBirthdays> getEmployeesBirthdays(AuthToken token, Date dateFrom, Date dateUntil) {
        EmployeeQuery query = new EmployeeQuery();
        query.setFired(false);
        query.setDeleted(false);
        query.setBirthdayRange(new DateRange(En_DateIntervalType.FIXED, dateFrom, dateUntil));
        List<EmployeeShortView> employees = employeeShortViewDAO.getEmployees(query);
        EmployeesBirthdays birthdays = new EmployeesBirthdays();
        birthdays.setDateFrom(dateFrom);
        birthdays.setDateUntil(dateUntil);
        birthdays.setBirthdays(stream(employees)
                .filter(employee -> nonNull(employee.getBirthday()))
                .map(employee -> {
                    EmployeeBirthday birthday = new EmployeeBirthday();
                    birthday.setId(employee.getId());
                    birthday.setName(employee.getDisplayShortName());
                    birthday.setGender(employee.getGender());
                    birthday.setBirthdayMonth(employee.getBirthday().getMonth() + 1);
                    birthday.setBirthdayDayOfMonth(employee.getBirthday().getDate());
                    return birthday;
                })
                .collect(Collectors.toList()));
        return ok(birthdays);
    }

    /**
     * Уведомление о грядущих датах рождения
     * @return
     */
    @Override
    public Result<Void> notifyAboutBirthdays() {

        Date from = makeDateWithOffset(-2);
        Date to = makeDateWithOffset(9);

        log.info("notifyAboutBirthdays(): start");

        EmployeeQuery query = new EmployeeQuery();
        query.setFired(false);
        query.setDeleted(false);
        query.setBirthdayRange(new DateRange(En_DateIntervalType.FIXED, from, to));
        query.setSortField(En_SortField.birthday);
        query.setSortDir(En_SortDir.ASC);
        List<EmployeeShortView> employees = employeeShortViewDAO.getEmployees(query);

        if (CollectionUtils.isEmpty(employees)) {
            log.info("notifyAboutBirthdays(): employees birthdays list is empty for period {} - {}", from, to);
            return ok();
        }

        List<NotificationEntry> notifiers = makeNotificationListFromConfiguration();

        if (CollectionUtils.isEmpty(notifiers)) {
            log.info("notifyAboutBirthdays(): no entries to be notified");
            return ok();
        }

        log.info("notifyAboutBirthdays(): birthdays notification: entries to be notified: {}", notifiers);

        publisherService.publishEvent(new BirthdaysNotificationEvent(this, employees, from, to, notifiers));

        log.info("notifyAboutBirthdays(): done");
        return ok();
    }

    private List<NotificationEntry> makeNotificationListFromConfiguration() {
        return Stream.of(
                portalConfig.data().getMailNotificationConfig().getCrmBirthdaysNotificationsRecipients()
        )
                .filter(Objects::nonNull)
                .filter(not(String::isEmpty))
                .map(address -> NotificationEntry.email(address, CrmConstants.DEFAULT_LOCALE))
                .collect(Collectors.toList());
    }

    private EmployeeShortView removeSensitiveInformation(EmployeeShortView employeeShortView) {
        List<ContactItem> sensitive = getSensitiveContactItems(employeeShortView.getContactInfo().getItems());
        employeeShortView.setContactInfo(new ContactInfo(stream(employeeShortView.getContactInfo().getItems())
                .filter(item -> !sensitive.contains(item))
                .collect(Collectors.toList())));
        return employeeShortView;
    }

    private ContactInfo removeSensitiveInformation(ContactInfo contactInfo) {
        List<ContactItem> sensitive = getSensitiveContactItems(contactInfo.getItems());
        return new ContactInfo(stream(contactInfo.getItems())
                .filter(item -> !sensitive.contains(item))
                .collect(Collectors.toList()));
    }

    private List<ContactItem> getSensitiveContactItems(List<ContactItem> contactItems) {
        List<En_ContactItemType> types = listOf(En_ContactItemType.ADDRESS, En_ContactItemType.ADDRESS_LEGAL);
        return stream(contactItems)
                .filter(item -> types.contains(item.type()))
                .collect(Collectors.toList());
    }

    private void updateAccount(UserLogin userLogin, AuthToken token) {
        if (userLoginDAO.saveOrUpdate(userLogin)) {
            jdbcManyRelationsHelper.persist( userLogin, "roles" );
            makeAudit(userLogin, En_AuditType.ACCOUNT_MODIFY, token);
        }
    }

    private void fillMapWorkersToRemove(Map<WorkerEntry, Integer> finalWorkersMap, List<WorkerEntry> newWorkerEntries, List<WorkerEntry> oldWorkerEntries, int TO_REMOVE) {
        for (WorkerEntry worker : oldWorkerEntries) {
            boolean isActualWorkerEntry = false;

            if (isWorkerFrom1C(worker)){
                continue;
            }

            Iterator<WorkerEntry> workerEntryIterator = newWorkerEntries.iterator();
            while (workerEntryIterator.hasNext()){
                WorkerEntry workerEntry = workerEntryIterator.next();

                if (worker.getId().equals(workerEntry.getId())){
                    isActualWorkerEntry = true;
                    workerEntryIterator.remove();
                    break;
                }
            }

            if(!isActualWorkerEntry){
                finalWorkersMap.put(worker, TO_REMOVE);
            }
        }
    }

    private void fillMapWorkersToCreate(Map<WorkerEntry, Integer> finalWorkersMap, List<WorkerEntry> newWorkerEntries, int TO_CREATE) {
        Iterator<WorkerEntry> workerEntryIterator = newWorkerEntries.iterator();

        while (workerEntryIterator.hasNext()) {
            WorkerEntry workerEntry = workerEntryIterator.next();
            if (workerEntry.getId() == null){
                finalWorkersMap.put(workerEntry, TO_CREATE);
                workerEntryIterator.remove();
                continue;
            }

            if (isWorkerFrom1C(workerEntry)){
                workerEntryIterator.remove();
            }
        }
    }

    private void makeAudit(AuditableObject object, En_AuditType type, AuthToken token){
        AuditObject auditObject = new AuditObject();
        auditObject.setCreated( new Date() );
        auditObject.setType(type);
        auditObject.setCreatorId( token.getPersonId() );
        try {
            auditObject.setCreatorIp(Inet4Address.getLocalHost ().getHostAddress());
        } catch (UnknownHostException e) {
            log.warn("makeAudit(): fail to setCreatorIp, UnknownHostException");
            auditObject.setCreatorIp("0.0.0.0");
        }
        auditObject.setCreatorShortName(token.getPersonDisplayShortName());
        auditObject.setEntryInfo(object);

        auditObjectDAO.insertAudit(auditObject);
    }

    private void hireEmployeeIfNeed(Long personId) {
        Person person = personDAO.get(personId);

        if (person != null && person.isFired()){
            person.setFired(false, null);
            personDAO.merge(person);
        }
    }

    private boolean updateEmployeeInOldPortal(Long personId) {
        List<WorkerEntry> workers = workerEntryDAO.getWorkers(new WorkerEntryQuery(personId));

        WorkerEntry worker = workers == null ? null : getMainEntry(workers);

        Person person = personDAO.get(personId);
        if (worker == null || person == null){
            log.warn("updateEmployeeInOldPortal(): activeWorker={}, person={}", worker, person);
            return false;
        }
        jdbcManyRelationsHelper.fill(person, Person.Fields.CONTACT_ITEMS);

        return migrationManager.saveExternalEmployee(person, worker.getDepartmentName(), worker.getPositionName()).equals(En_ResultStatus.OK);
    }

    private boolean fireEmployeeInOldPortal(Person person) {
        if (person == null){
            return false;
        }

        return migrationManager.saveExternalEmployee(person, "", "").equals(En_ResultStatus.OK);
    }

    private void checkActiveFlag(List<WorkerEntry> newWorkerEntries) {
        boolean isFlagSet = newWorkerEntries.stream()
                .anyMatch(workerEntry -> workerEntry.getActiveFlag() > 0);

        if (!isFlagSet){
            newWorkerEntries.get(0).setActiveFlag(1);
        }
    }

    private void createChangeLastNameYoutrackIssueIfNeeded(Long employeeId, String firstName, String lastName, String secondName, String oldLastName) {
        if (Objects.equals(lastName, oldLastName)) {
            return;
        }

        String employeeOldFullName = oldLastName + " " + firstName + " " + (secondName != null ? secondName : "");
        String employeeNewFullName = lastName + " " + firstName + " " + (secondName != null ? secondName : "");

        String summary = "Смена фамилии сотрудника " + employeeOldFullName;

        final String PORTAL_URL = portalConfig.data().getCommonConfig().getCrmUrlInternal();

        String description = "Карточка сотрудника: " + "[" + employeeNewFullName + "](" + PORTAL_URL + "#employee_preview:id=" + employeeId + ")" + "\n" +
                "Старое ФИО: " + employeeOldFullName + "\n" +
                "Новое ФИО: " + employeeNewFullName + "\n" +
                "\n" +
                "Необходимо изменение учетной записи, почты.";

        final String ADMIN_PROJECT_NAME = portalConfig.data().youtrack().getAdminProject();

        youtrackService.createIssue( ADMIN_PROJECT_NAME, summary, description );
    }

    private void createFireEmployeeYoutrackIssue(Person person) {

        String employeeFullName = person.getLastName() + " " + person.getFirstName() + " " + (person.getSecondName() != null ? person.getSecondName() : "");

        String summary = "Увольнение сотрудника " + employeeFullName;

        final String PORTAL_URL = portalConfig.data().getCommonConfig().getCrmUrlInternal();

        String description = "Карточка сотрудника: " + "[" + employeeFullName + "](" + PORTAL_URL + "#employee_preview:id=" + person.getId() + ")";

        youtrackService.createFireWorkerIssue(summary, description );
    }

    private boolean removeWorkerEntriesByPersonId(Long personId){
        WorkerEntryQuery workerEntryQuery = new WorkerEntryQuery(personId);
        List<WorkerEntry> workers = workerEntryDAO.getWorkers(workerEntryQuery);

        for (WorkerEntry worker : workers) {
            if (isWorkerFrom1C(worker)){
                return false;
            }
        }

        boolean isSuccess = true;
        for (WorkerEntry worker : workers) {
            if (!workerEntryDAO.remove(worker)){
                isSuccess = false;
            }
        }

        return isSuccess;
    }

    private Result<Boolean> removeWorkerEntry (WorkerEntry worker){
        if (isWorkerFrom1C(worker)){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        boolean result = workerEntryDAO.remove(worker);

        return result ? ok(result) : error(En_ResultStatus.INTERNAL_ERROR);
    }

    private boolean isWorkerFrom1C(WorkerEntry worker){
        return !(worker.getContractAgreement() || companyDAO.getAllHomeCompanyIdsWithoutSync().contains(worker.getCompanyId()));
    }

    private boolean isEmployeeExist(Person person){
        EmployeeQuery employeeQuery = new EmployeeQuery();
        employeeQuery.setDeleted(false);
        employeeQuery.setFirstName(person.getFirstName());
        employeeQuery.setLastName(person.getLastName());
        employeeQuery.setSecondName(person.getSecondName());
        employeeQuery.setBirthdayRange(person.getBirthday() == null ? null :
                new DateRange(En_DateIntervalType.FIXED, person.getBirthday(), person.getBirthday()));

        List<EmployeeShortView> employee = employeeShortViewDAO.getEmployees(employeeQuery);

        return employee != null && !employee.isEmpty();
    }

    private String createPersonShortName(Person person) {
        StringBuilder b = new StringBuilder();
        b.append (person.getLastName()).append(" ")
                .append (person.getFirstName().substring(0,1).toUpperCase()).append(".")
        ;

        if (!person.getSecondName().isEmpty()) {
            b.append(" ").append(person.getSecondName().substring(0,1).toUpperCase()).append(".");
        }
        return b.toString();
    }

    private Set<EntityOption> fillHiddenCompaniesIfProteiChosen(Set<EntityOption> homeCompanies) {
        if (homeCompanies != null && !homeCompanies.isEmpty()) {
            boolean containsProtei = homeCompanies.stream().anyMatch(company -> company.getId().equals(CrmConstants.Company.MAIN_HOME_COMPANY_ID));
            if (containsProtei) {
                SearchResult<Company> homeCompaniesSearchResult = companyDAO.getSearchResultByQuery(new CompanyQuery(true));
                List<Company> allHomeCompanies = homeCompaniesSearchResult.getResults();
                allHomeCompanies.forEach(company -> {
                    if (company.getHidden() != null && company.getHidden()) {
                        homeCompanies.add(new EntityOption(company.getCname(), company.getId()));
                    }
                });
            }
        }
        return homeCompanies;
    }

    private boolean validatePerson(Person person) {
        if (person.isFired()) {
            log.warn("avoid to update fired person with id = {}", person.getId());
            return false;
        }

        if (StringUtils.isBlank(person.getFirstName())) {
            return false;
        }

        if (StringUtils.isBlank(person.getLastName())) {
            return false;
        }

        if (StringUtils.isBlank(person.getIpAddress())) {
            return false;
        }

        if (En_Gender.UNDEFINED.equals(person.getGender())) {
            return false;
        }

        PlainContactInfoFacade facade = new PlainContactInfoFacade(person.getContactInfo());
        if (StringUtils.isBlank(facade.getEmail())) {
            return false;
        }

        return true;
    }

    private boolean isEmailExists(Long personId, String email) {

        List<Person> employeeByEmail = personDAO.findEmployeeByEmail(email);

        if (CollectionUtils.isNotEmpty(employeeByEmail)){
            if (personId == null) {
                return true;
            }

            if (employeeByEmail.stream()
                    .noneMatch(personFromDB -> personFromDB.getId().equals(personId))){
                return true;
            }
        }

        return false;
    }

    private List<WorkerEntryShortView> changeCompanyNameIfHidden(List<WorkerEntryShortView> list) {
        stream(list).forEach(workerEntry ->
                workerEntry.setCompanyName(Boolean.TRUE.equals(workerEntry.getCompanyIsHidden())
                        ? CrmConstants.Company.MAIN_HOME_COMPANY_NAME
                        : workerEntry.getCompanyName()
                )
        );
        return list;
    }

    private Result<UserLogin> createLDAPAccount(Person person) {
            String email = new PlainContactInfoFacade(person.getContactInfo()).getEmail();

            UserLogin userLogin = userLoginDAO.createNewUserLogin(person);
            userLogin.setUlogin(makeLogin(email));
            userLogin.setAuthType(En_AuthType.LDAP);
            userLogin.setRoles(new HashSet<>(userRoleDAO.getDefaultEmployeeRoles()));
            return ok(userLogin);
    }

    private String makeLogin(String email) {
        return email.substring(0, email.indexOf("@")).trim();
    }

    private boolean isUserLoginExist(String email) {
        return !userLoginDAO.isUnique(makeLogin(email));
    }

    private boolean saveUserLogin(UserLogin userLogin, AuthToken authToken) {
        if (userLoginDAO.saveOrUpdate(userLogin)) {
            jdbcManyRelationsHelper.persist( userLogin, "roles" );
            makeAudit(userLogin, En_AuditType.ACCOUNT_CREATE, authToken);
            return true;
        } else {
            log.warn("saveUserLogin(): fail to create login. Rollback transaction. userLogin={}, authToken={}", userLogin, authToken);
           return false;
        }
    }

    AbsenceQuery makeAbsenceQuery(Set<Long> employeeIds) {
        Date startOfToday = DateUtils.resetSeconds(new Date());
        return new AbsenceQuery(
                new DateRange(En_DateIntervalType.FIXED, startOfToday, startOfToday),
                employeeIds,
                Arrays.asList(En_AbsenceReason.values()).stream()
                        .filter(En_AbsenceReason::isActual)
                        .map(En_AbsenceReason::getId)
                        .collect(Collectors.toSet()));
    }

    private WorkerEntry getMainEntry(List<WorkerEntry> workers) {
        return workers.stream().filter(WorkerEntry::isMain).findFirst().orElse(getFirstEntry(workers));
    }

    private WorkerEntry getFirstEntry(List<WorkerEntry> workers) {
        return workers.stream().findFirst().orElse(null);
    }
}
