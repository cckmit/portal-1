package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.BirthdaysNotificationEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.not;


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
        jdbcManyRelationsHelper.fill(employeeShortView, "workerEntries");

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
        jdbcManyRelationsHelper.fill(employeeShortView, "workerEntries");

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

        if (checkExistEmployee(person)){
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
            return createLDAPAccount(person)
                    .flatMap(userLogin -> {
                        userLogin.setAdminStateId(En_AdminState.UNLOCKED.getId());
                        saveAccount(userLogin, token);
                        return ok();
                    }).map(ignore -> person);
        }

        return error(En_ResultStatus.INTERNAL_ERROR);
    }

    @Override
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

        final boolean YOUTRACK_INTEGRATION_ENABLED = portalConfig.data().integrationConfig().isYoutrackEmployeeSyncEnabled();

        if (needToChangeAccount && YOUTRACK_INTEGRATION_ENABLED) {
            createChangeLastNameYoutrackIssueIfNeeded(person.getId(), person.getFirstName(), person.getLastName(), person.getSecondName(), oldPerson.getLastName());
        }

        person.setDisplayName(person.getLastName() + " " + person.getFirstName() + (StringUtils.isNotEmpty(person.getSecondName()) ? " " + person.getSecondName() : ""));
        person.setDisplayShortName(createPersonShortName(person));

        person.setCompanyId(CrmConstants.Company.HOME_COMPANY_ID);
        boolean success = personDAO.partialMerge(person,  "company_id", "firstname", "lastname", "secondname", "sex", "birthday", "ipaddress", "contactInfo", "displayname", "displayShortName");

        if (success) {
            return ok(true);
        }

        return error(En_ResultStatus.INTERNAL_ERROR);
    }

    @Override
    public Result<Boolean> updateEmployeeWorker(AuthToken token, WorkerEntry worker) {
        if (worker == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (isSyncCompanyWorker(worker)){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        boolean result = workerEntryDAO.partialMerge(worker, "dep_id", "companyId", "positionId", "active");

        if (result) {
            return ok(true);
        }

        return error(En_ResultStatus.INTERNAL_ERROR);
    }

    @Override
    public Result<WorkerEntry> createEmployeeWorker(AuthToken token, WorkerEntry worker) {
        if (worker == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (isSyncCompanyWorker(worker)){
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
        PlainContactInfoFacade contactInfoFacade = new PlainContactInfoFacade(personFromDb.getContactInfo());
        contactInfoFacade.setEmail(null);

        boolean result = personDAO.merge(personFromDb);

        if (result) {
            boolean isRemoved = removeWorkerEntriesByPersonId(personFromDb.getId());

            if (!isRemoved){
                return error(En_ResultStatus.EMPLOYEE_NOT_FIRED_FROM_THESE_COMPANIES);
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

    @Transactional
    @Override
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
                        return error(createResult.getStatus());
                    }
                    break;
                case TO_REMOVE :
                    Result removeStatus  = removeWorkerEntry(entry.getKey());
                    makeAudit(entry.getKey(), En_AuditType.WORKER_REMOVE, token);
                    if (removeStatus.isError()){
                        return error(removeStatus.getStatus());
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

    /**
     * Уведомление о грядущих датах рождения
     * @return
     */
    @Override
    public Result<Boolean> notifyAboutBirthdays() {

        Date from = makeDateWithOffset(-2);
        Date to = makeDateWithOffset(9);

        log.info("notifyAboutBirthdays(): start");

        EmployeeQuery query = new EmployeeQuery();
        query.setFired(false);
        query.setDeleted(false);
        query.setBirthdayRange(new DateRange(En_DateIntervalType.FIXED, from, to));
        List<EmployeeShortView> employees = employeeShortViewDAO.getEmployees(query);

        if (CollectionUtils.isEmpty(employees)) {
            log.info("notifyAboutBirthdays(): employees birthdays list is empty for period {} - {}", from, to);
            return ok(false);
        }

        List<NotificationEntry> notifiers = makeNotificationListFromConfiguration();

        if (CollectionUtils.isEmpty(notifiers)) {
            log.info("notifyAboutBirthdays(): no entries to be notified");
            return ok(false);
        }

        log.info("notifyAboutBirthdays(): birthdays notification: entries to be notified: {}", notifiers);

        publisherService.publishEvent(new BirthdaysNotificationEvent(this, employees, from, to, notifiers));

        log.info("notifyAboutBirthdays(): done");
        return ok(true);
    }

    private Date makeDateWithOffset(int dayOffset) {
        LocalDate localDate = LocalDate.now().plusDays(dayOffset);
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
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

    private void updateAccount(UserLogin userLogin, AuthToken token) {
        if (userLoginDAO.saveOrUpdate(userLogin)) {
            jdbcManyRelationsHelper.persist( userLogin, "roles" );
            makeAudit(userLogin, En_AuditType.ACCOUNT_MODIFY, token);
        }
    }

    private void fillMapWorkersToRemove(Map<WorkerEntry, Integer> finalWorkersMap, List<WorkerEntry> newWorkerEntries, List<WorkerEntry> oldWorkerEntries, int TO_REMOVE) {
        for (WorkerEntry worker : oldWorkerEntries) {
            boolean isActualWorkerEntry = false;

            if (isSyncCompanyWorker(worker)){
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

            if (isSyncCompanyWorker(workerEntry)){
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

        WorkerEntry activeWorker = workers == null ? null : workers.stream().filter(WorkerEntry::isMain).findFirst().orElse(null);

        Person person = personDAO.get(personId);

        if (activeWorker == null || person == null){
            log.warn("updateEmployeeInOldPortal(): activeWorker={}, person={}", activeWorker, person);
            return false;
        }

        return migrationManager.saveExternalEmployee(person, activeWorker.getDepartmentName(), activeWorker.getPositionName()).equals(En_ResultStatus.OK);
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
            if (isSyncCompanyWorker(worker)){
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
        if (isSyncCompanyWorker(worker)){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        boolean result = workerEntryDAO.remove(worker);

        return result ? ok(result) : error(En_ResultStatus.INTERNAL_ERROR);
    }

    private boolean isSyncCompanyWorker (WorkerEntry worker){
        return !companyDAO.getAllHomeCompanyIdsWithoutSync().contains(worker.getCompanyId());
    }

    private boolean checkExistEmployee (Person person){
        EmployeeQuery employeeQuery = new EmployeeQuery();
        employeeQuery.setFired(false);
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

    private List<WorkerEntryShortView> changeCompanyNameIfHidden(List<WorkerEntryShortView> list){
        list.forEach(workerEntry ->
                workerEntry.setCompanyName(workerEntry.getCompanyIsHidden() != null && workerEntry.getCompanyIsHidden()
                        ? CrmConstants.Company.MAIN_HOME_COMPANY_NAME
                        : workerEntry.getCompanyName())
                );
        return list;
    }

    private Result<UserLogin> createLDAPAccount(Person person) {

        ContactItem email = person.getContactInfo().findFirst(En_ContactItemType.EMAIL, En_ContactDataAccess.PUBLIC);
        if (!email.isEmpty() && HelperFunc.isNotEmpty(email.value())) {
            String login = email.value().substring(0, email.value().indexOf("@"));
            if (!userLoginDAO.isUnique(login.trim())) {
                log.debug("error: Login already exist.");
                return error(En_ResultStatus.ALREADY_EXIST);
            }

            UserLogin userLogin = userLoginDAO.createNewUserLogin(person);
            userLogin.setUlogin(login.trim());
            userLogin.setAuthType(En_AuthType.LDAP);
            userLogin.setRoles(new HashSet<>(userRoleDAO.getDefaultEmployeeRoles()));
            return ok(userLogin);
        }
        return error(En_ResultStatus.INCORRECT_PARAMS);
    }

    private void saveAccount(UserLogin userLogin, AuthToken authToken) {
        if (userLoginDAO.saveOrUpdate(userLogin)) {
            jdbcManyRelationsHelper.persist( userLogin, "roles" );
            makeAudit(userLogin, En_AuditType.ACCOUNT_CREATE, authToken);
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
}
