package ru.protei.portal.api.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;
import protei.sql.query.Tm_SqlQueryHelper;
import ru.protei.portal.api.config.WSConfig;
import ru.protei.portal.api.model.*;
import ru.protei.portal.api.utils.HelperService;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.net.Inet4Address;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/api/worker", headers = "Accept=application/xml")
public class WorkerController {

    private static Logger logger = Logger.getLogger(WorkerController.class);

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

    @RequestMapping(method = RequestMethod.GET, value = "/get.person")
    public @ResponseBody WorkerRecord getPerson(@RequestParam(name = "id") Long id) {

        logger.debug("=== getPerson ===");
        logger.debug("=== id = " + id);

        try {
            if (id != null) {
                Person person = personDAO.get (id);
                if (person != null && person.getExternalCode() != null) {
                    return new WorkerRecord(person);
                }
            }
        } catch (Exception e) {
            logger.error ("error while get worker", e);
        }

        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get.worker")
    public @ResponseBody WorkerRecord getWorker(@RequestParam(name = "id") Long id, @RequestParam(name = "companyCode") String companyCode) {

        logger.debug("=== getWorker ===");
        logger.debug("=== id = " + id);
        logger.debug("=== companyCode = " + companyCode);

        try {

            String companyDecode = URLDecoder.decode( companyCode, "UTF-8" );
            logger.debug("=== companyCode = " + companyDecode);

            if (id != null && HelperFunc.isNotEmpty(companyDecode)) {
                CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(companyDecode.trim());
                if (item != null) {
                    WorkerEntry worker = workerEntryDAO.getByExternalId(id, item.getCompanyId());
                    if (worker != null) {
                        return new WorkerRecord(worker);
                    }
                }
            }
        } catch (Exception e) {
            logger.error ("error while get worker", e);
        }

        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get.department")
    public @ResponseBody DepartmentRecord getDepartment() {

        logger.debug("=== getDepartment ===");

        DepartmentRecord departmentRecord = new DepartmentRecord();
        departmentRecord.setCompanyCode("protei");
        departmentRecord.setDepartmentId(1L);
        departmentRecord.setDepartmentName("Department Name");
        departmentRecord.setHeadId(1L);
        departmentRecord.setParentId(null);
        return departmentRecord;
    }

/*
    @RequestMapping(method = RequestMethod.GET, value = "/get.result")
    public @ResponseBody ServiceResult getServiceResult() {

        return ServiceResult.failResult (En_ErrorCode.NOT_CREATE.getCode (), En_ErrorCode.NOT_CREATE.getMessage (), 0L);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get.results")
    public @ResponseBody ServiceResultList getServiceResultList() {

        ServiceResultList serviceResultList = new ServiceResultList();
        serviceResultList.getServiceResults().add(ServiceResult.failResult (En_ErrorCode.NOT_CREATE.getCode (), En_ErrorCode.NOT_CREATE.getMessage (), 0L));
        serviceResultList.getServiceResults().add(ServiceResult.successResult(1L));
        return serviceResultList;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get.photo")
    public @ResponseBody Photo getPhoto() {

        Photo photo = new Photo();
        photo.setId(2L);
        byte[] bytes = { 0, 0, 0, 25 };
        photo.setPhoto(bytes);
        return photo;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get.photos")
    public @ResponseBody PhotoList getPhotos() {

        PhotoList photoList = new PhotoList();
        Photo photo = new Photo();
        photo.setId(2L);
        byte[] bytes = { 0, 0, 0, 25 };
        photo.setPhoto(bytes);
        photoList.getPhotos().add(photo);
        return photoList;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get.ids")
    public @ResponseBody IdList getIds() {

        IdList list = new IdList();
        list.getIds().add(1L);
        list.getIds().add(2L);
        list.getIds().add(3L);
        return list;
    }
*/

    @RequestMapping(method = RequestMethod.GET, value = "/get.persons")
    public @ResponseBody WorkerRecordList getPersons(@RequestParam(name = "expr") String expr) {

        logger.debug("=== getPersons ===");

        WorkerRecordList workers = new WorkerRecordList();

        try {
            String exprDecode = URLDecoder.decode( expr, "UTF-8" );
            exprDecode = Tm_SqlQueryHelper.makeLikeArgEx (exprDecode);
            logger.debug("=== expr = " + exprDecode);

            EmployeeQuery query = new EmployeeQuery(null, false, exprDecode,
                    En_SortField.person_full_name, En_SortDir.ASC);
            query.setSearchByContactInfo(false);
            List<Person> persons = personDAO.getEmployees(query);

            persons.forEach(person ->
                    workers.getWorkerRecords().add(new WorkerRecord(person))
            );

        } catch (Exception e) {
            logger.error ("error while get workers", e);
        }

        return workers;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/add.worker")
    public @ResponseBody ServiceResult addWorker(@RequestBody WorkerRecord rec) {

        logger.debug("=== addWorker ===");

        try {

            BeanInfo infoRec = Introspector.getBeanInfo(rec.getClass());

            logger.debug("=== properties from 1C ===");
            for (PropertyDescriptor pl : infoRec.getPropertyDescriptors()) {
                logger.debug(pl.getDisplayName() + " = " + (pl.getReadMethod() != null ? pl.getReadMethod().invoke(rec,null) : null));
            }
            logger.debug("==========================");

            return transactionTemplate.execute(transactionStatus -> {

                try {

                    ServiceResult isValid = isValidWorkerRecord (rec);
                    if (!isValid.isSuccess ()) {
                        logger.debug("=== error result, " + isValid.getErrInfo());
                        return isValid;
                    }

                    CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(rec.getCompanyCode().trim());
                    if (item == null) {
                        logger.debug("=== error result, " + En_ErrorCode.UNKNOWN_COMP.getMessage());
                        return ServiceResult.failResult(En_ErrorCode.UNKNOWN_COMP.getCode(), En_ErrorCode.UNKNOWN_COMP.getMessage(), rec.getId());
                    }

                    CompanyDepartment department = companyDepartmentDAO.getByExternalId(rec.getDepartmentId (), item.getCompanyId ());
                    if (department == null) {
                        logger.debug("=== error result, " + En_ErrorCode.UNKNOWN_DEP.getMessage());
                        logger.debug(En_ErrorCode.UNKNOWN_DEP.getMessage());
                        return ServiceResult.failResult(En_ErrorCode.UNKNOWN_DEP.getCode(), En_ErrorCode.UNKNOWN_DEP.getMessage(), null);
                    }

                    if (workerEntryDAO.checkExistsByExternalId(rec.getWorkerId (), item.getCompanyId ())) {
                        logger.debug("=== error result, " + En_ErrorCode.EXIST_WOR.getMessage());
                        return ServiceResult.failResult(En_ErrorCode.EXIST_WOR.getCode(), En_ErrorCode.EXIST_WOR.getMessage(), null);
                    }

                    Person person = null;
                    if (rec.getId () != null) {
                        person = personDAO.get (rec.getId ());
                    }

                    if (person == null) {
                        person = new Person ();
                        person.setCreated (new Date());
                        person.setCreator ("portal-api@" + Inet4Address.getLocalHost ().getHostAddress());
                        person.setCompanyId (item.getCompanyId ());
                    }

                    copy (rec, person);

                    if (person.getId () == null) {
                        personDAO.persist(person);
                    } else {
                        personDAO.merge(person);
                    }

                    WorkerPosition position = getValidPosition (rec.getPositionName (), item.getCompanyId ());

                    WorkerEntry worker = new WorkerEntry ();
                    worker.setCreated (new Date ());
                    worker.setPersonId (person.getId ());
                    worker.setCompanyId (item.getCompanyId ());
                    worker.setDepartmentId (department.getId ());
                    worker.setPositionId (position.getId ());
                    worker.setHireDate (rec.getHireDate () != null && rec.getHireDate ().trim ().length () > 0 ? HelperService.DATE.parse (rec.getHireDate ()) : null);
                    worker.setHireOrderNo (rec.getHireOrderNo () != null && rec.getHireOrderNo ().trim ().length () > 0 ? rec.getHireOrderNo ().trim () : null);
                    worker.setActiveFlag (rec.getActive ());
                    worker.setExternalId (rec.getWorkerId ());

                    workerEntryDAO.persist (worker);

                    //wsMigrationManager.persistPerson (person);

                    logger.debug("=== success result, workerRowId = " + worker.getId());
                    return ServiceResult.successResult (person.getId ());

                } catch (Exception e) {
                    logger.error ("error while transaction execution", e);
                    throw new RuntimeException();
                }
            });


        } catch (Exception e) {
            logger.error ("error while add worker's record", e);
            return ServiceResult.failResult (En_ErrorCode.NOT_CREATE.getCode (), En_ErrorCode.NOT_CREATE.getMessage (), null);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/update.worker")
    public @ResponseBody ServiceResult updateWorker(@RequestBody WorkerRecord rec) {

        logger.debug("=== updateWorker ===");

        try {

            BeanInfo infoRec = Introspector.getBeanInfo(rec.getClass());

            logger.debug("=== properties from 1C ===");
            for (PropertyDescriptor pl : infoRec.getPropertyDescriptors()) {
                logger.debug(pl.getDisplayName() + " = " + (pl.getReadMethod() != null ? pl.getReadMethod().invoke(rec,null) : null));
            }
            logger.debug("==========================");

            return transactionTemplate.execute(transactionStatus -> {

                try {
                    ServiceResult isValid = isValidWorkerRecord (rec);
                    if (!isValid.isSuccess ()) {
                        logger.debug("=== error result, " + isValid.getErrInfo());
                        return isValid;
                    }

                    if (rec.getId () == null || rec.getId () < 0) {
                        logger.debug("=== error result, " + En_ErrorCode.EMPTY_PER_ID.getMessage());
                        return ServiceResult.failResult(En_ErrorCode.EMPTY_PER_ID.getCode(), En_ErrorCode.EMPTY_PER_ID.getMessage(), rec.getId());
                    }

                    CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(rec.getCompanyCode ().trim ());
                    if (item == null) {
                        logger.debug("=== error result, " + En_ErrorCode.UNKNOWN_COMP.getMessage());
                        return ServiceResult.failResult(En_ErrorCode.UNKNOWN_COMP.getCode(), En_ErrorCode.UNKNOWN_COMP.getMessage(), rec.getId());
                    }

                    CompanyDepartment department = companyDepartmentDAO.getByExternalId(rec.getDepartmentId (), item.getCompanyId ());
                    if (department == null) {
                        logger.debug("=== error result, " + En_ErrorCode.UNKNOWN_DEP.getMessage());
                        return ServiceResult.failResult(En_ErrorCode.UNKNOWN_DEP.getCode(), En_ErrorCode.UNKNOWN_DEP.getMessage(), null);
                    }

                    Person person = personDAO.get (rec.getId ());
                    if (person == null) {
                        logger.debug("=== error result, " + En_ErrorCode.UNKNOWN_PER.getMessage());
                        return ServiceResult.failResult(En_ErrorCode.UNKNOWN_PER.getCode(), En_ErrorCode.UNKNOWN_PER.getMessage(), null);
                    }

                    WorkerEntry worker = workerEntryDAO.getByExternalId(rec.getWorkerId (), item.getCompanyId ());
                    if (worker == null || !worker.getPersonId().equals(person.getId ())) {
                        logger.debug("=== error result, " + En_ErrorCode.UNKNOWN_WOR.getMessage());
                        return ServiceResult.failResult(En_ErrorCode.UNKNOWN_WOR.getCode(), En_ErrorCode.UNKNOWN_WOR.getMessage(), null);
                    }

                    copy (rec, person);

                    personDAO.merge (person);

                    if (rec.isFired ()) {

                        logger.debug("=== fireWorker ===");

                        workerEntryDAO.remove (worker);

                        if (!workerEntryDAO.checkExistsByPersonId(person.getId())) {
                            person.setFired (true);
                            personDAO.merge (person);
                        }

                        logger.debug("=== success result, workerRowId = " + worker.getId());
                        return ServiceResult.successResult (person.getId ());
                    }

                    WorkerPosition position = getValidPosition (rec.getPositionName (), item.getCompanyId ());

                    worker.setDepartmentId (department.getId ());

                    worker.setPositionId (position.getId ());
                    worker.setHireDate (rec.getHireDate () != null && rec.getHireDate ().trim ().length () > 0 ? HelperService.DATE.parse (rec.getHireDate ()) : null);
                    worker.setHireOrderNo (rec.getHireOrderNo () != null && rec.getHireOrderNo ().trim ().length () > 0 ? rec.getHireOrderNo ().trim () : null);
                    worker.setActiveFlag (rec.getActive ());

                    workerEntryDAO.merge (worker);

                    //wsMigrationManager.mergePerson (person);

                    logger.debug("=== success result, workerRowId = " + worker.getId());
                    return ServiceResult.successResult (person.getId ());

                } catch (Exception e) {
                    logger.error ("error while transaction execution", e);
                    throw new RuntimeException();
                }
            });

        } catch (Exception e) {
            logger.error ("error while update worker's record", e);
            return ServiceResult.failResult (En_ErrorCode.NOT_UPDATE.getCode (), En_ErrorCode.NOT_UPDATE.getCode (), null);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/update.workers")
    public @ResponseBody ServiceResultList updateWorkers(@RequestBody WorkerRecordList list) {

        logger.debug("=== updateWorkers ===");

        ServiceResultList results = new ServiceResultList();

        try {
            if (list != null && list.getWorkerRecords() != null && !list.getWorkerRecords().isEmpty ()) {
                for (WorkerRecord wr : list.getWorkerRecords()) {
                    results.getServiceResults().add(updateWorker(wr));
                }
            }
        } catch (Exception e) {
            logger.error ("error while update workers", e);
        }
        return results;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete.worker")
    public @ResponseBody ServiceResult deleteWorker(@RequestParam(name = "externalId") Long externalId, @RequestParam(name = "companyCode") String companyCode) {

        logger.debug("=== deleteWorker ===");
        logger.debug("=== externalId = " + externalId);

        try {

            String companyDecode = URLDecoder.decode( companyCode, "UTF-8" );
            logger.debug("=== companyCode = " + companyDecode);

            return transactionTemplate.execute(transactionStatus -> {
                try {

                    if (HelperFunc.isEmpty(companyDecode))
                        return ServiceResult.failResult (En_ErrorCode.EMPTY_COMP_CODE.getCode (), En_ErrorCode.EMPTY_COMP_CODE.getMessage (), externalId);

                    if (externalId < 0) {
                        logger.debug("=== error result, " + En_ErrorCode.EMPTY_WOR_ID.getMessage());
                        return ServiceResult.failResult(En_ErrorCode.EMPTY_WOR_ID.getCode(), En_ErrorCode.EMPTY_WOR_ID.getMessage(), externalId);
                    }

                    CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(companyDecode.trim ());
                    if (item == null) {
                        logger.debug("=== error result, " + En_ErrorCode.UNKNOWN_COMP.getMessage());
                        return ServiceResult.failResult(En_ErrorCode.UNKNOWN_COMP.getCode(), En_ErrorCode.UNKNOWN_COMP.getMessage(), externalId);
                    }

                    WorkerEntry worker = workerEntryDAO.getByExternalId(externalId, item.getCompanyId ());
                    if (worker == null) {
                        logger.debug("=== error result, " + En_ErrorCode.UNKNOWN_WOR.getMessage());
                        return ServiceResult.failResult(En_ErrorCode.UNKNOWN_WOR.getCode(), En_ErrorCode.UNKNOWN_WOR.getMessage(), null);
                    }

                    Long personId = worker.getPersonId ();

                    workerEntryDAO.remove (worker);

                    if (!workerEntryDAO.checkExistsByPersonId(personId)){
                        Person person = personDAO.get (personId);
                        person.setDeleted (true);
                        personDAO.merge (person);
                        //wsMigrationManager.removePerson (person);
                    }
                    logger.debug("=== success result, workerRowId = " + worker.getId());
                    return ServiceResult.successResult (worker.getExternalId ());

                } catch (Exception e) {
                    logger.error ("error while transaction execution", e);
                    throw new RuntimeException();
                }
            });

        } catch (Exception e) {
            logger.error ("error while remove worker's record", e);
            return ServiceResult.failResult (En_ErrorCode.NOT_DELETE.getCode (), En_ErrorCode.NOT_DELETE.getMessage (), null);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/update.photo")
    public @ResponseBody ServiceResult updatePhoto(@RequestBody Photo photo) {

        logger.debug("=== updatePhoto ===");
        logger.debug("=== properties from 1C ===");
        logger.debug("personId = " + (photo == null ? null : photo.getId()));
        logger.debug("photo's length = " + (photo == null || photo.getContent() == null ? null : photo.getContent().length()));
        logger.debug("photo's content in Base64 = " + (photo == null ? null : photo.getContent()));
        logger.debug("==========================");

        OutputStream out = null;

        try {

            if (photo == null) {
                logger.debug("=== error result, " + En_ErrorCode.EMPTY_PHOTO.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EMPTY_PHOTO.getCode(), En_ErrorCode.EMPTY_PHOTO.getMessage(), photo.getId());
            }

            if (photo.getId() < 0) {
                logger.debug("=== error result, " + En_ErrorCode.EMPTY_PER_ID.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EMPTY_PER_ID.getCode(), En_ErrorCode.EMPTY_PER_ID.getMessage(), photo.getId());
            }

            if (HelperFunc.isEmpty(photo.getContent())) {
                logger.debug("=== error result, " + En_ErrorCode.EMPTY_PHOTO_CONTENT.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EMPTY_PHOTO_CONTENT.getCode(), En_ErrorCode.EMPTY_PHOTO_CONTENT.getMessage(), photo.getId());
            }

            Person person = personDAO.get (photo.getId());
            if (person == null) {
                logger.debug("=== error result, " + En_ErrorCode.UNKNOWN_PER.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_PER.getCode(), En_ErrorCode.UNKNOWN_PER.getMessage(), photo.getId());
            }

            String fileName = WSConfig.getInstance ().getDirPhotos () + photo.getId() + ".jpg";
            logger.debug("=== fileName = " + fileName);

            out = new BufferedOutputStream(new FileOutputStream(fileName));
            out.write (Base64.getDecoder().decode(photo.getContent()));

            logger.debug("=== success result, personId = " + photo.getId());
            return ServiceResult.successResult (photo.getId());

        } catch (Exception e) {
            logger.error ("error while update photo", e);
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {}
        }

        return ServiceResult.failResult (En_ErrorCode.NOT_UPDATE.getCode (), En_ErrorCode.NOT_UPDATE.getMessage (), null);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/get.photos")
    public @ResponseBody PhotoList getPhotos(@RequestBody IdList list) {

        logger.debug("=== getPhotos ===");
        logger.debug("=== properties from 1C ===");
        logger.debug("list = " + list.getIds());
        logger.debug("==========================");

        InputStream in = null;
        PhotoList photos = new PhotoList();

        try {

            for (Long id : list.getIds()) {

                logger.debug("=== personId = " + id);
                String fileName = WSConfig.getInstance ().getDirPhotos () + id + ".jpg";
                logger.debug("=== fileName = " + fileName);
                File file = new File(fileName);
                if (file.exists()) {
                    in = new BufferedInputStream(new FileInputStream(file));
                    Long size = file.length();
                    byte[] buf = new byte[size.intValue()];
                    in.read(buf);

                    Photo photo = new Photo ();
                    photo.setId (id);
                    photo.setContent (Base64.getEncoder().encodeToString(buf));
                    photos.getPhotos().add (photo);

                    logger.debug("=== file exists");
                    logger.debug("=== photo's length = " + (photo.getContent().length()));
                    logger.debug("=== photo's content in Base64 = " + photo.getContent());
                } else {
                    logger.debug ("=== file doesn't exist");
                }
            }

        } catch (Exception e) {
            logger.error ("error while get photos", e);
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (Exception e) {}
        }

        logger.debug("=== result, size of photo's list = " + photos.getPhotos().size());
        return photos;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/update.department")
    public @ResponseBody ServiceResult updateDepartment(@RequestBody DepartmentRecord rec) {

        logger.debug("=== updateDepartment ===");

        try {

            BeanInfo infoRec = Introspector.getBeanInfo(rec.getClass());

            logger.debug("=== properties from 1C ===");
            for (PropertyDescriptor pl : infoRec.getPropertyDescriptors()) {
                logger.debug(pl.getDisplayName() + " = " + (pl.getReadMethod() != null ? pl.getReadMethod().invoke(rec,null) + ";" : "null;"));
            }
            logger.debug("==========================");

            ServiceResult isValid = isValidDepartmentRecord (rec);
            if (!isValid.isSuccess ()) {
                logger.debug("=== error result, " + isValid.getErrInfo());
                return isValid;
            }

            CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(rec.getCompanyCode ().trim ());
            if (item == null) {
                logger.debug("=== error result, " + En_ErrorCode.UNKNOWN_COMP.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_COMP.getCode(), En_ErrorCode.UNKNOWN_COMP.getMessage(), rec.getDepartmentId());
            }

            if (rec.getParentId () != null && !companyDepartmentDAO.checkExistsByExternalId(rec.getParentId (), item.getCompanyId ())) {
                logger.debug("=== error result, " + En_ErrorCode.UNKNOWN_PAR_DEP.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_PAR_DEP.getCode(), En_ErrorCode.UNKNOWN_PAR_DEP.getMessage(), null);
            }

            CompanyDepartment department = companyDepartmentDAO.getByExternalId(rec.getDepartmentId (), item.getCompanyId ());
            if (department == null) {
                department = new CompanyDepartment ();
                department.setCreated (new Date ());
                department.setCompanyId (item.getCompanyId ());
                department.setTypeId (1);
                department.setExternalId (rec.getDepartmentId ());
            }

            department.setName (rec.getDepartmentName ().trim ());
            department.setParentId (rec.getParentId ());
            department.setHeadId (rec.getHeadId ());

            if (department.getId () == null)
                companyDepartmentDAO.persist (department);
            else
                companyDepartmentDAO.merge (department);

            logger.debug("=== success result, departmentRowId = " + department.getId());
            return ServiceResult.successResult (department.getExternalId ());

        } catch (Exception e) {
            logger.error ("error while update department's record", e);
        }

        return ServiceResult.failResult (En_ErrorCode.NOT_UPDATE.getCode (), En_ErrorCode.NOT_UPDATE.getMessage (), null);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete.department")
    public @ResponseBody ServiceResult deleteDepartment(@RequestParam(name = "externalId") Long externalId, @RequestParam(name = "companyCode") String companyCode) {

        logger.debug("=== deleteDepartment ===");
        logger.debug("=== externalId = " + externalId);

        try {

            String companyDecode = URLDecoder.decode( companyCode, "UTF-8" );
            logger.debug("=== companyCode = " + companyDecode);

            if (HelperFunc.isEmpty(companyDecode))
                return ServiceResult.failResult (En_ErrorCode.EMPTY_COMP_CODE.getCode (), En_ErrorCode.EMPTY_COMP_CODE.getMessage (), externalId);

            if (externalId < 0) {
                logger.debug("=== error result, " + En_ErrorCode.EMPTY_DEP_ID.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EMPTY_DEP_ID.getCode(), En_ErrorCode.EMPTY_DEP_ID.getMessage(), externalId);
            }

            CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(companyDecode.trim ());
            if (item == null) {
                logger.debug("=== error result, " + En_ErrorCode.UNKNOWN_COMP.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_COMP.getCode(), En_ErrorCode.UNKNOWN_COMP.getMessage(), externalId);
            }

            CompanyDepartment department = companyDepartmentDAO.getByExternalId(externalId, item.getCompanyId ());
            if (department == null) {
                logger.debug("=== error result, " + En_ErrorCode.UNKNOWN_DEP.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_DEP.getCode(), En_ErrorCode.UNKNOWN_DEP.getMessage(), null);
            }

            if (companyDepartmentDAO.checkExistsByParentId(department.getId ())) {
                logger.debug("=== error result, " + En_ErrorCode.EXIST_CHILD_DEP.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EXIST_CHILD_DEP.getCode(), En_ErrorCode.EXIST_CHILD_DEP.getMessage(), null);
            }

            if (workerEntryDAO.checkExistsByDepId(department.getId ())) {
                logger.debug("=== error result, " + En_ErrorCode.EXIST_DEP_WOR.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EXIST_DEP_WOR.getCode(), En_ErrorCode.EXIST_DEP_WOR.getMessage(), null);
            }

            companyDepartmentDAO.remove (department);

            logger.debug("=== success result, departmentRowId = " + department.getId());
            return ServiceResult.successResult (department.getExternalId ());

        } catch (Exception e) {
            logger.error ("error while remove department's record", e);
        }

        return ServiceResult.failResult (En_ErrorCode.NOT_DELETE.getCode (), En_ErrorCode.NOT_DELETE.getMessage (), null);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/update.position")
    public @ResponseBody ServiceResult updatePosition(@RequestParam(name = "oldName") String oldName, @RequestParam(name = "newName") String newName, @RequestParam(name = "companyCode") String companyCode) {

        logger.debug("=== updatePosition ===");
        logger.debug("=== oldName = " + oldName);

        try {

            String oldNameDecode = URLDecoder.decode( oldName, "UTF-8" );
            String newNameDecode = URLDecoder.decode( newName, "UTF-8" );
            String companyDecode = URLDecoder.decode( companyCode, "UTF-8" );

            logger.debug("=== oldName = " + oldNameDecode);
            logger.debug("=== newName = " + newNameDecode);
            logger.debug("=== companyCode = " + companyDecode);

            if (HelperFunc.isEmpty(companyDecode))
                return ServiceResult.failResult (En_ErrorCode.EMPTY_COMP_CODE.getCode (), En_ErrorCode.EMPTY_COMP_CODE.getMessage (), null);

            if (HelperFunc.isEmpty(oldNameDecode) || HelperFunc.isEmpty(newNameDecode)) {
                logger.debug("=== error result, " + En_ErrorCode.EMPTY_POS.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EMPTY_POS.getCode(), En_ErrorCode.EMPTY_POS.getMessage(), null);
            }

            CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(companyDecode.trim ());
            if (item == null) {
                logger.debug("=== error result, " + En_ErrorCode.UNKNOWN_COMP.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_COMP.getCode(), En_ErrorCode.UNKNOWN_COMP.getMessage(), null);
            }

            WorkerPosition existsPosition = workerPositionDAO.getByName(newNameDecode, item.getCompanyId());
            if (existsPosition != null) {
                logger.debug("=== error result, " + En_ErrorCode.EXIST_POS.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EXIST_POS.getCode(), En_ErrorCode.EXIST_POS.getMessage(), null);
            }

            WorkerPosition position = workerPositionDAO.getByName(oldNameDecode, item.getCompanyId());
            if (position == null) {
                logger.debug("=== error result, " + En_ErrorCode.UNKNOWN_POS.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_POS.getCode(), En_ErrorCode.UNKNOWN_POS.getMessage(), null);
            }

            position.setName(newNameDecode.trim());

            workerPositionDAO.merge(position);

            logger.debug("=== success result, positionRowId = " + position.getId());
            return ServiceResult.successResult (position.getId());

        } catch (Exception e) {
            logger.error ("error while update position's record", e);
        }

        return ServiceResult.failResult (En_ErrorCode.NOT_UPDATE.getCode (), En_ErrorCode.NOT_UPDATE.getMessage (), null);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete.position")
    public @ResponseBody ServiceResult deletePosition(@RequestParam(name = "name") String name, @RequestParam(name = "companyCode") String companyCode) {

        logger.debug("=== deletePosition ===");

        try {

            String nameDecode = URLDecoder.decode( name, "UTF-8" );
            String companyDecode = URLDecoder.decode( companyCode, "UTF-8" );
            logger.debug("=== name = " + nameDecode);
            logger.debug("=== companyCode = " + companyDecode);

            if (HelperFunc.isEmpty(companyDecode))
                return ServiceResult.failResult (En_ErrorCode.EMPTY_COMP_CODE.getCode (), En_ErrorCode.EMPTY_COMP_CODE.getMessage (), null);

            if (HelperFunc.isEmpty(nameDecode)) {
                logger.debug("=== error result, " + En_ErrorCode.EMPTY_POS.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EMPTY_POS.getCode(), En_ErrorCode.EMPTY_POS.getMessage(), null);
            }

            CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(companyDecode.trim ());
            if (item == null) {
                logger.debug("=== error result, " + En_ErrorCode.UNKNOWN_COMP.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_COMP.getCode(), En_ErrorCode.UNKNOWN_COMP.getMessage(), null);
            }

            WorkerPosition position = workerPositionDAO.getByName(nameDecode, item.getCompanyId ());
            if (position == null) {
                logger.debug("=== error result, " + En_ErrorCode.UNKNOWN_POS.getMessage());
                return ServiceResult.failResult(En_ErrorCode.UNKNOWN_POS.getCode(), En_ErrorCode.UNKNOWN_POS.getMessage(), null);
            }

            if (workerEntryDAO.checkExistsByPosId(position.getId ())) {
                logger.debug("=== error result, " + En_ErrorCode.EXIST_POS_WOR.getMessage());
                return ServiceResult.failResult(En_ErrorCode.EXIST_POS_WOR.getCode(), En_ErrorCode.EXIST_POS_WOR.getMessage(), null);
            }

            workerPositionDAO.remove (position);

            logger.debug("=== success result, positionRowId = " + position.getId());
            return ServiceResult.successResult (position.getId());

        } catch (Exception e) {
            logger.error ("error while remove position's record", e);
        }

        return ServiceResult.failResult (En_ErrorCode.NOT_DELETE.getCode (), En_ErrorCode.NOT_DELETE.getMessage (), null);
    }

    private ServiceResult isValidWorkerRecord(WorkerRecord rec) {

        if (HelperFunc.isEmpty(rec.getCompanyCode ()))
            return ServiceResult.failResult (En_ErrorCode.EMPTY_COMP_CODE.getCode (), En_ErrorCode.EMPTY_COMP_CODE.getMessage (), rec.getId ());

        if (rec.getDepartmentId () < 0)
            return ServiceResult.failResult (En_ErrorCode.EMPTY_DEP_ID.getCode (), En_ErrorCode.EMPTY_DEP_ID.getMessage (), rec.getId ());

        if (HelperFunc.isEmpty(rec.getPositionName ()))
            return ServiceResult.failResult (En_ErrorCode.EMPTY_POS.getCode (), En_ErrorCode.EMPTY_POS.getMessage (), rec.getId ());

        if (rec.getWorkerId () < 0)
            return ServiceResult.failResult (En_ErrorCode.EMPTY_WOR_ID.getCode (), En_ErrorCode.EMPTY_WOR_ID.getMessage (), rec.getId ());

        if (HelperFunc.isEmpty(rec.getFirstName ()))
            return ServiceResult.failResult (En_ErrorCode.EMPTY_FIRST_NAME.getCode (), En_ErrorCode.EMPTY_FIRST_NAME.getMessage (), rec.getId ());

        if (HelperFunc.isEmpty(rec.getLastName ()))
            return ServiceResult.failResult (En_ErrorCode.EMPTY_LAST_NAME.getCode (), En_ErrorCode.EMPTY_LAST_NAME.getMessage (), rec.getId ());

        if (HelperFunc.isNotEmpty(rec.getIpAddress ()) &&
                !rec.getIpAddress ().trim ().matches("^[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}$"))
            return ServiceResult.failResult (En_ErrorCode.INV_FORMAT_IP.getCode (), En_ErrorCode.INV_FORMAT_IP.getMessage (), rec.getId ());

        return ServiceResult.successResult (rec.getId ());
    }

    private ServiceResult isValidDepartmentRecord(DepartmentRecord rec) {

        if (HelperFunc.isEmpty(rec.getCompanyCode ()))
            return ServiceResult.failResult (En_ErrorCode.EMPTY_COMP_CODE.getCode (), En_ErrorCode.EMPTY_COMP_CODE.getMessage (), rec.getDepartmentId ());

        if (rec.getDepartmentId () < 0)
            return ServiceResult.failResult (En_ErrorCode.EMPTY_DEP_ID.getCode (), En_ErrorCode.EMPTY_DEP_ID.getMessage (), rec.getDepartmentId ());

        if (HelperFunc.isEmpty(rec.getDepartmentName ()))
            return ServiceResult.failResult (En_ErrorCode.EMPTY_DEP_NAME.getCode (), En_ErrorCode.EMPTY_DEP_NAME.getMessage (), rec.getDepartmentId ());

        if (rec.getHeadId () != null && !personDAO.checkExistsByCondition ("id=?", rec.getHeadId ()))
            return ServiceResult.failResult (En_ErrorCode.UNKNOWN_HEAD_DEP.getCode (), En_ErrorCode.UNKNOWN_HEAD_DEP.getMessage (), null);

        return ServiceResult.successResult (rec.getDepartmentId ());
    }

    private void copy(WorkerRecord rec, Person person) throws ParseException {

        person.setFirstName (rec.getFirstName ().trim ());
        person.setLastName (rec.getLastName ().trim ());
        person.setSecondName (rec.getSecondName () != null && rec.getSecondName ().trim ().length () > 0 ? rec.getSecondName ().trim () : null);
        person.setDisplayName (HelperService.generateDisplayName (person.getFirstName (), person.getLastName (), person.getSecondName ()));
        person.setDisplayShortName (HelperService.generateDisplayShortName (person.getFirstName (), person.getLastName (), person.getSecondName ()));
        person.setGender (rec.getSex () == null ? En_Gender.UNDEFINED : rec.getSex () == 1 ? En_Gender.MALE : En_Gender.FEMALE);
        person.setBirthday (rec.getBirthday() != null && rec.getBirthday().trim ().length () > 0 ? HelperService.DATE.parse(rec.getBirthday()) : null);
        person.setIpAddress (rec.getIpAddress () != null && rec.getIpAddress ().trim ().length () > 0 ? rec.getIpAddress ().trim () : null);
        person.setPassportInfo (rec.getPassportInfo () != null && rec.getPassportInfo ().trim ().length () > 0 ? rec.getPassportInfo ().trim () : null);
        person.setInfo (rec.getInfo () != null && rec.getInfo ().trim ().length () > 0 ? rec.getInfo ().trim () : null);
        person.setDeleted (rec.isDeleted ());
        person.setFired(rec.isFired());

        PlainContactInfoFacade contactInfoFacade = new PlainContactInfoFacade(person.getContactInfo());
        contactInfoFacade.setWorkPhone (rec.getPhoneWork () != null && rec.getPhoneWork ().trim ().length () > 0 ? rec.getPhoneWork ().trim () : null);
        contactInfoFacade.setMobilePhone (rec.getPhoneMobile () != null && rec.getPhoneMobile ().trim ().length () > 0 ? rec.getPhoneMobile ().trim () : null);
        contactInfoFacade.setHomePhone (rec.getPhoneHome () != null && rec.getPhoneHome ().trim ().length () > 0 ? rec.getPhoneHome ().trim () : null);
        contactInfoFacade.setLegalAddress (rec.getAddress () != null && rec.getAddress ().trim ().length () > 0 ? rec.getAddress ().trim () : null);
        contactInfoFacade.setHomeAddress (rec.getAddressHome () != null && rec.getAddressHome ().trim ().length () > 0 ? rec.getAddressHome ().trim () : null);
        contactInfoFacade.setEmail (rec.getEmail () != null && rec.getEmail ().trim ().length () > 0 ? rec.getEmail ().trim () : null);
        contactInfoFacade.setEmail_own (rec.getEmailOwn () != null && rec.getEmailOwn ().trim ().length () > 0 ? rec.getEmailOwn ().trim () : null);
        contactInfoFacade.setFax (rec.getFax () != null && rec.getFax ().trim ().length () > 0 ? rec.getFax ().trim () : null);

        //person.setDepartment ();
        //person.setPosition ();

    }

    private WorkerPosition getValidPosition(String positionName, Long companyId) {

        WorkerPosition position = workerPositionDAO.getByName(positionName, companyId);

        if (position != null)
            return position;

        position = new WorkerPosition ();
        position.setCompanyId (companyId);
        position.setName (positionName.trim ());
        Long id = workerPositionDAO.persist (position);
        position.setId (id);

        return position;
    }
}
