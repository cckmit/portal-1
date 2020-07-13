package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.WorkerEntryQuery;
import ru.protei.portal.core.model.struct.AuditObject;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.*;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;
import ru.protei.winter.jdbc.JdbcSort;

import javax.annotation.PostConstruct;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.portal.core.model.helper.StringUtils.join;


/**
 * Реализация сервиса управления сотрудниками
 */
public class EmployeeServiceImpl implements EmployeeService {

    private static Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);
    private static String ADMIN_PROJECT_NAME, PORTAL_URL;
    private static boolean YOUTRACK_INTEGRATION_ENABLED;

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
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Autowired
    PortalConfig portalConfig;

    @Autowired
    YoutrackService youtrackService;

    @Autowired
    LegacySystemDAO migrationManager;

    @Autowired
    AuditObjectDAO auditObjectDAO;

    @PostConstruct
    public void setYoutrackConst() {
        YOUTRACK_INTEGRATION_ENABLED = portalConfig.data().integrationConfig().isYoutrackEmployeeSyncEnabled();
        ADMIN_PROJECT_NAME = portalConfig.data().youtrack().getAdminProject();
        PORTAL_URL = portalConfig.data().getCommonConfig().getCrmUrlInternal();
    }

    @Override
    public EmployeeDetailView getEmployeeAbsences(Long id, Long tFrom, Long tTill, Boolean isFull) {

        Person p = personDAO.getEmployee( id );
        if (p == null) {
            return null;
        }

        List<PersonAbsence> personAbsences = absenceDAO.getForRange(id, new Date(tFrom), new Date(tTill));
        if(isFull){
            fillAbsencesOfCreators(personAbsences);
        }

        return new EmployeeDetailView().fill(personAbsences, isFull);
    }

    public EmployeeDetailView getEmployeeProfile(Long id){

        Person p = personDAO.getEmployee( id );
        if (p == null) {
            return null;
        }

        EmployeeDetailView view = new EmployeeDetailView().fill(p);

        view.fill(absenceDAO.getForRange(p.getId(), null, null), false);

        return view;
    }

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
    public Result<PersonShortView> getEmployee(AuthToken token, Long employeeId) {

        if (employeeId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Person person = personDAO.get(employeeId);
        if(person==null) return error(En_ResultStatus.NOT_FOUND);
        return ok(person.toFullNameShortView());
    }

    @Override
    public Result<SearchResult<EmployeeShortView>> employeeList(AuthToken token, EmployeeQuery query) {

        SearchResult<EmployeeShortView> sr = employeeShortViewDAO.getSearchResult(query);
        List<EmployeeShortView> results = sr.getResults();

        if (CollectionUtils.isNotEmpty(results)) {
            List<Long> employeeIds = results.stream().map(e -> e.getId()).collect(Collectors.toList());
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
            List<Long> employeeIds = results.stream().map(EmployeeShortView::getId).collect(Collectors.toList());
            List<WorkerEntryShortView> workerEntries = changeCompanyNameIfHidden(workerEntryShortViewDAO.listByPersonIds(employeeIds));

            results.forEach(employee ->
                    employee.setWorkerEntries(workerEntries.stream()
                            .filter(workerEntry -> workerEntry.getPersonId().equals(employee.getId()))
                            .collect(Collectors.toList()))
            );
        }
        return ok(sr);
    }

    @Override
    public Result<EmployeeShortView> getEmployeeShortView(AuthToken token, Long employeeId) {

        if (employeeId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        EmployeeShortView employeeShortView = employeeShortViewDAO.get(employeeId);
        jdbcManyRelationsHelper.fill(employeeShortView, "workerEntries");

        return ok(employeeShortView);
    }

    @Override
    public Result<EmployeeShortView> getEmployeeShortViewWithChangedHiddenCompanyNames(AuthToken token, Long employeeId) {

        if (employeeId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        EmployeeShortView employeeShortView = employeeShortViewDAO.get(employeeId);
        jdbcManyRelationsHelper.fill(employeeShortView, "workerEntries");

        employeeShortView.setWorkerEntries(changeCompanyNameIfHidden(employeeShortView.getWorkerEntries()));

        return ok(employeeShortView);
    }

    @Override
    public Result<List<WorkerView>> list(String param) {

        Company our_comp = companyDAO.get(CrmConstants.Company.HOME_COMPANY_ID);

        param = HelperFunc.makeLikeArg(param, true);

        JdbcSort sort = new JdbcSort(JdbcSort.Direction.ASC, "displayName");

        List<WorkerView> result = personDAO.getListByCondition("company_id=? and isdeleted=? and displayName like ?", sort, our_comp.getId(), 0, param)
                .stream().map(p -> new WorkerView(p, our_comp))
                .collect(Collectors.toList());

        return ok(result);
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

            UserLogin userLogin = userLoginDAO.findByPersonId(personFromDb.getId());
            if(userLogin != null) {
                userLogin.setAdminStateId(En_AdminState.LOCKED.getId());
                updateAccount(userLogin, token);
            }
        }

        if (portalConfig.data().legacySysConfig().isImportEmployeesEnabled()) {
            if (!fireEmployeeInOldPortal(personFromDb)) {
                log.warn("fireEmployee(): fail to migrate employee to old portal. Person={}", personFromDb);
                return error(En_ResultStatus.EMPLOYEE_MIGRATION_FAILED);
            }
        }

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

        String description = "Карточка сотрудника: " + "[" + employeeNewFullName + "](" + PORTAL_URL + "#employee_preview:id=" + employeeId + ")" + "\n" +
                "Старое ФИО: " + employeeOldFullName + "\n" +
                "Новое ФИО: " + employeeNewFullName + "\n" +
                "\n" +
                "Необходимо изменение учетной записи, почты.";

        youtrackService.createIssue( ADMIN_PROJECT_NAME, summary, description );
    }

    private void createFireEmployeeYoutrackIssue(Person person) {

        String employeeFullName = person.getLastName() + " " + person.getFirstName() + " " + (person.getSecondName() != null ? person.getSecondName() : "");

        String summary = "Увольнение сотрудника " + employeeFullName;

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
        employeeQuery.setBirthday(person.getBirthday());

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

    private void fillAbsencesOfCreators(List<PersonAbsence> personAbsences){
        if(personAbsences.size()==0)
            return;

        List<Long> ids = personAbsences.stream()
                .map(p -> p.getCreatorId())
                .distinct()
                .collect(Collectors.toList());

        HashMap<Long,String> creators = new HashMap<>();

        for (Person p : personDAO.partialGetListByKeys(ids, "displayShortName")){
            creators.put(p.getId(), p.getDisplayShortName());
        }

        for(PersonAbsence p:personAbsences)
            p.setCreator(creators.get(p.getCreatorId()));
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
}
