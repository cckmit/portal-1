package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.enterprise1c.api.Api1CWork;
import ru.protei.portal.core.event.BirthdaysNotificationEvent;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.*;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.DateRangeUtils.makeDateWithOffset;
import static ru.protei.portal.core.model.util.CrmConstants.Masks.RUS_PHONE_NUMBER_PATTERN;
import static ru.protei.portal.core.model.util.CrmConstants.Masks.WORK_PHONE_NUMBER_PATTERN;
import static ru.protei.portal.core.model.view.EmployeeShortView.Fields.CONTACT_ITEMS;
import static ru.protei.portal.core.model.view.EmployeeShortView.Fields.WORKER_ENTRIES;


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
    PersonShortViewDAO personShortViewDAO;

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
    @Autowired
    CompanyService companyService;
    @Autowired
    Api1CWork api1CWorkService;

    private Pattern workPhone = Pattern.compile(WORK_PHONE_NUMBER_PATTERN);
    private Pattern mobilePhone = Pattern.compile(RUS_PHONE_NUMBER_PATTERN);

    @Override
    public Result<List<PersonShortView>> shortViewList( EmployeeQuery query) {
        List<PersonShortView> list = personShortViewDAO.getEmployees(query);

        if (list == null) {
            return Result.error( En_ResultStatus.GET_DATA_ERROR);
        }

        return ok(list);
    }

    @Override
    public Result<SearchResult<EmployeeShortView>> employeeList(AuthToken token, EmployeeQuery query) {

        query.setHomeCompanies(fillHiddenCompaniesIfProteiChosen(query.getHomeCompanies()));

        SearchResult<EmployeeShortView> sr = employeeShortViewDAO.getSearchResult(query);
        jdbcManyRelationsHelper.fill(sr.getResults(), EmployeeShortView.Fields.CONTACT_ITEMS);
        sr.setResults(stream(sr.getResults())
                .map(this::removeAllPrivacyInfo)
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
                employee.setTimezoneOffset(employee.getBirthday() == null ? null : employee.getBirthday().getTimezoneOffset());
            });
        }
        return ok(sr);
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
    public Result<EmployeeShortView> getEmployee(AuthToken token, Long employeeId) {
        return getEmployee(employeeId).map(this::removeAllPrivacyInfo);
    }

    @Override
    public Result<EmployeeShortView> getEmployeeWithPrivacyInfo(AuthToken token, Long employeeId) {
        if (!portalConfig.data().getCommonConfig().isProductionServer()) {
            return getEmployee(token, employeeId);
        }

        return getEmployee(employeeId).map(employee -> {
            employee.setContactInfo(removeContactPrivacyInfo(employee.getContactInfo()));
            return employee;
        });
    }

    @Override
    public Result<PersonShortView> getDepartmentHead(AuthToken token, Long departmentId) {
        if (departmentId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        CompanyDepartment department = companyDepartmentDAO.get(departmentId);
        if (department == null) return ok( null );
        if (department.getHead() != null) return ok( department.getHead() );
        if (department.getParentHead() != null) return ok( department.getParentHead() );
        return ok( null );
    }

    @Override
    @Transactional
    public Result<Person> createEmployee(AuthToken token, Person person, List<WorkerEntry> workerEntries) {

        if (person == null || CollectionUtils.isEmpty(workerEntries)) {
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

        WorkerEntry mainWorkerEntry = checkAndGetMainWorkerEntry(workerEntries);
        person.setCompanyId(groupHomeDAO.isSingleHomeCompany(mainWorkerEntry.getCompanyId()) ? mainWorkerEntry.getCompanyId(): CrmConstants.Company.HOME_COMPANY_ID);

        Long personId = personDAO.persist(person);
        if (personId == null) {
            log.error("createEmployee(): failed to create employee to db");
            return error(En_ResultStatus.NOT_CREATED);
        }

        person.setId(personId);
        contactItemDAO.saveOrUpdateBatch(person.getContactItems());
        jdbcManyRelationsHelper.persist(person, Person.Fields.CONTACT_ITEMS);

        updateWorkerEntries(token, personId, workerEntries);

        Result<UserLogin> userLoginResult = createLDAPAccount(token, person);
        if (userLoginResult.isError()) {
            throw new RollbackTransactionException(userLoginResult.getStatus(), "Failed to create employee login, personId=" + personId);
        }

        if (portalConfig.data().legacySysConfig().isExportEnabled()) {
            if (!updateEmployeeInOldPortal(personId)) {
                log.warn("createEmployee(): failed to migrate employee to old portal, personId={}", personId);
                return error(En_ResultStatus.EMPLOYEE_MIGRATION_FAILED);
            }
        }

        return ok(person);
    }

    @Override
    @Transactional
    public Result<Person> updateEmployee(AuthToken token, Person person, List<WorkerEntry> workerEntries, boolean needToChangeAccount) {

        if (person == null || CollectionUtils.isEmpty(workerEntries)) {
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
        person.setContactInfo(removeContactPrivacyInfo(person.getContactInfo()));

        boolean success = personDAO.partialMerge(person,  "firstname", "lastname", "secondname", "sex", "birthday", "ipaddress", "displayname", "displayShortName", "isfired", "firedate", "inn");
        if (!success) {
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        contactItemDAO.saveOrUpdateBatch(person.getContactItems());
        jdbcManyRelationsHelper.persist(person, Person.Fields.CONTACT_ITEMS);

        updateWorkerEntries(token, person.getId(), workerEntries);

        if (isHiredAgain(oldPerson)) {
            userLoginDAO.unlockAccounts(oldPerson.getId());
        }

        final boolean YOUTRACK_INTEGRATION_ENABLED = portalConfig.data().integrationConfig().isYoutrackEmployeeSyncEnabled();
        if (needToChangeAccount && YOUTRACK_INTEGRATION_ENABLED) {
            createChangeLastNameYoutrackIssueIfNeeded(person.getId(), person.getFirstName(), person.getLastName(), person.getSecondName(), oldPerson.getLastName());
        }

        if (portalConfig.data().legacySysConfig().isExportEnabled()) {
            if (!updateEmployeeInOldPortal(person.getId())) {
                log.warn("updateEmployee(): failed to migrate employee to old portal, personId={}", person.getId());
                return error(En_ResultStatus.EMPLOYEE_MIGRATION_FAILED);
            }
        }

        return ok(person);
    }

    @Override
    @Transactional
    public Result<WorkerEntry> updateWorkerEntry(AuthToken token, WorkerEntry worker) {
        if (worker == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (isWorkerFrom1C(worker)){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        boolean result = workerEntryDAO.partialMerge(worker, "dep_id", "companyId", "positionId", "active");

        if (result) {
            return ok(worker);
        }

        return error(En_ResultStatus.INTERNAL_ERROR);
    }

    @Override
    @Transactional
    public Result<WorkerEntry> createWorkerEntry(AuthToken token, WorkerEntry worker) {
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
        if(!groupHomeDAO.isHomeCompany( person.getCompanyId() )){
            return error(En_ResultStatus.NOT_AVAILABLE);
        }

        Person personFromDb = personDAO.get(person.getId());

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
                throw new RollbackTransactionException(En_ResultStatus.EMPLOYEE_NOT_FIRED_FROM_THESE_COMPANIES);
            }

            List<UserLogin> userLogins = userLoginDAO.findByPersonId(personFromDb.getId());
            if(isNotEmpty(userLogins)) {
                for (UserLogin userLogin : userLogins) {
                    userLogin.setAdminStateId(En_AdminState.LOCKED.getId());
                    updateAccount(userLogin, token);
                }
            }
        }

        if (portalConfig.data().legacySysConfig().isExportEnabled()) {
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
    public Result<Person> updateWorkerEntries(AuthToken token, Long personId, List<WorkerEntry> workerEntries) {

        if (personId == null || CollectionUtils.isEmpty(workerEntries)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        workerEntries.forEach(workerEntry -> workerEntry.setPersonId(personId));

        final int TO_CREATE = 1;
        final int TO_REMOVE = 2;

        WorkerEntryQuery query = new WorkerEntryQuery(personId);
        List<WorkerEntry> oldWorkerEntries = workerEntryDAO.getWorkers(query);

        Map<WorkerEntry, Integer> finalWorkersMap = new HashMap<>();
        fillMapWorkersToCreate(finalWorkersMap, workerEntries, TO_CREATE);
        fillMapWorkersToRemove(finalWorkersMap, workerEntries, oldWorkerEntries, TO_REMOVE);

        for (Map.Entry<WorkerEntry, Integer> entry : finalWorkersMap.entrySet()) {
            switch (entry.getValue()) {
                case TO_CREATE :
                    Result createResult  = createWorkerEntry(token, entry.getKey());
                    if (createResult.isError()) {
                        throw new RollbackTransactionException(createResult.getStatus(), "Failed to create worker entry, personId=" + personId);
                    }
                    makeAudit(entry.getKey(), En_AuditType.WORKER_CREATE, token);
                    break;
                case TO_REMOVE :
                    Result removeResult  = removeWorkerEntry(entry.getKey());
                    if (removeResult.isError()) {
                        throw new RollbackTransactionException(removeResult.getStatus(), "Failed to remove worker entry, personId=" + personId);
                    }
                    makeAudit(entry.getKey(), En_AuditType.WORKER_REMOVE, token);
                    break;
            }
        }

        return ok(personDAO.get(personId));
    }

    @Override
    public Result<EmployeesBirthdays> getEmployeesBirthdays(AuthToken token, Date dateFrom, Date dateUntil) {
        EmployeeQuery query = new EmployeeQuery();
        query.setFired(false);
        query.setDeleted(false);
        query.setBirthdayInterval(new Interval(dateFrom, dateUntil));
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
        query.setBirthdayInterval(new Interval(from, to));
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

    @Override
    public Result<String> getEmployeeRestVacationDays(AuthToken token, Long employeeId) {
        if (employeeId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        EmployeeShortView employee = employeeShortViewDAO.get(employeeId);

        if (employee == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        jdbcManyRelationsHelper.fill(employee, WORKER_ENTRIES);
        WorkerEntryFacade entryFacade = new WorkerEntryFacade(employee.getWorkerEntries());
        WorkerEntryShortView mainEntry = entryFacade.getMainEntry();

        if (mainEntry == null || mainEntry.getWorkerExtId() == null) {
            return error(En_ResultStatus.EMPLOYEE_NOT_SYNCHRONIZING_WITH_1C);
        }

        return api1CWorkService.getEmployeeRestVacationDays(
                mainEntry.getWorkerExtId(), mainEntry.getCompanyName());
    }

    @Override
    public Result<List<PersonShortView>> getAccountingEmployee(AuthToken token) {
        String contractNotifierDepartmentIds = portalConfig.data().getCommonConfig().getContractAccountingDepartmentIds();
        String contractNotifierIds = portalConfig.data().getCommonConfig().getContractAccountingEmployeeIds();

        List<PersonShortView> accountingEmployees = personShortViewDAO.getAccountingEmployees(
                StringUtils.isNotEmpty(contractNotifierIds) ?
                        Arrays.asList(contractNotifierIds.split(",")) : Collections.emptyList(),
                StringUtils.isNotEmpty(contractNotifierDepartmentIds) ?
                        Arrays.asList(contractNotifierDepartmentIds.split(",")) : Collections.emptyList()
        );

        return ok(accountingEmployees);
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

    private Result<EmployeeShortView> getEmployee(Long employeeId) {

        if (employeeId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        EmployeeShortView employeeShortView = employeeShortViewDAO.get(employeeId);

        if (employeeShortView == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        jdbcManyRelationsHelper.fill(employeeShortView, CONTACT_ITEMS);
        jdbcManyRelationsHelper.fill(employeeShortView, WORKER_ENTRIES);
        employeeShortView.setWorkerEntries(changeCompanyNameIfHidden(employeeShortView.getWorkerEntries()));
        employeeShortView.setCurrentAbsence(personAbsenceDAO.currentAbsence(employeeId));
        employeeShortView.setTimezoneOffset(employeeShortView.getBirthday() == null ? null : employeeShortView.getBirthday().getTimezoneOffset());

        return ok(employeeShortView);
    }

    private EmployeeShortView removeAllPrivacyInfo(EmployeeShortView employeeShortView) {
        List<ContactItem> sensitive = getPrivateContactItems(employeeShortView.getContactInfo().getItems());
        employeeShortView.setContactInfo(new ContactInfo(stream(employeeShortView.getContactInfo().getItems())
                .filter(item -> !sensitive.contains(item))
                .collect(Collectors.toList())));

        employeeShortView.setInn(null);
        return employeeShortView;
    }

    private ContactInfo removeContactPrivacyInfo(ContactInfo contactInfo) {
        List<ContactItem> sensitive = getPrivateContactItems(contactInfo.getItems());
        return new ContactInfo(stream(contactInfo.getItems())
                .filter(item -> !sensitive.contains(item))
                .collect(Collectors.toList()));
    }

    private List<ContactItem> getPrivateContactItems(List<ContactItem> contactItems) {
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

    private WorkerEntry checkAndGetMainWorkerEntry(List<WorkerEntry> newWorkerEntries) {
        Optional<WorkerEntry> mainWorkerEntry = newWorkerEntries.stream().filter(WorkerEntry::isMain).findFirst();
        if (!mainWorkerEntry.isPresent()) {
            newWorkerEntries.get(0).setActiveFlag(1);
        }
        return newWorkerEntries.stream().filter(WorkerEntry::isMain).findFirst().get();
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

        final String USER_SUPPORT_PROJECT_NAME = portalConfig.data().youtrack().getSupportProject();

        youtrackService.createIssue( USER_SUPPORT_PROJECT_NAME, summary, description );
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
        employeeQuery.setBirthdayInterval(person.getBirthday() == null ? null :
                new Interval(person.getBirthday(), person.getBirthday()));

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

        if (!stream(facade.getGeneralPhoneList()).allMatch(phone -> workPhone.matcher(phone.value()).matches())) {
            return false;
        }

        if (!stream(facade.getMobilePhoneList()).allMatch(phone -> mobilePhone.matcher(phone.value()).matches())) {
            return false;
        }

        return true;
    }

    private boolean isEmailExists(Long personId, String email) {

        PersonQuery query = new PersonQuery();
        query.setEmail(email);
        query.setDeleted(false);
        query.setFired(false);
        List<Person> employeeByEmail = personDAO.getPersons(query);

        if (CollectionUtils.isNotEmpty(employeeByEmail)){
            if (personId == null) {
                return true;
            }

            return (employeeByEmail.stream()
                    .anyMatch(personFromDB -> !personFromDB.getId().equals(personId)));
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

    private Result<UserLogin> createLDAPAccount(AuthToken token, Person person) {
        String email = new PlainContactInfoFacade(person.getContactInfo()).getEmail();
        UserLogin userLogin = userLoginDAO.createNewUserLogin(person);
        userLogin.setUlogin(makeLogin(email));
        userLogin.setAuthType(En_AuthType.LDAP);
        userLogin.setAdminStateId(En_AdminState.UNLOCKED.getId());
        userLogin.setRoles(new HashSet<>(userRoleDAO.getDefaultEmployeeRoles()));
        Long userLoginId = userLoginDAO.persist(userLogin);
        if (userLoginId == null) {
            return error(En_ResultStatus.NOT_CREATED);
        }
        jdbcManyRelationsHelper.persist(userLogin, "roles");
        makeAudit(userLogin, En_AuditType.ACCOUNT_CREATE, token);
        return ok(userLogin);
    }

    private String makeLogin(String email) {
        return email.substring(0, email.indexOf("@")).trim();
    }

    private boolean isUserLoginExist(String email) {
        return !userLoginDAO.isUnique(makeLogin(email));
    }

    AbsenceQuery makeAbsenceQuery(Set<Long> employeeIds) {
        Date startOfToday = DateUtils.resetSeconds(new Date());
        return new AbsenceQuery(
                new DateRange(En_DateIntervalType.FIXED, startOfToday, startOfToday),
                employeeIds,
                Arrays.asList(En_AbsenceReason.values()).stream()
                        .filter(En_AbsenceReason::isActual)
                        .collect(Collectors.toSet()));
    }

    private WorkerEntry getMainEntry(List<WorkerEntry> workers) {
        return workers.stream().filter(WorkerEntry::isMain).findFirst().orElse(getFirstEntry(workers));
    }

    private WorkerEntry getFirstEntry(List<WorkerEntry> workers) {
        return workers.stream().findFirst().orElse(null);
    }

    private boolean isHiredAgain(Person oldPerson) {
        return oldPerson.isFired();
    }
}
