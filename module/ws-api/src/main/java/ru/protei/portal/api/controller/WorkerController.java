package ru.protei.portal.api.controller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;
import protei.sql.query.Tm_SqlQueryHelper;
import ru.protei.portal.api.config.WSConfig;
import ru.protei.portal.api.model.*;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.api.struct.Workers;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.query.WorkerEntryQuery;
import ru.protei.portal.core.model.struct.*;
import ru.protei.portal.core.service.WorkerEntryService;
import ru.protei.portal.core.service.YoutrackService;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.utils.SessionIdGen;
import ru.protei.portal.tools.migrate.HelperService;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;
import ru.protei.portal.util.AuthUtils;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.PhoneUtils.normalizePhoneNumber;

@RestController
@RequestMapping(value = "/api/worker")
public class WorkerController {

    private static Logger logger = LoggerFactory.getLogger(WorkerController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private SessionIdGen sidGen;

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private CompanyDAO companyDAO;

    @Autowired
    private CompanyGroupHomeDAO companyGroupHomeDAO;

    @Autowired
    private CompanyDepartmentDAO companyDepartmentDAO;

    @Autowired
    private UserLoginDAO userLoginDAO;

    @Autowired
    private UserRoleDAO userRoleDAO;

    @Autowired
    private WorkerPositionDAO workerPositionDAO;

    @Autowired
    private WorkerEntryDAO workerEntryDAO;

    @Autowired
    private EmployeeRegistrationDAO employeeRegistrationDAO;

    @Autowired
    ContactItemDAO contactItemDAO;

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Autowired
    LegacySystemDAO migrationManager;

    @Autowired
    AuditObjectDAO auditObjectDAO;

    @Autowired
    PortalConfig portalConfig;

    @Autowired
    YoutrackService youtrackService;

    @Autowired
    WorkerEntryService workerEntryService;

    /**
     * Получить данные о физическом лице
     * @param id идентификатор физического лица на портале
     * @return Result<WorkerRecord>
     */
    @RequestMapping(method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_XML_VALUE,
                    value = "/get.person")
    Result<WorkerRecord> getPerson(@RequestParam(name = "id") Long id,
                           HttpServletRequest request,
                           HttpServletResponse response) {

        logger.debug("getPerson(): id={}", id);

        if (!checkAuth(request, response)) return error(En_ResultStatus.INVALID_LOGIN_OR_PWD);

        try {
            Person person = personDAO.get(id);
            jdbcManyRelationsHelper.fill(person, Person.Fields.CONTACT_ITEMS);
            return ok(new WorkerRecord(person));
        } catch (Throwable e) {
            logger.error("error while get worker", e);
            return error(En_ResultStatus.INTERNAL_ERROR,  e.getMessage());
        }
    }

    /**
     * Получить данные о сотруднике
     * @param id идентификатор сотрудника в 1С
     * @param companyCode код компании
     * @return Result<WorkerRecord>
     */
    @RequestMapping(method = RequestMethod.GET,
                   produces = MediaType.APPLICATION_XML_VALUE,
                   value = "/get.worker")
    Result<WorkerRecord> getWorker(@RequestParam(name = "id") String id, @RequestParam(name = "companyCode") String companyCode,
                           HttpServletRequest request,
                           HttpServletResponse response) {

        logger.debug("getWorker(): id={}, companyCode={}", id, companyCode);

        if (!checkAuth(request, response)) return error(En_ResultStatus.INVALID_LOGIN_OR_PWD);

        try {
            return withHomeCompany(companyCode,
                    item -> {
                        WorkerEntry entry = workerEntryDAO.getByExternalId(id.trim(), item.getCompanyId());
                        Person person = personDAO.get(entry.getPersonId());
                        jdbcManyRelationsHelper.fill(person, Person.Fields.CONTACT_ITEMS);
                        EmployeeRegistration registration = employeeRegistrationDAO.getByPersonId(entry.getPersonId());
                        return  ok(new WorkerRecord(person, entry, registration));
                    });

        } catch (NullPointerException e){
            logger.error("error while get worker = {}", En_ErrorCode.UNKNOWN_WOR.getMessage());
            return error(En_ResultStatus.INCORRECT_PARAMS,  En_ErrorCode.UNKNOWN_WOR.getMessage());
        }
        catch (Throwable e) {
            logger.error("error while get worker", e);
            return error(En_ResultStatus.INTERNAL_ERROR,  e.toString());
        }
    }

    /**
     * Получить данные об отделе
     * @param id идентификатор отдела в 1С
     * @param companyCode код компании
     * @return Result<DepartmentRecord>
     */
    @RequestMapping(method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_XML_VALUE,
                    value = "/get.department")
    Result<DepartmentRecord> getDepartment(@RequestParam(name = "id") String id, @RequestParam(name = "companyCode") String companyCode,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

        logger.debug("getDepartment(): id={}, companyCode={}", id, companyCode);

        if (!checkAuth(request, response)) return error(En_ResultStatus.INVALID_LOGIN_OR_PWD);

        try {

            return withHomeCompany(companyCode,
                    item -> ok(new DepartmentRecord(companyDepartmentDAO.getByExternalId(id, item.getCompanyId()))));

        } catch (Exception e) {
            logger.error("error while get department", e);
            return error(En_ResultStatus.INTERNAL_ERROR,  e.getMessage());
        }
    }

    /**
     * Получить список физических лиц
     * @param expr строка для поиска с использованием шаблонных символов
     * @return Result<WorkerRecordList>
     */
    @RequestMapping(method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_XML_VALUE,
                    value = "/get.persons")
    Result<WorkerRecordList> getPersons(@RequestParam(name = "expr") String expr,
                                HttpServletRequest request,
                                HttpServletResponse response) {

        logger.debug("getPersons(): expr={}", expr);

        if (!checkAuth(request, response)) return error(En_ResultStatus.INVALID_LOGIN_OR_PWD);

        WorkerRecordList persons = new WorkerRecordList();

        try {
            EmployeeQuery query = new EmployeeQuery(Tm_SqlQueryHelper.makeLikeArgEx(expr.trim()), En_SortField.person_full_name, En_SortDir.ASC);
            query.setDeleted(false);
            stream(personDAO.getEmployees(query))
                .map(person -> {
                    jdbcManyRelationsHelper.fill(person, Person.Fields.CONTACT_ITEMS);
                    return person;
                })
                .forEach(person -> persons.append(new WorkerRecord(person)));

        } catch (Exception e) {
            logger.error("error while get persons", e);
            return error(En_ResultStatus.INTERNAL_ERROR,  e.getMessage());
        }
        return ok(persons);
    }

    /**
     * Добавить сотрудника
     * @param rec данные о сотруднике
     * @return Result<Long>
     */
    @RequestMapping(method = RequestMethod.POST,
                    consumes = MediaType.APPLICATION_XML_VALUE,
                    produces = MediaType.APPLICATION_XML_VALUE,
                    value = "/add.worker")
    Result<Long> addWorker(@RequestBody WorkerRecord rec,
                            HttpServletRequest request,
                            HttpServletResponse response) {

        logger.debug("addWorker(): rec={}", rec);

        if (!checkAuth(request, response)) return error(En_ResultStatus.INVALID_LOGIN_OR_PWD);

        Result<Long> isValid = isValidWorkerRecord(rec);
        if (isValid.isError()) {
            logger.debug("error result: {}", isValid.getMessage());
            return isValid;
        }

        if (rec.isDeleted() || rec.isFired()) {
            logger.debug("error result: {}", En_ErrorCode.DELETED_OR_FIRED_RECORD.getMessage());
            return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.DELETED_OR_FIRED_RECORD.getMessage());
        }

        try {

            OperationData operationData = new OperationData(rec)
                    .requireHomeItem()
                    .requireDepartment(null)
                    .requireNotExistsWorker()
                    .requireAccount(null)
                    .requireRegistration(null);

            if (!operationData.isValid())
                return operationData.failResult();

            return transactionTemplate.execute(transactionStatus -> {

                try {

                    Person person = null;
                    if (rec.getId() != null) {
                        person = personDAO.get(rec.getId());
                        if (person == null) {
                            person = personDAO.findEmployeeByParameters(
                                    rec.getFirstName().trim(),
                                    rec.getLastName().trim(),
                                    HelperFunc.isEmpty(rec.getBirthday()) ? null : HelperService.DATE.parse(rec.getBirthday()));
                        }
                        if (person != null) {
                            jdbcManyRelationsHelper.fill(person, Person.Fields.CONTACT_ITEMS);
                        }
                    }

                    if (person == null) {
                        person = personDAO.createNewPerson(operationData.homeItem().getMainId());
                    }

                    convert(rec, person);

                    String email = new PlainContactInfoFacade(person.getContactInfo()).getEmail();
                    if (isEmailExists(person.getId(), email)){
                        logger.debug("addWorker(): worker with email={} already exists", email);
                        return error(En_ResultStatus.EMPLOYEE_EMAIL_ALREADY_EXIST, En_ErrorCode.EMAIL_ALREADY_EXIST.getMessage());
                    }

                    person.setFired(false);
                    person.setDeleted(false);

                    if (person.getId() == null) {
                        persistPerson(person);
                        logger.debug("created person with id={}", person.getId());
                    } else {
                        mergePerson(person);
                    }

                    List<UserLogin> userLogins = operationData.account();
                    if (isEmpty(userLogins)) {

                        Result<UserLogin> userLoginResult = createLDAPAccount(person);
                        if (userLoginResult.isError()){
                            return error(userLoginResult.getStatus(), userLoginResult.getMessage());
                        }

                        UserLogin userLogin = userLoginResult.getData();
                        if (userLogin != null) {
                            userLogin.setAdminStateId(En_AdminState.UNLOCKED.getId());
                            saveAccount(userLogin);
                        }
                    } else {
                        for (UserLogin userLogin : userLogins) {
                            userLogin.setAdminStateId(En_AdminState.UNLOCKED.getId());
                            saveAccount(userLogin);
                        }
                    }

                    EmployeeRegistration employeeRegistration = operationData.registration();
                    if (employeeRegistration != null) {
                        checkRegistrationByPerson(person.getId());
                        employeeRegistration.setPerson(person);
                        mergeEmployeeRegistration(employeeRegistration);
                    }

                    WorkerPosition position = getValidPosition(rec.getPositionName(), operationData.homeItem().getCompanyId());

                    WorkerEntry worker = new WorkerEntry();
                    worker.setCreated(new Date());
                    worker.setPersonId(person.getId());
                    worker.setCompanyId(operationData.homeItem().getCompanyId());
                    worker.setDepartmentId(operationData.department().getId());
                    worker.setPositionId(position.getId());
                    worker.setHireDate(HelperFunc.isNotEmpty(rec.getHireDate()) ? HelperService.DATE.parse(rec.getHireDate()) : null);
                    worker.setHireOrderNo(HelperFunc.isNotEmpty(rec.getHireOrderNo()) ? rec.getHireOrderNo().trim() : null);
                    worker.setActiveFlag(rec.getActive());
                    worker.setExternalId(rec.getWorkerId().trim());
                    worker.setContractAgreement(false);

                    persistWorker(worker);

                    if (WSConfig.getInstance().isEnableMigration()) {
                        Workers workers = new Workers(workerEntryDAO.getWorkers(new WorkerEntryQuery(person.getId())));
                        String departmentName = worker.getActiveFlag() == 1 ? operationData.department().getName() : workers.getActiveDepartment(operationData.department().getName());
                        String positionName = worker.getActiveFlag() == 1 ? position.getName() : workers.getActivePosition(position.getName());
                        migrationManager.saveExternalEmployee(person, departmentName, positionName);
                    }


                    logger.debug("success result, workerRowId={}", worker.getId());
                    return ok(person.getId());

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            logger.error("error while add worker's record", e);
            //return error(En_ResultStatus.INTERNAL_ERROR,  e.getMessage());
        }

        return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.NOT_CREATE.getMessage());
    }

    /**
     * Обновить сотрудника
     * @param rec данные о сотруднике
     * @return Result<Long>
     */
    @RequestMapping(method = RequestMethod.PUT,
                    consumes = MediaType.APPLICATION_XML_VALUE,
                    produces = MediaType.APPLICATION_XML_VALUE,
                    value = "/update.worker")
    Result<Long> updateWorker(@RequestBody WorkerRecord rec,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        logger.debug("updateWorker(): rec={}", rec);

        if (!checkAuth(request, response)) return error(En_ResultStatus.INVALID_LOGIN_OR_PWD);

        return update(rec);
    }

    /**
     * Обновить сотрудников
     * @param list список сотрудников
     * @return ResultList
     */
    @RequestMapping(method = RequestMethod.PUT,
                    consumes = MediaType.APPLICATION_XML_VALUE,
                    produces = MediaType.APPLICATION_XML_VALUE,
                    value = "/update.workers")
    ResultList updateWorkers(@RequestBody WorkerRecordList list,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {

        logger.debug("updateWorkers(): list={}", list);

        ResultList results = new ResultList();

        if (!checkAuth(request, response))  {
            results.append(error(En_ResultStatus.INVALID_LOGIN_OR_PWD));
            return results;
        };

        try {

            list.getWorkerRecords().forEach(
                    p -> results.append(update(p))
            );

        } catch (Exception e) {
            logger.error("error while update workers", e);
        }
        return results;
    }

    /**
     * Обновить даты увольнения
     * @param list список сотрудников
     * @return ResultList
     */
    @RequestMapping(method = RequestMethod.PUT,
                    consumes = MediaType.APPLICATION_XML_VALUE,
                    produces = MediaType.APPLICATION_XML_VALUE,
                    value = "/update.fire.dates")
    ResultList updateFireDates(@RequestBody WorkerRecordList list,
                             HttpServletRequest request,
                             HttpServletResponse response) {

        logger.debug("updateFireDates(): list={}", list);

        ResultList results = new ResultList();

        if (!checkAuth(request, response))  {
            results.append(error(En_ResultStatus.INVALID_LOGIN_OR_PWD));
            return results;
        };

        try {

            list.getWorkerRecords().forEach(
                    p -> results.append(updateDate(p))
            );

        } catch (Exception e) {
            logger.error("error while update fire dates", e);
        }
        return results;
    }

    /**
     * Обновить дату увольнения
     * @param rec данные о сотруднике
     * @return Result<Long>
     */
    @RequestMapping(method = RequestMethod.PUT,
                    consumes = MediaType.APPLICATION_XML_VALUE,
                    produces = MediaType.APPLICATION_XML_VALUE,
                    value = "/update.fire.date")
    Result<Long> updateFireDate(@RequestBody WorkerRecord rec,
                              HttpServletRequest request,
                              HttpServletResponse response) {

        logger.debug("updateFireDate(): rec={}", rec);

        if (!checkAuth(request, response)) return error(En_ResultStatus.INVALID_LOGIN_OR_PWD);

        return updateDate(rec);
    }

    /**
     * Удалить сотрудника
     * @param externalId идентификатор сотрудника в 1С
     * @param companyCode код компании
     * @return Result<Long>
     */
    @RequestMapping(method = RequestMethod.DELETE,
                    produces = MediaType.APPLICATION_XML_VALUE,
                    value = "/delete.worker")
    Result<Long> deleteWorker(@RequestParam(name = "externalId") String externalId, @RequestParam(name = "companyCode") String companyCode,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        logger.debug("deleteWorker(): externalId={}, companyCode={}", externalId, companyCode);

        if (!checkAuth(request, response)) return error(En_ResultStatus.INVALID_LOGIN_OR_PWD);

        try {

            OperationData operationData = new OperationData(companyCode, null, externalId)
                    .requireHomeItem()
                    .requireWorker(null);

            if (!operationData.isValid())
                return operationData.failResult();

            return transactionTemplate.execute(transactionStatus -> {
                try {

                    WorkerEntry worker = operationData.worker();
                    Long personId = worker.getPersonId();
                    List<UserLogin> userLogins = userLoginDAO.findLDAPByPersonId(personId);

                    removeWorker(worker);

                    if (!workerEntryDAO.checkExistsByPersonId(personId)) {
                        Person person = personDAO.get(personId);
                        jdbcManyRelationsHelper.fill(person, Person.Fields.CONTACT_ITEMS);
                        person.setDeleted(true);
                        person.setIpAddress(person.getIpAddress() == null ? null : person.getIpAddress().replace(".", "_"));

                        mergePerson(person);

                        if(isNotEmpty(userLogins)) {
                            for (UserLogin userLogin : userLogins) {
                                removeAccount(userLogin);
                            }
                        }

                        if (WSConfig.getInstance().isEnableMigration()) {
                            migrationManager.deleteExternalEmployee(person);
                        }
                    }
                    logger.debug("success result, workerRowId={}", worker.getId());
                    return ok(worker.getId());

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            logger.error("error while remove worker's record", e);
        }

        return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.NOT_DELETE.getMessage());
    }

    /**
     * Получить фотографию сотрудника
     * @param id идентификатор физического лица
     */
    @RequestMapping(method = RequestMethod.GET,
                    produces = MediaType.IMAGE_JPEG_VALUE,
                    value = "/get.photo/{id}")
    public void getPhoto (@PathVariable("id") Long id,
                          HttpServletResponse response,
                          HttpServletRequest request)  {

        logger.debug("getPhoto(): id = {}", id);

        if (!checkAuth(request, response)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            logger.debug("getPhoto(): 403 FORBIDDEN. Bad password or login");
            return;
        }

        if (id == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.debug("getPhoto(): 400 BAD_REQUEST. id is null");
            return;
        }

        Path photoPath = Paths.get(makeFileName(id));

        if (!Files.exists(photoPath)){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            logger.debug("getPhoto(): 404 NOT_FOUND. Photo not found");
            return;
        }

        try (InputStream in = Files.newInputStream(photoPath)){

            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            IOUtils.copy(in, response.getOutputStream());
            logger.debug("getPhoto(): success result, photo path: {}", photoPath);

        } catch (Exception e) {
            logger.error("getPhoto(): error while getting photo", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        } finally {
            try {
                response.getOutputStream().flush();
                response.getOutputStream().close();
                logger.debug("getPhoto(): success closing streams");

            } catch (Exception e){
                logger.error("getPhoto(): can't close stream");
            }
        }
    }

    /**
     * Обновить фотографию сотрудника
     * @param id идентификатор физического лица
     * @param photoBytes - фотография в виде байтового массива в теле запроса
     */

    @RequestMapping(method = RequestMethod.PUT,
                    consumes = MediaType.IMAGE_JPEG_VALUE,
                    value = "/update.photo/{id}")
    public void updatePhoto (@PathVariable("id") Long id, @RequestBody byte[] photoBytes,
                          HttpServletResponse response,
                          HttpServletRequest request)  {

        logger.debug("updatePhoto(): id = {}", id);

        if (!checkAuth(request, response)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            logger.debug("updatePhoto(): 403 FORBIDDEN. Bad password or login");
            return;
        }

        if (id == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.debug("updatePhoto(): 400 BAD_REQUEST. id is null");
            return;
        }

        if (photoBytes == null || photoBytes.length == 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.debug("updatePhoto(): 400 BAD_REQUEST. {}", En_ErrorCode.EMPTY_PHOTO_CONTENT.getMessage());
        }

        try {

            OperationData operationData = new OperationData(null, id, null, null)
                    .requirePerson(null);

            if (!operationData.isValid()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                logger.debug("updatePhoto(): 404 NOT_FOUND. Person not found");
                return;
            }

            Files.write(Paths.get(makeFileName(id)), photoBytes);

            String base64Photo = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(makeFileName(id))));
            Photo photo = new Photo();
            photo.setId(id);
            photo.setContent(base64Photo);

            makeAudit(photo, En_AuditType.PHOTO_UPLOAD);

            logger.debug("updatePhoto(): success result, personId={}", photo.getId());

        } catch (Exception e) {
            logger.error("updatePhoto(): error while updating photo", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        } finally {
            try {
                response.getOutputStream().flush();
                response.getOutputStream().close();
                logger.debug("updatePhoto(): success closing response stream");

            } catch (Exception e){
                logger.error("updatePhoto(): can't close stream");
            }
        }
    }

    /**
     * Создать/обновить отдел
     * @param rec данные об отделе
     * @return Result<Long>
     */
    @RequestMapping(method = RequestMethod.PUT,
                    consumes = MediaType.APPLICATION_XML_VALUE,
                    produces = MediaType.APPLICATION_XML_VALUE,
                    value = "/update.department")
    Result<Long> updateDepartment(@RequestBody DepartmentRecord rec,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

        logger.debug("updateDepartment(): rec={}", rec);

        if (!checkAuth(request, response)) return error(En_ResultStatus.INVALID_LOGIN_OR_PWD);

        Result<Long> isValid = isValidDepartmentRecord(rec);
        if (isValid.isError()) {
            logger.debug("error result: " + isValid.getMessage());
            return isValid;
        }

        try {

            OperationData operationData = new OperationData(rec)
                    .requireHomeItem()
                    .requireParentDepartment(null)
                    .requireHeadDepartment(null)
                    .requireDepartment(CompanyDepartment::new);

            if (!operationData.isValid())
                return operationData.failResult();

            CompanyDepartment department = operationData.department();
            department.setName(rec.getDepartmentName().trim());
            department.setParentId(operationData.parentDepartment() == null ? null : operationData.parentDepartment().getId());
            department.setHeadId(operationData.headDepartment() == null ? null : operationData.headDepartment().getId());

            if (department.getId() == null) {
                department.setCreated(new Date());
                department.setCompanyId(operationData.homeItem().getCompanyId());
                department.setExternalId(rec.getDepartmentId().trim());
                persistDepartment(department);
            } else {
                mergeDepartment(department);
            }

            logger.debug("success result, departmentRowId={}", department.getId());
            return ok(department.getId());

        } catch (Exception e) {
            logger.error("error while update department's record", e);
        }

        return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.NOT_UPDATE.getMessage());
    }

    /**
     * Удалить отдел
     * @param externalId идентификатор отдела в 1С
     * @param companyCode код компании
     * @return Result<Long>
     */
    @RequestMapping(method = RequestMethod.DELETE,
                    produces = MediaType.APPLICATION_XML_VALUE,
                    value = "/delete.department")
    Result<Long> deleteDepartment(@RequestParam(name = "externalId") String externalId, @RequestParam(name = "companyCode") String companyCode,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

        logger.debug("deleteDepartment(): externalId={}, companyCode={}", externalId, companyCode);

        if (!checkAuth(request, response)) return error(En_ResultStatus.INVALID_LOGIN_OR_PWD);

        try {

            OperationData operationData = new OperationData(companyCode, externalId, null)
                    .requireHomeItem()
                    .requireDepartment(null)
                    .requireNotExistsChildDepartment()
                    .requireNotExistsDepartmentWorker();

            if (!operationData.isValid())
                return operationData.failResult();

            CompanyDepartment department = operationData.department();
            removeDepartment(department);

            logger.debug("success result, departmentRowId={}", department.getId());
            return ok(department.getId());

        } catch (Exception e) {
            logger.error("error while remove department's record", e);
        }

        return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.NOT_DELETE.getMessage());
    }

    /**
     * Обновить должность
     * @param oldName наименование должности
     * @param newName новое наименование должности
     * @param companyCode код компании
     * @return Result<Long>
     */
    @RequestMapping(method = RequestMethod.PUT,
                    produces = MediaType.APPLICATION_XML_VALUE,
                    value = "/update.position")
    Result<Long> updatePosition(@RequestParam(name = "oldName") String oldName, @RequestParam(name = "newName")
            String newName, @RequestParam(name = "companyCode") String companyCode,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        logger.debug("updatePosition(): oldName={}, newName={}, companyCode={}", oldName, newName, companyCode);

        if (!checkAuth(request, response)) return error(En_ResultStatus.INVALID_LOGIN_OR_PWD);

        if (HelperFunc.isEmpty(oldName) || HelperFunc.isEmpty(newName)) {
            logger.debug("error result: " + En_ErrorCode.EMPTY_POS.getMessage());
            return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.EMPTY_POS.getMessage());
        }

        try {

            OperationData operationData = new OperationData(companyCode, null, oldName, newName)
                    .requireHomeItem()
                    .requirePosition(null)
                    .requireNotExistsNewPosition();

            if (!operationData.isValid())
                return operationData.failResult();

            WorkerPosition position = operationData.position();
            position.setName(newName.trim());

            mergePosition(position);

            logger.debug("success result, positionRowId={}", position.getId());
            return ok(position.getId());

        } catch (Exception e) {
            logger.error("error while update position's record", e);
        }

        return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.NOT_UPDATE.getMessage());
    }

    /**
     * Удалить должность
     * @param name наименование должности
     * @param companyCode код компании
     * @return Result<Long>
     */
    @RequestMapping(method = RequestMethod.DELETE,
                    produces = MediaType.APPLICATION_XML_VALUE,
                    value = "/delete.position")
    Result<Long> deletePosition(@RequestParam(name = "name") String name, @RequestParam(name = "companyCode") String companyCode,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        logger.debug("deletePosition(): name={}, companyCode={}", name, companyCode);

        if (!checkAuth(request, response)) return error(En_ResultStatus.INVALID_LOGIN_OR_PWD);

        try {

            OperationData operationData = new OperationData(companyCode, null, name, null)
                    .requireHomeItem()
                    .requirePosition(null)
                    .requireNotExistsPositionWorker();

            if (!operationData.isValid())
                return operationData.failResult();

            WorkerPosition position = operationData.position();
            removePosition(position);

            logger.debug("success result, positionRowId={}", position.getId());
            return ok(position.getId());

        } catch (Exception e) {
            logger.error("error while remove position's record", e);
        }

        return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.NOT_DELETE.getMessage());
    }

    private <R> R withHomeCompany(String companyCode, Function<CompanyHomeGroupItem, R> func) throws Exception {
        CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(companyCode.trim());
        return item == null ? null : func.apply(item);
    }

    private Result<Long> isValidWorkerRecord(WorkerRecord rec) {

        if (HelperFunc.isEmpty(rec.getCompanyCode())) {
            return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.EMPTY_COMP_CODE.getMessage());
        }

        if (HelperFunc.isEmpty(rec.getDepartmentId())) {
            return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.EMPTY_DEP_ID.getMessage());
        }

        if (HelperFunc.isEmpty(rec.getPositionName())) {
            return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.EMPTY_POS.getMessage());
        }

        if (HelperFunc.isEmpty(rec.getWorkerId())) {
            return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.EMPTY_WOR_ID.getMessage());
        }

        if (!rec.getWorkerId().trim().matches("^\\S{1,30}$")) {
            return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.INV_FORMAT_WOR_CODE.getMessage());
        }

        if (HelperFunc.isEmpty(rec.getFirstName())) {
            return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.EMPTY_FIRST_NAME.getMessage());
        }

        if (HelperFunc.isEmpty(rec.getLastName())) {
            return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.EMPTY_LAST_NAME.getMessage());
        }

        if (HelperFunc.isNotEmpty(rec.getIpAddress()) &&
                !rec.getIpAddress().trim().matches("^[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}$")) {
            return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.INV_FORMAT_IP.getMessage());
        }

        return ok(rec.getId());
    }

    private Result<Long> isValidDepartmentRecord(DepartmentRecord rec) {

        if (HelperFunc.isEmpty(rec.getCompanyCode())) {
            return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.EMPTY_COMP_CODE.getMessage());
        }

        if (HelperFunc.isEmpty(rec.getDepartmentId())) {
            return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.EMPTY_DEP_ID.getMessage());
        }

        if (!rec.getDepartmentId().trim().matches("^\\S{1,30}$")) {
            return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.INV_FORMAT_DEP_CODE.getMessage());
        }

        if (HelperFunc.isEmpty(rec.getDepartmentName())) {
            return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.EMPTY_DEP_NAME.getMessage());
        }

        return ok(null);
    }

    private void convert(WorkerRecord rec, Person person) throws ParseException {

        //person.setUpdated(new Date());

        person.setFirstName(rec.getFirstName().trim());
        person.setLastName(rec.getLastName().trim());
        person.setSecondName(HelperFunc.isEmpty(rec.getSecondName()) ? null : rec.getSecondName().trim());
        person.setDisplayName(HelperService.generateDisplayName(person.getFirstName(), person.getLastName(), person.getSecondName()));
        person.setDisplayShortName(HelperService.generateDisplayShortName(person.getFirstName(), person.getLastName(), person.getSecondName()));
        person.setGender(rec.getSex() == null ? En_Gender.UNDEFINED : rec.getSex() == 1 ? En_Gender.MALE : En_Gender.FEMALE);
        person.setBirthday(HelperFunc.isEmpty(rec.getBirthday()) ? null : HelperService.DATE.parse(rec.getBirthday()));
        person.setIpAddress(HelperFunc.isEmpty(rec.getIpAddress()) ? null : rec.getIpAddress().trim());
        person.setInfo(HelperFunc.isEmpty(rec.getInfo()) ? null : rec.getInfo().trim());
        person.setInn(HelperFunc.isEmpty(rec.getInn()) ? null : rec.getInfo().trim());

        PlainContactInfoFacade contactInfoFacade = new PlainContactInfoFacade(person.getContactInfo());
        contactInfoFacade.setWorkPhone(HelperFunc.isEmpty(rec.getPhoneWork()) ? null : normalizePhoneNumber(rec.getPhoneWork().trim()));
        contactInfoFacade.setMobilePhone(HelperFunc.isEmpty(rec.getPhoneMobile()) ? null : normalizePhoneNumber(rec.getPhoneMobile().trim()));
        contactInfoFacade.setHomePhone(HelperFunc.isEmpty(rec.getPhoneHome()) ? null : normalizePhoneNumber(rec.getPhoneHome().trim()));
        contactInfoFacade.setEmail(HelperFunc.isEmpty(rec.getEmail()) ? null : rec.getEmail().trim());
        contactInfoFacade.setEmail_own(HelperFunc.isEmpty(rec.getEmailOwn()) ? null : rec.getEmailOwn().trim());
    }

    private WorkerPosition getValidPosition(String positionName, Long companyId) throws Exception {

        WorkerPosition position = workerPositionDAO.getByName(positionName.trim(), companyId);

        if (position != null)
            return position;

        position = new WorkerPosition();
        position.setCompanyId(companyId);
        position.setName(positionName.trim());

        persistPosition(position);

        return position;
    }

    private Result<UserLogin> createLDAPAccount(Person person) throws Exception {

        ContactItem email = person.getContactInfo().findFirst(En_ContactItemType.EMAIL, En_ContactDataAccess.PUBLIC);
        if (!email.isEmpty() && HelperFunc.isNotEmpty(email.value())) {
            String login = email.value().substring(0, email.value().indexOf("@"));
            if (!userLoginDAO.isUnique(login.trim())) {
                logger.debug("error: Login already exist.");
                return error(En_ResultStatus.LOGIN_ALREADY_EXIST, En_ErrorCode.LOGIN_ALREADY_EXIST.getMessage());
            }

            UserLogin userLogin = userLoginDAO.createNewUserLogin(person);
            userLogin.setUlogin(login.trim());
            userLogin.setAuthType(En_AuthType.LDAP);
            userLogin.setRoles(new HashSet<>(userRoleDAO.getDefaultEmployeeRoles()));
            return ok(userLogin);
        }

        return ok();
    }

    private String makeFileName(Long id) {
        String fileName = WSConfig.getInstance().getDirPhotos() + id + ".jpg";
        logger.debug("name of file: {} ", fileName);
        return fileName;
    }

    private void checkRegistrationByPerson(Long personId) throws Exception {
        EmployeeRegistration registration = employeeRegistrationDAO.getByPersonId(personId);
        if (registration != null) {
            registration.setPerson(null);
            mergeEmployeeRegistration(registration);
        }
    }

    private void persistPerson(Person person) throws Exception {
        personDAO.persist(person);
        contactItemDAO.saveOrUpdateBatch(person.getContactItems());
        jdbcManyRelationsHelper.persist(person, Person.Fields.CONTACT_ITEMS);
        makeAudit(person, En_AuditType.EMPLOYEE_CREATE);
    }

    private void mergePerson(Person person) throws Exception {
        personDAO.merge(person);
        contactItemDAO.saveOrUpdateBatch(person.getContactItems());
        jdbcManyRelationsHelper.persist(person, Person.Fields.CONTACT_ITEMS);
        makeAudit(person, En_AuditType.EMPLOYEE_MODIFY);
    }

    private void persistWorker(WorkerEntry worker) throws Exception {
        workerEntryDAO.persist(worker);
        makeAudit(worker, En_AuditType.WORKER_CREATE);
    }

    private void mergeWorker(WorkerEntry worker) throws Exception {
        workerEntryDAO.merge(worker);
        makeAudit(worker, En_AuditType.WORKER_MODIFY);
    }

    private void removeWorker(WorkerEntry worker) throws Exception {
        if (workerEntryDAO.remove(worker))
            makeAudit(new LongAuditableObject(worker.getId()), En_AuditType.WORKER_REMOVE);
    }

    private void persistDepartment(CompanyDepartment department) throws Exception {
        companyDepartmentDAO.persist(department);
        makeAudit(department, En_AuditType.DEPARTMENT_CREATE);
    }

    private void mergeDepartment(CompanyDepartment department) throws Exception {
        companyDepartmentDAO.merge(department);
        makeAudit(department, En_AuditType.DEPARTMENT_MODIFY);
    }

    private void removeDepartment(CompanyDepartment department) throws Exception {
        if (companyDepartmentDAO.remove(department))
            makeAudit(new LongAuditableObject(department.getId()), En_AuditType.DEPARTMENT_REMOVE);
    }

    private void persistPosition(WorkerPosition position) throws Exception {
        workerPositionDAO.persist(position);
        makeAudit(position, En_AuditType.POSITION_CREATE);
    }

    private void mergePosition(WorkerPosition position) throws Exception {
        workerPositionDAO.merge(position);
        makeAudit(position, En_AuditType.POSITION_MODIFY);
    }

    private void removePosition(WorkerPosition position) throws Exception {
        if (workerPositionDAO.remove(position))
            makeAudit(new LongAuditableObject(position.getId()), En_AuditType.POSITION_REMOVE);
    }

    private void saveAccount(UserLogin userLogin) throws Exception {
        if (userLoginDAO.saveOrUpdate(userLogin)) {
            jdbcManyRelationsHelper.persist( userLogin, "roles" );
            makeAudit(userLogin, userLogin.getId() == null ? En_AuditType.ACCOUNT_CREATE : En_AuditType.ACCOUNT_MODIFY);
        }
    }

    private void removeAccount(UserLogin userLogin) throws Exception {
        if (userLoginDAO.remove(userLogin))
            makeAudit(new LongAuditableObject(userLogin.getId()), En_AuditType.ACCOUNT_REMOVE);
    }

    private void mergeEmployeeRegistration(EmployeeRegistration employeeRegistration) throws Exception {
        employeeRegistrationDAO.merge(employeeRegistration);
        makeAudit(employeeRegistration, En_AuditType.EMPLOYEE_REGISTRATION_MODIFY);
    }

    private void makeAudit(AuditableObject object, En_AuditType type) throws Exception {
        AuditObject auditObject = new AuditObject();
        auditObject.setCreated( new Date() );
        auditObject.setType(type);
        auditObject.setCreatorId( 0L );
        auditObject.setCreatorIp(Inet4Address.getLocalHost ().getHostAddress());
        auditObject.setCreatorShortName("portal-api");
        auditObject.setEntryInfo(object);

        auditObjectDAO.insertAudit(auditObject);
    }

    /**
     * utility
     **/

    public class OperationData {

        CompanyHomeGroupItem homeGroupItem;
        CompanyDepartment department;
        CompanyDepartment parentDepartment;
        WorkerEntry headDepartment;
        WorkerEntry worker;
        Person person;
        WorkerPosition position;
        List<UserLogin> account;
        EmployeeRegistration registration;

        Record record;

        private En_ErrorCode lastError;

        public OperationData(WorkerRecord record) {
            this.record = new Record(record);
        }

        public OperationData(DepartmentRecord record) {
            this.record = new Record(record);
        }

        public OperationData(String companyCode, String departmentId, String workerId) {
            this.record = new Record(companyCode, departmentId, workerId, null, null, null);
        }

        public OperationData(String companyCode, Long personId, String positionName, String newPositionName) {
            this.record = new Record(companyCode, null, null, personId, positionName, newPositionName);
        }

        public OperationData requireHomeItem() {
            return requireHomeItem(null);
        }

        public OperationData requireHomeItem(Supplier<CompanyHomeGroupItem> optional) {
            if (homeGroupItem == null)
                homeGroupItem = handle(companyGroupHomeDAO.getByExternalCode(record.getCompanyCode().trim()), optional, En_ErrorCode.UNKNOWN_COMP);

            return this;
        }

        public OperationData requireDepartment(Supplier<CompanyDepartment> optional) {
            requireHomeItem();
            if (isValid())
                this.department = handle(companyDepartmentDAO.getByExternalId(record.getDepartmentId().trim(), homeGroupItem.getCompanyId()), optional, En_ErrorCode.UNKNOWN_DEP);

            return this;
        }

        public OperationData requirePerson(Supplier<Person> optional) {
            if (isValid())
                this.person = handle(personDAO.get(record.getPersonId()), optional, En_ErrorCode.UNKNOWN_PER);

            return this;
        }

        public OperationData requireWorker(Supplier<WorkerEntry> optional) {
            requireHomeItem();
            if (isValid())
                this.worker = handle(workerEntryDAO.getByExternalId(record.getWorkerId().trim(), homeGroupItem.getCompanyId()), optional, En_ErrorCode.UNKNOWN_WOR);

            return this;
        }

        public OperationData requireParentDepartment(Supplier<CompanyDepartment> optional) {
            requireHomeItem();
            if (isValid() && HelperFunc.isNotEmpty(record.getParentDepartmentId()))
                this.parentDepartment = handle(companyDepartmentDAO.getByExternalId(record.getParentDepartmentId().trim(), homeGroupItem.getCompanyId()), optional, En_ErrorCode.UNKNOWN_PAR_DEP);

            return this;
        }

        public OperationData requireHeadDepartment(Supplier<WorkerEntry> optional) {
            requireHomeItem();
            if (isValid() && HelperFunc.isNotEmpty(record.getHeadDepartmentId()))
                this.headDepartment = handle(workerEntryDAO.getByExternalId(record.getHeadDepartmentId().trim(), homeGroupItem.getCompanyId()), optional, En_ErrorCode.UNKNOWN_WOR);

            return this;
        }

        public OperationData requirePosition(Supplier<WorkerPosition> optional) {
            requireHomeItem();
            if (isValid())
                this.position = handle(workerPositionDAO.getByName(record.getPositionName().trim(), homeGroupItem.getCompanyId()), optional, En_ErrorCode.UNKNOWN_POS);

            return this;
        }

        public OperationData requireAccount(Supplier<List<UserLogin>> optional) {
            if (isValid() && record.getPersonId() != null) {
                this.account = handle(userLoginDAO.findLDAPByPersonId(record.getPersonId()), optional, null, true);
                jdbcManyRelationsHelper.fill(account, "roles");
            }

            return this;
        }

        public OperationData requireRegistration(Supplier<EmployeeRegistration> optional) {
            if (isValid() && record.registrationId != null)
                this.registration = handle(employeeRegistrationDAO.get(record.getRegistrationId()), optional, En_ErrorCode.UNKNOWN_REG);

            return this;
        }

        public OperationData requireNotExistsWorker() {
            requireHomeItem();
            if (!isValid())
                return this;

            if (workerEntryDAO.checkExistsByExternalId(record.getWorkerId().trim(), homeGroupItem.getCompanyId())) {
                lastError = En_ErrorCode.EXIST_WOR;
            }

            return this;
        }

        public OperationData requireNotExistsChildDepartment() {
            requireHomeItem();
            if (!isValid())
                return this;

            if (companyDepartmentDAO.checkExistsByParent(record.getDepartmentId().trim(), homeGroupItem.getCompanyId())) {
                lastError = En_ErrorCode.EXIST_CHILD_DEP;
            }

            return this;
        }

        public OperationData requireNotExistsDepartmentWorker() {
            requireHomeItem();
            if (!isValid())
                return this;

            if (workerEntryDAO.checkExistsByDep(record.getDepartmentId().trim(), homeGroupItem.getCompanyId())) {
                lastError = En_ErrorCode.EXIST_DEP_WOR;
            }

            return this;
        }

        public OperationData requireNotExistsNewPosition() {
            requireHomeItem();
            if (!isValid())
                return this;

            if (position == null && workerPositionDAO.checkExistsByName(record.getNewPositionName().trim(), homeGroupItem.getCompanyId())) {
                lastError = En_ErrorCode.EXIST_POS;
            }

            if (position != null && workerPositionDAO.checkExistsByName(record.getNewPositionName().trim(), homeGroupItem.getCompanyId(), position.getId())) {
                lastError = En_ErrorCode.EXIST_POS;
            }

            return this;
        }

        public OperationData requireNotExistsPositionWorker() {
            requireHomeItem();
            if (!isValid())
                return this;

            if (workerEntryDAO.checkExistsByPosName(record.getPositionName().trim(), homeGroupItem.getCompanyId())) {
                lastError = En_ErrorCode.EXIST_POS_WOR;
            }

            return this;
        }

        private <T> T handle(T value, Supplier<T> optional, En_ErrorCode failCode) {
            return handle(value, optional, failCode, false);
        }

        private <T> T handle(T value, Supplier<T> optional, En_ErrorCode failCode, boolean nullable) {
            if (value == null && optional != null)
                value = optional.get();

            if (value == null && !nullable) {
                lastError = failCode;
            }
            return value;
        }

        public boolean isValid() {
            return lastError == null;
        }

        public Result failResult() {
            if (lastError != null) {
                logger.debug("error result: {}", lastError.getMessage());
                return error(En_ResultStatus.INCORRECT_PARAMS, lastError.getMessage());
            }

            return null;
        }

        public CompanyHomeGroupItem homeItem() {
            return homeGroupItem;
        }

        public CompanyDepartment department() {
            return department;
        }

        public CompanyDepartment parentDepartment() {
            return parentDepartment;
        }

        public WorkerEntry headDepartment() {
            return headDepartment;
        }

        public Person person() {
            return person;
        }

        public WorkerEntry worker() {
            return worker;
        }

        public WorkerPosition position() {
            return position;
        }

        public List<UserLogin> account() {
            return account;
        }

        public EmployeeRegistration registration() {
            return registration;
        }

        public class Record {
            String companyCode;
            String departmentId;
            String parentDepartmentId;
            String headDepartmentId;
            String workerId;
            Long personId;
            String positionName;
            String newPositionName;
            Long registrationId;

            public Record(WorkerRecord workerRecord) {
                this.companyCode = workerRecord.getCompanyCode();
                this.departmentId = workerRecord.getDepartmentId();
                this.workerId = workerRecord.getWorkerId();
                this.personId = workerRecord.getId();
                this.registrationId = workerRecord.getRegistrationId();
            }

            public Record(DepartmentRecord departmentRecord) {
                this.companyCode = departmentRecord.getCompanyCode();
                this.departmentId = departmentRecord.getDepartmentId();
                this.parentDepartmentId = departmentRecord.getParentId();
                this.headDepartmentId = departmentRecord.getHeadId();
            }

            public Record(String companyCode, String departmentId, String workerId, Long personId, String positionName, String newPositionName) {
                this.companyCode = companyCode;
                this.departmentId = departmentId;
                this.workerId = workerId;
                this.personId = personId;
                this.positionName = positionName;
                this.newPositionName = newPositionName;
            }

            public String getCompanyCode() {
                return companyCode;
            }

            public String getDepartmentId() {
                return departmentId;
            }

            public String getParentDepartmentId() {
                return parentDepartmentId;
            }

            public String getHeadDepartmentId() {
                return headDepartmentId;
            }

            public String getWorkerId() {
                return workerId;
            }

            public Long getPersonId() {
                return personId;
            }

            public String getPositionName() {
                return positionName;
            }

            public String getNewPositionName() {
                return newPositionName;
            }

            public Long getRegistrationId() {
                return registrationId;
            }
        }
    }

    private boolean checkAuth (HttpServletRequest request, HttpServletResponse response){
        Result<AuthToken> authTokenAPIResult = AuthUtils.authenticate(request, response, authService, sidGen, logger);
        if (authTokenAPIResult.isError()){
            try {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        AuthToken token = authTokenAPIResult.getData();

        Result<UserLogin> userLoginResult = authService.getUserLogin(token, token.getUserLoginId());
        if (userLoginResult.isError()) {
            try {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        if (!userLoginResult.getData().getUlogin().equals("ws_api")) {
            try {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    private Result<Long> update(WorkerRecord rec){
        Result<Long> isValid = isValidWorkerRecord(rec);
        if (isValid.isError()) {
            logger.debug("error result: {}", isValid.getMessage());
            return isValid;
        }

        try {

            OperationData operationData = new OperationData(rec)
                    .requireHomeItem()
                    .requireDepartment(null)
                    .requirePerson(null)
                    .requireWorker(null)
                    .requireAccount(null)
                    .requireRegistration(null);

            if (!operationData.isValid())
                return operationData.failResult();

            return transactionTemplate.execute(transactionStatus -> {

                try {

                    Person person = operationData.person();
                    String personLastName = person.getLastName();
                    WorkerEntry worker = operationData.worker();
                    List<UserLogin> userLogins = operationData.account();
                    EmployeeRegistration employeeRegistration = operationData.registration();

                    convert(rec, person);

                    String email = new PlainContactInfoFacade(person.getContactInfo()).getEmail();
                    if (isEmailExists(person.getId(), email)){
                        logger.debug("addWorker(): worker with email={} already exists", email);
                        return error(En_ResultStatus.EMPLOYEE_EMAIL_ALREADY_EXIST, En_ErrorCode.EMAIL_ALREADY_EXIST.getMessage());
                    }

                    if (rec.isFired() || rec.isDeleted()) {
                        boolean immediately = false;
                        if (HelperFunc.isNotEmpty(rec.getFireDate())) {
                            Date firedDate = HelperService.DATE.parse(rec.getFireDate());
                            Date now = new Date();
                            if (firedDate.after(now)) {
                                worker.setFiredDate(firedDate);
                                worker.setDeleted(rec.isDeleted());
                                workerEntryDAO.merge(worker);

                                logger.debug("success result, workerRowId={}", worker.getId());
                            } else {
                                immediately = true;
                            }
                        } else {
                            immediately = true;
                        }

                        if (immediately) {
                            workerEntryDAO.remove(worker);
                            if (!workerEntryDAO.checkExistsByPersonId(person.getId())) {
                                return workerEntryService.firePerson(person, rec.isFired(), null, rec.isDeleted(), userLogins, WSConfig.getInstance().isEnableMigration())
                                        .map(ignore -> {
                                            logger.debug("success result, workerRowId={}", worker.getId());
                                            return person.getId();
                                        });
                            }
                        }

                        logger.debug("success result, workerRowId={}", worker.getId());
                        return ok(person.getId());
                    }

                    mergePerson(person);

                    /* final boolean YOUTRACK_INTEGRATION_ENABLED = portalConfig.data().integrationConfig().isYoutrackEmployeeSyncEnabled();
                    if (YOUTRACK_INTEGRATION_ENABLED) {
                        createAdminYoutrackIssueIfNeeded(person.getId(), person.getFirstName(), person.getLastName(), person.getSecondName(), personLastName);
                    }*/

                    if (isEmpty(userLogins)) {
                        Result<UserLogin> userLoginResult = createLDAPAccount(person);
                        if (userLoginResult.isError()){
                            return error(userLoginResult.getStatus(), userLoginResult.getMessage());
                        }

                         UserLogin userLogin = userLoginResult.getData();

                        if (userLogin != null) {
                            userLogin.setAdminStateId(En_AdminState.UNLOCKED.getId());
                            saveAccount(userLogin);
                        }
                    } else {
                        for (UserLogin userLogin : userLogins) {
                            userLogin.setAdminStateId(En_AdminState.UNLOCKED.getId());
                            saveAccount(userLogin);
                        }
                    }

                    if (employeeRegistration != null) {
                        checkRegistrationByPerson(person.getId());
                        employeeRegistration.setPerson(person);
                        mergeEmployeeRegistration(employeeRegistration);
                    }

                    WorkerPosition position = getValidPosition(rec.getPositionName(), operationData.homeItem().getCompanyId());

                    worker.setDepartmentId(operationData.department().getId());
                    worker.setPositionId(position.getId());
                    worker.setHireDate(HelperFunc.isNotEmpty(rec.getHireDate()) ? HelperService.DATE.parse(rec.getHireDate()) : null);
                    worker.setHireOrderNo(HelperFunc.isNotEmpty(rec.getHireOrderNo()) ? rec.getHireOrderNo().trim() : null);
                    worker.setActiveFlag(rec.getActive());

                    mergeWorker(worker);

                    if (WSConfig.getInstance().isEnableMigration()) {
                        Workers workers = new Workers(workerEntryDAO.getWorkers(new WorkerEntryQuery(person.getId())));
                        String departmentName = worker.getActiveFlag() == 1 ? operationData.department().getName() : workers.getActiveDepartment(operationData.department().getName());
                        String positionName = worker.getActiveFlag() == 1 ? position.getName() : workers.getActivePosition(position.getName());
                        migrationManager.saveExternalEmployee(person, departmentName, positionName);
                    }

                    logger.debug("success result, workerRowId={}", worker.getId());
                    return ok(person.getId());

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            logger.error("error while update worker's record", e);
        }

        return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.NOT_UPDATE.getMessage());
    }

    private Result<Long> updateDate(WorkerRecord rec){
        Result<Long> isValid = isValidWorkerRecord(rec);
        if (isValid.isError()) {
            logger.debug("error result: {}", isValid.getMessage());
            return isValid;
        }

        try {

            OperationData operationData = new OperationData(rec)
                    .requirePerson(null);

            if (!operationData.isValid()){
                try {
                    return transactionTemplate.execute(transactionStatus -> {
                        try{
                            Date newDate = HelperFunc.isNotEmpty(rec.getFireDate()) ? HelperService.DATE.parse(rec.getFireDate()) : null;
                            Date birthday =  HelperFunc.isNotEmpty(rec.getBirthday()) ? HelperService.DATE.parse(rec.getBirthday()) : null;

                            if (newDate == null) return ok();
                            if (birthday == null) return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.EMPTY_BIRTHDAY.getMessage());

                            List<Person> personList = personDAO.getListByCondition(
                                    "person.isdeleted=false and person.isfired=true and person.lastname=? and person.firstname=? and person.birthday=?",
                                    rec.getLastName(), rec.getFirstName(), rec.getBirthday());
                            jdbcManyRelationsHelper.fill(personList, Person.Fields.CONTACT_ITEMS);

                            if (personList.isEmpty()) return ok();

                            for (Person person : personList) {
                                if (person.getFireDate() == null || person.getFireDate().before(newDate)) {
                                    person.setFired(newDate);
                                    mergePerson(person);
                                    logger.debug("success result, personId={}", person.getId());
                                    return ok(person.getId());
                                }
                            }
                            return ok();
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                } catch (Exception e){
                    logger.error("error while update worker's record", e);
                }
            }
            else {


                return transactionTemplate.execute(transactionStatus -> {

                    try {

                        Person person = operationData.person();

                        if (person.isFired() && HelperFunc.isNotEmpty(rec.getFireDate())) {
                            Date currentDate = person.getFireDate();
                            Date newDate = HelperService.DATE.parse(rec.getFireDate());

                            if (currentDate == null || currentDate.before(newDate)) {
                                person.setFired(newDate);
                                mergePerson(person);
                            }
                        }

                        logger.debug("success result, personId={}", person.getId());
                        return ok(person.getId());

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }

        } catch (Exception e) {
            logger.error("error while update worker's record", e);
        }

        return error(En_ResultStatus.INCORRECT_PARAMS, En_ErrorCode.NOT_UPDATE.getMessage());
    }

    private void createAdminYoutrackIssueIfNeeded(Long employeeId, String firstName, String lastName, String secondName, String oldLastName) {
        if (Objects.equals(lastName, oldLastName)) {
            return;
        }
        final String USER_SUPPORT_PROJECT_NAME = portalConfig.data().youtrack().getSupportProject();
        final String PORTAL_URL = portalConfig.data().getCommonConfig().getCrmUrlInternal();

        String employeeOldFullName = oldLastName + " " + firstName + " " + (secondName != null ? secondName : "");
        String employeeNewFullName = lastName + " " + firstName + " " + (secondName != null ? secondName : "");

        String summary = "Смена фамилии сотрудника " + employeeOldFullName;

        String description = "Карточка сотрудника: " + "[" + employeeNewFullName + "](" + PORTAL_URL + "#employee_preview:id=" + employeeId + ")" + "\n" +
                             "Старое ФИО: " + employeeOldFullName + "\n" +
                             "Новое ФИО: " + employeeNewFullName + "\n" +
                             "\n" +
                             "Необходимо изменение учетной записи, почты.";

        youtrackService.createIssue( USER_SUPPORT_PROJECT_NAME, summary, description );
    }

    private boolean isEmailExists(Long personId, String email) {

        if (email == null) {
            return false;
        }

        PersonQuery personQuery = new PersonQuery();
        personQuery.setEmail(email);
        personQuery.setDeleted(false);
        personQuery.setFired(false);
        List<Person> employeeByEmail = personDAO.getPersons(personQuery);

        if (CollectionUtils.isNotEmpty(employeeByEmail)){
            if (personId == null) {
                return true;
            }

            return (employeeByEmail.stream()
                    .anyMatch(personFromDB -> !personFromDB.getId().equals(personId)));
        }

        return false;
    }

}