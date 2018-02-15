package ru.protei.portal.api.controller;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;
import protei.sql.query.Tm_SqlQueryHelper;
import ru.protei.portal.api.config.WSConfig;
import ru.protei.portal.api.model.*;
import ru.protei.portal.tools.migrate.WSMigrationManager;
import ru.protei.portal.tools.migrate.HelperService;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.struct.AuditObject;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.struct.Photo;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;

import java.io.*;
import java.net.Inet4Address;
import java.text.ParseException;
import java.util.Date;
import java.util.function.Function;
import java.util.function.Supplier;

@RestController
@RequestMapping(value = "/api/worker", headers = "Accept=application/xml")
public class WorkerController {

    private static Logger logger = LoggerFactory.getLogger(WorkerController.class);

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private CompanyGroupHomeDAO companyGroupHomeDAO;

    @Autowired
    private CompanyDepartmentDAO companyDepartmentDAO;

    @Autowired
    private WorkerPositionDAO workerPositionDAO;

    @Autowired
    private WorkerEntryDAO workerEntryDAO;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Autowired
    WSMigrationManager migrationManager;

    @Autowired
    AuditObjectDAO auditObjectDAO;

    @RequestMapping(method = RequestMethod.GET, value = "/get.person")
    public @ResponseBody
    WorkerRecord getPerson(@RequestParam(name = "id") Long id) {

        logger.debug("getPerson(): id={}", id);

        try {
            return new WorkerRecord(personDAO.get(id));
        } catch (Throwable e) {
            logger.error("error while get worker", e);
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get.worker")
    public @ResponseBody
    WorkerRecord getWorker(@RequestParam(name = "id") String id, @RequestParam(name = "companyCode") String companyCode) {

        logger.debug("getWorker(): id={}, companyCode={}", id, companyCode);

        try {

            return withHomeCompany(companyCode,
                    item -> new WorkerRecord(workerEntryDAO.getByExternalId(id.trim(), item.getCompanyId())));

        } catch (Throwable e) {
            logger.error("error while get worker", e);
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get.department")
    public @ResponseBody
    DepartmentRecord getDepartment(@RequestParam(name = "id") String id, @RequestParam(name = "companyCode") String companyCode) {

        logger.debug("getDepartment(): id={}, companyCode={}", id, companyCode);

        try {

            return withHomeCompany(companyCode,
                    item -> new DepartmentRecord(companyDepartmentDAO.getByExternalId(id, item.getCompanyId())));

        } catch (Exception e) {
            logger.error("error while get department", e);
        }

        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get.persons")
    public @ResponseBody
    WorkerRecordList getPersons(@RequestParam(name = "expr") String expr) {

        logger.debug("getPersons(): expr={}", expr);

        WorkerRecordList persons = new WorkerRecordList();

        try {

            EmployeeQuery query = new EmployeeQuery(null, null,
                    Tm_SqlQueryHelper.makeLikeArgEx(expr.trim()), En_SortField.person_full_name, En_SortDir.ASC);

            personDAO.getEmployees(query).forEach(
                    p -> persons.append(new WorkerRecord(p))
            );

        } catch (Exception e) {
            logger.error("error while get persons", e);
        }
        return persons;
    }


    @RequestMapping(method = RequestMethod.POST, value = "/add.worker")
    public @ResponseBody
    ServiceResult addWorker(@RequestBody WorkerRecord rec) {

        logger.debug("addWorker(): rec={}", rec);

        ServiceResult isValid = isValidWorkerRecord(rec);
        if (!isValid.isSuccess()) {
            logger.debug("error result: {}", isValid.getErrInfo());
            return isValid;
        }

        if (rec.isDeleted() || rec.isFired()) {
            logger.debug("error result: {}", En_ErrorCode.DELETED_OR_FIRED_RECORD.getMessage());
            return ServiceResult.failResult(En_ErrorCode.DELETED_OR_FIRED_RECORD.getCode(), En_ErrorCode.DELETED_OR_FIRED_RECORD.getMessage(), rec.getId());
        }

        try {

            OperationData operationData = new OperationData(rec)
                    .requireHomeItem()
                    .requireDepartment(null)
                    .requireNotExistsWorker();

            if (!operationData.isValid())
                return operationData.failResult();

            return transactionTemplate.execute(transactionStatus -> {

                try {

                    Person person = null;
                    if (rec.getId() != null) {
                        person = personDAO.get(rec.getId());
                    }

                    if (person == null) {
                        person = personDAO.createNewPerson(operationData.homeItem().getMainId());
                    }

                    convert(rec, person);

                    person.setFired(false);
                    person.setDeleted(false);

                    if (person.getId() == null) {
                        persistPerson(person);
                        logger.debug("created person with id={}", person.getId());
                    } else {
                        mergePerson(person);
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

                    persistWorker(worker);

                    if (WSConfig.getInstance().isEnableMigration()) {
                        migrationManager.savePerson(person, operationData.department().getName(), position.getName());
                    }


                    logger.debug("success result, workerRowId={}", worker.getId());
                    return ServiceResult.successResult(person.getId());

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            logger.error("error while add worker's record", e);
        }

        return ServiceResult.failResult(En_ErrorCode.NOT_CREATE.getCode(), En_ErrorCode.NOT_CREATE.getMessage(), null);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/update.worker")
    public @ResponseBody
    ServiceResult updateWorker(@RequestBody WorkerRecord rec) {

        logger.debug("updateWorker(): rec={}", rec);

        ServiceResult isValid = isValidWorkerRecord(rec);
        if (!isValid.isSuccess()) {
            logger.debug("error result: {}", isValid.getErrInfo());
            return isValid;
        }

        try {

            OperationData operationData = new OperationData(rec)
                    .requireHomeItem()
                    .requireDepartment(null)
                    .requirePerson(null)
                    .requireWorker(null);

            if (!operationData.isValid())
                return operationData.failResult();

            return transactionTemplate.execute(transactionStatus -> {

                try {

                    Person person = operationData.person();
                    WorkerEntry worker = operationData.worker();

                    convert(rec, person);

                    if (rec.isFired() || rec.isDeleted()) {

                        workerEntryDAO.remove(worker);

                        if (!workerEntryDAO.checkExistsByPersonId(person.getId())) {
                            person.setFired(rec.isFired());
                            person.setDeleted(rec.isDeleted());
                        }

                        mergePerson(person);

                        if (WSConfig.getInstance().isEnableMigration()) {
                            migrationManager.savePerson(person, "", "");
                        }

                        logger.debug("success result, workerRowId={}", worker.getId());
                        return ServiceResult.successResult(person.getId());
                    }

                    mergePerson(person);

                    WorkerPosition position = getValidPosition(rec.getPositionName(), operationData.homeItem().getCompanyId());

                    worker.setDepartmentId(operationData.department().getId());
                    worker.setPositionId(position.getId());
                    worker.setHireDate(HelperFunc.isNotEmpty(rec.getHireDate()) ? HelperService.DATE.parse(rec.getHireDate()) : null);
                    worker.setHireOrderNo(HelperFunc.isNotEmpty(rec.getHireOrderNo()) ? rec.getHireOrderNo().trim() : null);
                    worker.setActiveFlag(rec.getActive());

                    mergeWorker(worker);

                    if (WSConfig.getInstance().isEnableMigration()) {
                        migrationManager.savePerson(person, worker.getDepartment().getName(), position.getName());
                    }

                    logger.debug("success result, workerRowId={}", worker.getId());
                    return ServiceResult.successResult(person.getId());

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            logger.error("error while update worker's record", e);
        }

        return ServiceResult.failResult(En_ErrorCode.NOT_UPDATE.getCode(), En_ErrorCode.NOT_UPDATE.getCode(), null);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/update.workers")
    public @ResponseBody
    ServiceResultList updateWorkers(@RequestBody WorkerRecordList list) {

        logger.debug("updateWorkers(): list={}", list);

        ServiceResultList results = new ServiceResultList();

        try {

            list.getWorkerRecords().forEach(
                    p -> results.append(updateWorker(p))
            );

        } catch (Exception e) {
            logger.error("error while update workers", e);
        }
        return results;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete.worker")
    public @ResponseBody
    ServiceResult deleteWorker(@RequestParam(name = "externalId") String externalId, @RequestParam(name = "companyCode") String companyCode) {

        logger.debug("deleteWorker(): externalId={}, companyCode={}", externalId, companyCode);

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

                    removeWorker(worker);

                    if (!workerEntryDAO.checkExistsByPersonId(personId)) {
                        Person person = personDAO.get(personId);
                        person.setDeleted(true);
                        mergePerson(person);

                        if (WSConfig.getInstance().isEnableMigration()) {
                            migrationManager.deletePerson(person);
                        }
                    }
                    logger.debug("success result, workerRowId={}", worker.getId());
                    return ServiceResult.successResult(worker.getId());

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            logger.error("error while remove worker's record", e);
        }

        return ServiceResult.failResult(En_ErrorCode.NOT_DELETE.getCode(), En_ErrorCode.NOT_DELETE.getMessage(), null);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/update.photo")
    public @ResponseBody
    ServiceResult updatePhoto(@RequestBody Photo photo) {

        logger.debug("updatePhoto(): photo={}", photo);

        if (HelperFunc.isEmpty(photo.getContent())) {
            logger.debug("error result: {}", En_ErrorCode.EMPTY_PHOTO_CONTENT.getMessage());
            return ServiceResult.failResult(En_ErrorCode.EMPTY_PHOTO_CONTENT.getCode(), En_ErrorCode.EMPTY_PHOTO_CONTENT.getMessage(), photo.getId());
        }

        try {

            OperationData operationData = new OperationData(null, photo.getId(), null, null)
                    .requirePerson(null);

            if (!operationData.isValid())
                return operationData.failResult();

            try (Base64OutputStream out = new Base64OutputStream(new FileOutputStream(makeFileName(photo.getId())), false)) {

                out.write(photo.getContent().getBytes());
                out.flush();

                makeAudit(photo, En_AuditType.PHOTO_UPLOAD);

                logger.debug("success result, personId={}", photo.getId());
                return ServiceResult.successResult(photo.getId());
            }

        } catch (Exception e) {
            logger.error("error while update photo", e);
        }

        return ServiceResult.failResult(En_ErrorCode.NOT_UPDATE.getCode(), En_ErrorCode.NOT_UPDATE.getMessage(), null);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/get.photos")
    public @ResponseBody
    PhotoList getPhotos(@RequestBody IdList list) {

        logger.debug("getPhotos(): list={}", list);

        Base64InputStream in = null;
        PhotoList photos = new PhotoList();

        try {

            for (Long id : list.getIds()) {

                File file = new File(makeFileName(id));
                if (file.exists()) {

                    in = new Base64InputStream(new FileInputStream(file), true);
                    StringWriter sw = new StringWriter();
                    IOUtils.copy(in, sw);

                    Photo photo = new Photo();
                    photo.setId(id);
                    photo.setContent(sw.toString());
                    photos.getPhotos().add(photo);

                    logger.debug("file exists, photo={}", photo);

                    IOUtils.closeQuietly(in);
                } else {
                    logger.debug("file doesn't exist");
                }
            }

        } catch (Exception e) {
            logger.error("error while get photos", e);
        } finally {
            IOUtils.closeQuietly(in);
        }

        logger.debug("result, size of photo's list {}", photos.getPhotos().size());
        return photos;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/update.department")
    public @ResponseBody
    ServiceResult updateDepartment(@RequestBody DepartmentRecord rec) {

        logger.debug("updateDepartment(): rec={}", rec);

        ServiceResult isValid = isValidDepartmentRecord(rec);
        if (!isValid.isSuccess()) {
            logger.debug("error result: " + isValid.getErrInfo());
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
                department.setTypeId(1);
                department.setExternalId(rec.getDepartmentId().trim());
                persistDepartment(department);
            } else {
                mergeDepartment(department);
            }

            logger.debug("success result, departmentRowId={}", department.getId());
            return ServiceResult.successResult(department.getId());

        } catch (Exception e) {
            logger.error("error while update department's record", e);
        }

        return ServiceResult.failResult(En_ErrorCode.NOT_UPDATE.getCode(), En_ErrorCode.NOT_UPDATE.getMessage(), null);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete.department")
    public @ResponseBody
    ServiceResult deleteDepartment(@RequestParam(name = "externalId") String externalId, @RequestParam(name = "companyCode") String companyCode) {

        logger.debug("deleteDepartment(): externalId={}, companyCode={}", externalId, companyCode);

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
            return ServiceResult.successResult(department.getId());

        } catch (Exception e) {
            logger.error("error while remove department's record", e);
        }

        return ServiceResult.failResult(En_ErrorCode.NOT_DELETE.getCode(), En_ErrorCode.NOT_DELETE.getMessage(), null);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/update.position")
    public @ResponseBody
    ServiceResult updatePosition(@RequestParam(name = "oldName") String oldName, @RequestParam(name = "newName") String newName, @RequestParam(name = "companyCode") String companyCode) {

        logger.debug("updatePosition(): oldName={}, newName={}, companyCode={}", oldName, newName, companyCode);

        if (HelperFunc.isEmpty(oldName) || HelperFunc.isEmpty(newName)) {
            logger.debug("error result: " + En_ErrorCode.EMPTY_POS.getMessage());
            return ServiceResult.failResult(En_ErrorCode.EMPTY_POS.getCode(), En_ErrorCode.EMPTY_POS.getMessage(), null);
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
            return ServiceResult.successResult(position.getId());

        } catch (Exception e) {
            logger.error("error while update position's record", e);
        }

        return ServiceResult.failResult(En_ErrorCode.NOT_UPDATE.getCode(), En_ErrorCode.NOT_UPDATE.getMessage(), null);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete.position")
    public @ResponseBody
    ServiceResult deletePosition(@RequestParam(name = "name") String name, @RequestParam(name = "companyCode") String companyCode) {

        logger.debug("deletePosition(): name={}, companyCode={}", name, companyCode);

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
            return ServiceResult.successResult(position.getId());

        } catch (Exception e) {
            logger.error("error while remove position's record", e);
        }

        return ServiceResult.failResult(En_ErrorCode.NOT_DELETE.getCode(), En_ErrorCode.NOT_DELETE.getMessage(), null);
    }

    private <R> R withHomeCompany(String companyCode, Function<CompanyHomeGroupItem, R> func) throws Exception {
        CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(companyCode.trim());
        return item == null ? null : func.apply(item);
    }

    private ServiceResult isValidWorkerRecord(WorkerRecord rec) {

        if (HelperFunc.isEmpty(rec.getCompanyCode())) {
            return ServiceResult.failResult(En_ErrorCode.EMPTY_COMP_CODE.getCode(), En_ErrorCode.EMPTY_COMP_CODE.getMessage(), rec.getId());
        }

        if (HelperFunc.isEmpty(rec.getDepartmentId())) {
            return ServiceResult.failResult(En_ErrorCode.EMPTY_DEP_ID.getCode(), En_ErrorCode.EMPTY_DEP_ID.getMessage(), rec.getId());
        }

        if (HelperFunc.isEmpty(rec.getPositionName())) {
            return ServiceResult.failResult(En_ErrorCode.EMPTY_POS.getCode(), En_ErrorCode.EMPTY_POS.getMessage(), rec.getId());
        }

        if (HelperFunc.isEmpty(rec.getWorkerId())) {
            return ServiceResult.failResult(En_ErrorCode.EMPTY_WOR_ID.getCode(), En_ErrorCode.EMPTY_WOR_ID.getMessage(), rec.getId());
        }

        if (!rec.getWorkerId().trim().matches("^\\S{1,30}$")) {
            return ServiceResult.failResult(En_ErrorCode.INV_FORMAT_WOR_CODE.getCode(), En_ErrorCode.INV_FORMAT_WOR_CODE.getMessage(), rec.getId());
        }

        if (HelperFunc.isEmpty(rec.getFirstName())) {
            return ServiceResult.failResult(En_ErrorCode.EMPTY_FIRST_NAME.getCode(), En_ErrorCode.EMPTY_FIRST_NAME.getMessage(), rec.getId());
        }

        if (HelperFunc.isEmpty(rec.getLastName())) {
            return ServiceResult.failResult(En_ErrorCode.EMPTY_LAST_NAME.getCode(), En_ErrorCode.EMPTY_LAST_NAME.getMessage(), rec.getId());
        }

        if (HelperFunc.isNotEmpty(rec.getIpAddress()) &&
                !rec.getIpAddress().trim().matches("^[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}$")) {
            return ServiceResult.failResult(En_ErrorCode.INV_FORMAT_IP.getCode(), En_ErrorCode.INV_FORMAT_IP.getMessage(), rec.getId());
        }

        return ServiceResult.successResult(rec.getId());
    }

    private ServiceResult isValidDepartmentRecord(DepartmentRecord rec) {

        if (HelperFunc.isEmpty(rec.getCompanyCode())) {
            return ServiceResult.failResult(En_ErrorCode.EMPTY_COMP_CODE.getCode(), En_ErrorCode.EMPTY_COMP_CODE.getMessage(), null);
        }

        if (HelperFunc.isEmpty(rec.getDepartmentId())) {
            return ServiceResult.failResult(En_ErrorCode.EMPTY_DEP_ID.getCode(), En_ErrorCode.EMPTY_DEP_ID.getMessage(), null);
        }

        if (!rec.getDepartmentId().trim().matches("^\\S{1,30}$")) {
            return ServiceResult.failResult(En_ErrorCode.INV_FORMAT_DEP_CODE.getCode(), En_ErrorCode.INV_FORMAT_DEP_CODE.getMessage(), null);
        }

        if (HelperFunc.isEmpty(rec.getDepartmentName())) {
            return ServiceResult.failResult(En_ErrorCode.EMPTY_DEP_NAME.getCode(), En_ErrorCode.EMPTY_DEP_NAME.getMessage(), null);
        }

        return ServiceResult.successResult(null);
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
        person.setPassportInfo(HelperFunc.isEmpty(rec.getPassportInfo()) ? null : rec.getPassportInfo().trim());
        person.setInfo(HelperFunc.isEmpty(rec.getInfo()) ? null : rec.getInfo().trim());

        PlainContactInfoFacade contactInfoFacade = new PlainContactInfoFacade(person.getContactInfo());
        contactInfoFacade.setWorkPhone(HelperFunc.isEmpty(rec.getPhoneWork()) ? null : rec.getPhoneWork().trim());
        contactInfoFacade.setMobilePhone(HelperFunc.isEmpty(rec.getPhoneMobile()) ? null : rec.getPhoneMobile().trim());
        contactInfoFacade.setHomePhone(HelperFunc.isEmpty(rec.getPhoneHome()) ? null : rec.getPhoneHome().trim());
        contactInfoFacade.setLegalAddress(HelperFunc.isEmpty(rec.getAddress()) ? null : rec.getAddress().trim());
        contactInfoFacade.setHomeAddress(HelperFunc.isEmpty(rec.getAddressHome()) ? null : rec.getAddressHome().trim());
        contactInfoFacade.setEmail(HelperFunc.isEmpty(rec.getEmail()) ? null : rec.getEmail().trim());
        contactInfoFacade.setEmail_own(HelperFunc.isEmpty(rec.getEmailOwn()) ? null : rec.getEmailOwn().trim());
        contactInfoFacade.setFax(HelperFunc.isEmpty(rec.getFax()) ? null : rec.getFax().trim());
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

    private String makeFileName(Long id) {
        String fileName = WSConfig.getInstance().getDirPhotos() + id + ".jpg";
        logger.debug("name of file: {} ", fileName);
        return fileName;
    }

    private void persistPerson(Person person) throws Exception {
        personDAO.persist(person);
        makeAudit(person, En_AuditType.EMPLOYEE_CREATE);
    }

    private void mergePerson(Person person) throws Exception {
        personDAO.merge(person);
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
        workerEntryDAO.remove(worker);
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
        companyDepartmentDAO.remove(department);
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
        workerPositionDAO.remove(position);
        makeAudit(new LongAuditableObject(position.getId()), En_AuditType.POSITION_REMOVE);
    }

    private void makeAudit(AuditableObject object, En_AuditType type) throws Exception {
        AuditObject auditObject = new AuditObject();
        auditObject.setCreated( new Date() );
        auditObject.setTypeId(type.getId());
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
            if (isValid())
                this.parentDepartment = handle(record.getParentDepartmentId() == null ? null :
                        companyDepartmentDAO.getByExternalId(record.getParentDepartmentId().trim(), homeGroupItem.getCompanyId()), optional, En_ErrorCode.UNKNOWN_PAR_DEP, true);

            return this;
        }

        public OperationData requireHeadDepartment(Supplier<WorkerEntry> optional) {
            requireHomeItem();
            if (isValid())
                this.headDepartment = handle(record.getHeadDepartmentId() == null ? null :
                        workerEntryDAO.getByExternalId(record.getHeadDepartmentId().trim(), homeGroupItem.getCompanyId()), optional, En_ErrorCode.UNKNOWN_WOR, true);

            return this;
        }

        public OperationData requirePosition(Supplier<WorkerPosition> optional) {
            requireHomeItem();
            if (isValid())
                this.position = handle(workerPositionDAO.getByName(record.getPositionName().trim(), homeGroupItem.getCompanyId()), optional, En_ErrorCode.UNKNOWN_POS);

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

        public ServiceResult failResult() {
            if (lastError != null) {
                logger.debug("error result: {}", lastError.getMessage());
                return ServiceResult.failResult(lastError.getCode(), lastError.getMessage(), record.getPersonId());
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

        public class Record {
            String companyCode;
            String departmentId;
            String parentDepartmentId;
            String headDepartmentId;
            String workerId;
            Long personId;
            String positionName;
            String newPositionName;

            public Record(WorkerRecord workerRecord) {
                this.companyCode = workerRecord.getCompanyCode();
                this.departmentId = workerRecord.getDepartmentId();
                this.workerId = workerRecord.getWorkerId();
                this.personId = workerRecord.getId();
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
        }
    }
}