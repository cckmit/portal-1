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
import java.nio.charset.Charset;
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
    public @ResponseBody WorkerRecord getWorker(@RequestParam(name = "id") Long id, @RequestParam(name = "code") String code) {

        logger.debug("=== getWorker ===");
        logger.debug("=== id = " + id);
        logger.debug("=== code = " + code);

        try {
            if (id != null && HelperFunc.isNotEmpty(code)) {
                CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(code.trim());
                logger.debug("=== companyId = " + item.getCompanyId());
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
            expr = Tm_SqlQueryHelper.makeLikeArgEx (expr);
            logger.debug("=== expr = " + expr);

            EmployeeQuery query = new EmployeeQuery(null, false, expr,
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
                    if (!isValid.isSuccess ())
                        return isValid;

                    CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(rec.getCompanyCode().trim());
                    if (item == null)
                        return ServiceResult.failResult (En_ErrorCode.UNKNOWN_COMP.getCode (), En_ErrorCode.UNKNOWN_COMP.getMessage (), rec.getId ());

                    CompanyDepartment department = companyDepartmentDAO.getByExternalId(rec.getDepartmentId (), item.getCompanyId ());
                    if (department == null)
                        return ServiceResult.failResult (En_ErrorCode.UNKNOWN_DEP.getCode (), En_ErrorCode.UNKNOWN_DEP.getMessage (), null);

                    if (workerEntryDAO.checkExistsByExternalId(rec.getWorkerId (), item.getCompanyId ()))
                        return ServiceResult.failResult (En_ErrorCode.EXIST_WOR.getCode (), En_ErrorCode.EXIST_WOR.getMessage (), null);

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

                    WorkerPosition position = getValidPosition (rec.getPositionId (), rec.getPositionName (), item.getCompanyId ());

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
                    if (!isValid.isSuccess ())
                        return isValid;

                    if (rec.getId () == null || rec.getId () < 0)
                        return ServiceResult.failResult (En_ErrorCode.EMPTY_PER_ID.getCode (), En_ErrorCode.EMPTY_PER_ID.getMessage (), rec.getId ());

                    CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(rec.getCompanyCode ().trim ());
                    if (item == null)
                        return ServiceResult.failResult (En_ErrorCode.UNKNOWN_COMP.getCode (), En_ErrorCode.UNKNOWN_COMP.getMessage (), rec.getId ());

                    CompanyDepartment department = companyDepartmentDAO.getByExternalId(rec.getDepartmentId (), item.getCompanyId ());
                    if (department == null)
                        return ServiceResult.failResult (En_ErrorCode.UNKNOWN_DEP.getCode (), En_ErrorCode.UNKNOWN_DEP.getMessage (), null);

                    Person person = personDAO.get (rec.getId ());
                    if (person == null)
                        return ServiceResult.failResult (En_ErrorCode.UNKNOWN_PER.getCode (), En_ErrorCode.UNKNOWN_PER.getMessage (), null);

                    logger.debug("=== fireWorker ===");

                    WorkerEntry worker = workerEntryDAO.getByExternalId(rec.getWorkerId (), item.getCompanyId ());
                    if (worker == null || !worker.getPersonId().equals(person.getId ()))
                        return ServiceResult.failResult(En_ErrorCode.UNKNOWN_WOR.getCode(), En_ErrorCode.UNKNOWN_WOR.getMessage(), null);

                    copy (rec, person);

                    personDAO.merge (person);

                    if (rec.isFired ()) {

                        logger.debug("=== fireWorker ===");

                        workerEntryDAO.remove (worker);

                        if (!workerEntryDAO.checkExistsByPersonId(person.getId())) {
                            person.setFired (true);
                            personDAO.merge (person);
                        }

                        return ServiceResult.successResult (person.getId ());
                    }

                    WorkerPosition position = getValidPosition (rec.getPositionId (), rec.getPositionName (), item.getCompanyId ());

                    worker.setDepartmentId (department.getId ());
                    worker.setPositionId (position.getId ());
                    worker.setHireDate (rec.getHireDate () != null && rec.getHireDate ().trim ().length () > 0 ? HelperService.DATE.parse (rec.getHireDate ()) : null);
                    worker.setHireOrderNo (rec.getHireOrderNo () != null && rec.getHireOrderNo ().trim ().length () > 0 ? rec.getHireOrderNo ().trim () : null);
                    worker.setActiveFlag (rec.getActive ());

                    workerEntryDAO.merge (worker);

                    //wsMigrationManager.mergePerson (person);

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
        logger.debug("=== companyCode = " + companyCode);

        try {

            return transactionTemplate.execute(transactionStatus -> {
                try {
                    if (externalId < 0)
                        return ServiceResult.failResult (En_ErrorCode.EMPTY_WOR_ID.getCode (), En_ErrorCode.EMPTY_WOR_ID.getMessage (), externalId);

                    CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(companyCode.trim ());
                    if (item == null)
                        return ServiceResult.failResult (En_ErrorCode.UNKNOWN_COMP.getCode (), En_ErrorCode.UNKNOWN_COMP.getMessage (), externalId);

                    WorkerEntry worker = workerEntryDAO.getByExternalId(externalId, item.getCompanyId ());
                    if (worker == null)
                        return ServiceResult.failResult (En_ErrorCode.UNKNOWN_WOR.getCode (), En_ErrorCode.UNKNOWN_WOR.getMessage (), null);

                    Long personId = worker.getPersonId ();

                    workerEntryDAO.remove (worker);

                    if (!workerEntryDAO.checkExistsByPersonId(personId)){
                        Person person = personDAO.get (personId);
                        person.setDeleted (true);
                        personDAO.merge (person);
                        //wsMigrationManager.removePerson (person);
                    }

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

    @RequestMapping(method = RequestMethod.PUT, value = "/update.photo", headers = "Content-Type=image/*")
    public @ResponseBody ServiceResult updatePhoto(@RequestParam(name = "id") Long id, @RequestBody byte[] buf) {

        logger.debug("=== updatePhoto ===");
        logger.debug("=== properties from 1C ===");
        logger.debug("personId = " + id);
        logger.debug("photo's length = " + (buf != null ? buf.length : null));
        logger.debug("==========================");

        OutputStream out = null;

        try {

            if (id == null || id < 0)
                return ServiceResult.failResult (En_ErrorCode.EMPTY_PER_ID.getCode (), En_ErrorCode.EMPTY_PER_ID.getMessage (), id);

            if (buf == null)
                return ServiceResult.failResult (En_ErrorCode.EMPTY_PHOTO.getCode (), En_ErrorCode.EMPTY_PHOTO.getMessage (), id);

            Person person = personDAO.get (id);
            if (person == null)
                return ServiceResult.failResult (En_ErrorCode.UNKNOWN_PER.getCode (), En_ErrorCode.UNKNOWN_PER.getMessage (), id);

            String fileName = WSConfig.getInstance ().getDirPhotos () + id + ".jpg";
            logger.debug("=== fileName = " + fileName);

            out = new BufferedOutputStream(new FileOutputStream(fileName));
            out.write (buf);

            return ServiceResult.successResult (id);

        } catch (Exception e) {
            logger.error ("error while update photo", e);
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {}
        }

        return ServiceResult.failResult (En_ErrorCode.NOT_UPDATE.getCode (), En_ErrorCode.NOT_UPDATE.getMessage (), id);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/update.foto")
    public @ResponseBody ServiceResult updateFoto(@RequestParam(name = "id") Long id, @RequestBody byte[] base64string) {

        logger.debug("=== updateFoto ===");
        logger.debug("=== properties from 1C ===");
        logger.debug("personId = " + id);
        logger.debug("photo's length = " + (base64string != null ? base64string.length : null));
        logger.debug("==========================");

        OutputStream out = null;

        try {

            if (id == null || id < 0)
                return ServiceResult.failResult (En_ErrorCode.EMPTY_PER_ID.getCode (), En_ErrorCode.EMPTY_PER_ID.getMessage (), id);

            if (base64string == null)
                return ServiceResult.failResult (En_ErrorCode.EMPTY_PHOTO.getCode (), En_ErrorCode.EMPTY_PHOTO.getMessage (), id);

            Person person = personDAO.get (id);
            if (person == null)
                return ServiceResult.failResult (En_ErrorCode.UNKNOWN_PER.getCode (), En_ErrorCode.UNKNOWN_PER.getMessage (), id);

            String fileName = WSConfig.getInstance ().getDirPhotos () + id + ".jpg";
            logger.debug("=== fileName = " + fileName);

            out = new BufferedOutputStream(new FileOutputStream(fileName));
            out.write (Base64.getDecoder().decode(base64string));

            return ServiceResult.successResult (id);

        } catch (Exception e) {
            logger.error ("error while update photo", e);
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {}
        }

        return ServiceResult.failResult (En_ErrorCode.NOT_UPDATE.getCode (), En_ErrorCode.NOT_UPDATE.getMessage (), id);
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
                    //photo.setPhoto (buf);
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
            if (!isValid.isSuccess ())
                return isValid;

            CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(rec.getCompanyCode ().trim ());
            if (item == null)
                return ServiceResult.failResult (En_ErrorCode.EMPTY_COMP_CODE.getCode (), En_ErrorCode.EMPTY_COMP_CODE.getMessage (), rec.getDepartmentId ());

            if (rec.getParentId () != null && !companyDepartmentDAO.checkExistsByExternalId(rec.getParentId (), item.getCompanyId ()))
                return ServiceResult.failResult (En_ErrorCode.UNKNOWN_PAR_DEP.getCode (), En_ErrorCode.UNKNOWN_PAR_DEP.getMessage (), null);

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
        logger.debug("=== companyCode = " + companyCode);

        try {

            if (externalId < 0)
                return ServiceResult.failResult (En_ErrorCode.EMPTY_DEP_ID.getCode (), En_ErrorCode.EMPTY_DEP_ID.getMessage (), externalId);

            CompanyHomeGroupItem item = companyGroupHomeDAO.getByExternalCode(companyCode.trim ());
            if (item == null)
                return ServiceResult.failResult (En_ErrorCode.EMPTY_COMP_CODE.getCode (), En_ErrorCode.EMPTY_COMP_CODE.getMessage (), externalId);

            CompanyDepartment department = companyDepartmentDAO.getByExternalId(externalId, item.getCompanyId ());
            if (department == null)
                return ServiceResult.failResult (En_ErrorCode.UNKNOWN_DEP.getCode (), En_ErrorCode.UNKNOWN_DEP.getMessage (), null);

            if (companyDepartmentDAO.checkExistsByParentId(department.getId ()))
                return ServiceResult.failResult (En_ErrorCode.EXIST_CHILD_DEP.getCode (), En_ErrorCode.EXIST_CHILD_DEP.getMessage (), null);

            if (workerEntryDAO.checkExistsByDepId(department.getId ()))
                return ServiceResult.failResult (En_ErrorCode.EXIST_DEP_WOR.getCode (), En_ErrorCode.EXIST_DEP_WOR.getMessage (), null);

            companyDepartmentDAO.remove (department);

            return ServiceResult.successResult (department.getExternalId ());

        } catch (Exception e) {
            logger.error ("error while remove department's record", e);
        }

        return ServiceResult.failResult (En_ErrorCode.NOT_DELETE.getCode (), En_ErrorCode.NOT_DELETE.getMessage (), null);
    }

    private ServiceResult isValidWorkerRecord(WorkerRecord rec) {

        if (rec.getCompanyCode () == null || rec.getCompanyCode ().trim ().equals (""))
            return ServiceResult.failResult (En_ErrorCode.EMPTY_COMP_CODE.getCode (), En_ErrorCode.EMPTY_COMP_CODE.getMessage (), rec.getId ());

        if (rec.getDepartmentId () < 0)
            return ServiceResult.failResult (En_ErrorCode.EMPTY_DEP_ID.getCode (), En_ErrorCode.EMPTY_DEP_ID.getMessage (), rec.getId ());

        if (rec.getPositionId () < 0 ||
                rec.getPositionName () == null || rec.getPositionName ().trim ().length () < 1)
            return ServiceResult.failResult (En_ErrorCode.EMPTY_POS.getCode (), En_ErrorCode.EMPTY_POS.getMessage (), rec.getId ());

        if (rec.getWorkerId () < 0)
            return ServiceResult.failResult (En_ErrorCode.EMPTY_WOR_ID.getCode (), En_ErrorCode.EMPTY_WOR_ID.getMessage (), rec.getId ());

        if (rec.getFirstName () == null || rec.getFirstName ().trim ().length () < 1)
            return ServiceResult.failResult (En_ErrorCode.EMPTY_FIRST_NAME.getCode (), En_ErrorCode.EMPTY_FIRST_NAME.getMessage (), rec.getId ());

        if (rec.getLastName () == null || rec.getLastName ().trim ().length () < 1)
            return ServiceResult.failResult (En_ErrorCode.EMPTY_LAST_NAME.getCode (), En_ErrorCode.EMPTY_LAST_NAME.getMessage (), rec.getId ());

        if (rec.getIpAddress () != null && !rec.getIpAddress ().trim ().equals("") &&
                !rec.getIpAddress ().trim ().matches("^[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}$"))
            return ServiceResult.failResult (En_ErrorCode.INV_FORMAT_IP.getCode (), En_ErrorCode.INV_FORMAT_IP.getMessage (), rec.getId ());

        return ServiceResult.successResult (rec.getId ());
    }

    private ServiceResult isValidDepartmentRecord(DepartmentRecord rec) {

        if (rec.getCompanyCode () == null || rec.getCompanyCode ().trim ().equals (""))
            return ServiceResult.failResult (En_ErrorCode.EMPTY_COMP_CODE.getCode (), En_ErrorCode.EMPTY_COMP_CODE.getMessage (), rec.getDepartmentId ());

        if (rec.getDepartmentId () < 0)
            return ServiceResult.failResult (En_ErrorCode.EMPTY_DEP_ID.getCode (), En_ErrorCode.EMPTY_DEP_ID.getMessage (), rec.getDepartmentId ());

        if (rec.getDepartmentName () == null || rec.getDepartmentName ().trim ().length () < 1)
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

    private WorkerPosition getValidPosition(Long positionId, String positionName, Long companyId) {

        WorkerPosition position = workerPositionDAO.getByExternalId(positionId, companyId);

        if (position == null) {
            position = new WorkerPosition ();
            position.setExternalId (positionId);
            position.setCompanyId (companyId);
        }
        position.setName (positionName.trim ());
        if (position.getId () == null) {
            Long id = workerPositionDAO.persist (position);
            position.setId (id);
        } else {
            workerPositionDAO.merge (position);
        }
        return position;
    }
}
