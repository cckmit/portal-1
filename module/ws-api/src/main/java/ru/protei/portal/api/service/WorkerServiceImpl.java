package ru.protei.portal.api.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import protei.sql.query.Tm_SqlQueryHelper;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.api.config.WSConfig;
import ru.protei.portal.api.model.*;
import ru.protei.portal.api.utils.HelperService;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.Photo;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.winter.jdbc.JdbcSort;

import javax.jws.WebService;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by turik on 18.08.16.
 */
@WebService(endpointInterface = "ru.protei.portal.api.service.WorkerService", serviceName = "WorkerService")
public class WorkerServiceImpl implements WorkerService {

    private static Logger logger = Logger.getLogger(WorkerServiceImpl.class);

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

    //@Autowired
    //private WSMigrationManager wsMigrationManager;

    /* todo: transaction + dao-methods + try-catch*/

    @Override
    public WorkerRecord getWorker(Long id) {

        logger.debug("=== getWorker ===");
        logger.debug("=== id = " + id);

        if (id != null) {
            Person person = personDAO.get (id);
            if (person != null) {
                return new WorkerRecord (person);
            }
        }

        return null;
    }

    @Override
    public List<WorkerRecord> getWorkers(String expr) {

        logger.debug("=== getWorkers ===");

        List<WorkerRecord> workers = new ArrayList<> ();
        expr = Tm_SqlQueryHelper.makeLikeArgEx (expr);

        logger.debug("=== expr = " + expr);

        List<Person> persons = personDAO.getListByCondition ("isdeleted=0 and (firstname like ? or lastname like ? or secondname like ?)",
                new JdbcSort (JdbcSort.Direction.ASC,"lastname,firstname,secondname"), expr, expr, expr);
        if (persons != null && !persons.isEmpty ())
            for (Person p : persons)
                workers.add (new WorkerRecord (p));

        return workers;
    }

    @Override
    public ServiceResult addWorker(WorkerRecord rec) {

        logger.debug("=== addWorker ===");

        try {

            BeanInfo infoRec = Introspector.getBeanInfo(rec.getClass());

            logger.debug("=== properties from 1C ===");
            for (PropertyDescriptor pl : infoRec.getPropertyDescriptors()) {
                logger.debug(pl.getDisplayName() + " = " + (pl.getReadMethod() != null ? pl.getReadMethod().invoke(rec,null) : null));
            }
            logger.debug("==========================");

            ServiceResult isValid = isValidWorkerRecord (rec);
            if (!isValid.isSuccess ())
                return isValid;

            CompanyHomeGroupItem item = companyGroupHomeDAO.getByCondition ("external_code=?", rec.getCompanyCode ().trim ());
            if (item == null)
                return ServiceResult.failResult (En_ErrorCode.UNKNOWN_COMP.getCode (), En_ErrorCode.UNKNOWN_COMP.getMessage (), rec.getId ());

            CompanyDepartment department = companyDepartmentDAO.getByCondition ("dep_extId=? and company_id=?", rec.getDepartmentId(), item.getCompanyId ());
            if (department == null)
                return ServiceResult.failResult (En_ErrorCode.UNKNOWN_DEP.getCode (), En_ErrorCode.UNKNOWN_DEP.getMessage (), null);

            Person person = null;
            if (rec.getId () != null) {
                person = personDAO.get (rec.getId ());
            }

            if (person == null) {
                person = personDAO.createNewPerson(item.getMainId());
//                person = new Person ();
//                person.setCreated (new Date ());
//                person.setCreator ("portal-api@" + Inet4Address.getLocalHost ().getHostAddress());
//                person.setCompanyId (item.getMainId());
            }

            copy (rec, person);

            if (person.getId () == null)
                personDAO.persist (person);
            else
                personDAO.merge (person);

            WorkerPosition position = getValidPosition (rec.getPositionName (), item.getCompanyId ());

            if (workerEntryDAO.checkExistsByCondition ("worker_extId=? and companyId=?", rec.getWorkerId(), item.getCompanyId ()))
                return ServiceResult.failResult (En_ErrorCode.EXIST_WOR.getCode (), En_ErrorCode.EXIST_WOR.getMessage (), null);

            WorkerEntry worker = new WorkerEntry ();
            worker.setCreated (new Date ());
            worker.setPersonId (person.getId ());
            worker.setCompanyId (item.getCompanyId ());
            worker.setDepartmentId (department.getId ());
            worker.setPositionId (position.getId ());
            worker.setHireDate (rec.getHireDate () != null && rec.getHireDate ().trim ().length () > 0 ? HelperService.DATE.parse (rec.getHireDate ()) : null);
            worker.setHireOrderNo (rec.getHireOrderNo () != null && rec.getHireOrderNo ().trim ().length () > 0 ? rec.getHireOrderNo ().trim () : null);
            worker.setActiveFlag (rec.getActive ());
            worker.setExternalId(rec.getWorkerId());

            workerEntryDAO.persist (worker);

            //wsMigrationManager.persistPerson (person);

            return ServiceResult.successResult (person.getId ());

        } catch (Exception e) {
            logger.error ("error while add worker's record", e);
        }

        return ServiceResult.failResult (En_ErrorCode.NOT_CREATE.getCode (), En_ErrorCode.NOT_CREATE.getMessage (), null);
    }

    @Override
    public ServiceResult updateWorker(WorkerRecord rec) {

        logger.debug("=== updateWorker ===");

        try {

            BeanInfo infoRec = Introspector.getBeanInfo(rec.getClass());

            logger.debug("=== properties from 1C ===");
            for (PropertyDescriptor pl : infoRec.getPropertyDescriptors()) {
                logger.debug(pl.getDisplayName() + " = " + (pl.getReadMethod() != null ? pl.getReadMethod().invoke(rec,null) : null));
            }
            logger.debug("==========================");

            ServiceResult isValid = isValidWorkerRecord (rec);
            if (!isValid.isSuccess ())
                return isValid;

            if (rec.getId () == null || rec.getId () < 0)
                return ServiceResult.failResult (En_ErrorCode.EMPTY_PER_ID.getCode (), En_ErrorCode.EMPTY_PER_ID.getMessage (), rec.getId ());

            CompanyHomeGroupItem item = companyGroupHomeDAO.getByCondition ("external_code=?", rec.getCompanyCode ().trim ());
            if (item == null)
                return ServiceResult.failResult (En_ErrorCode.UNKNOWN_COMP.getCode (), En_ErrorCode.UNKNOWN_COMP.getMessage (), rec.getId ());

            CompanyDepartment department = companyDepartmentDAO.getByCondition ("dep_extId=? and company_id=?", rec.getDepartmentId(), item.getCompanyId ());
            if (department == null)
                return ServiceResult.failResult (En_ErrorCode.UNKNOWN_DEP.getCode (), En_ErrorCode.UNKNOWN_DEP.getMessage (), null);

            Person person = personDAO.get (rec.getId ());
            if (person == null)
                return ServiceResult.failResult (En_ErrorCode.UNKNOWN_PER.getCode (), En_ErrorCode.UNKNOWN_PER.getMessage (), null);

            copy (rec, person);

            personDAO.merge (person);

            if (rec.isFired ()) {

                logger.debug("=== fireWorker ===");

                WorkerEntry worker = workerEntryDAO.getByCondition ("worker_extId=? and companyId=? and personId=?", rec.getWorkerId(), item.getCompanyId (), person.getId ());

                if (worker != null) {
                    workerEntryDAO.remove (worker);
                }

                List<WorkerEntry> list = workerEntryDAO.getListByCondition ("personId=?", person.getId ());
                if (list == null || list.isEmpty ()) {
                    person.setFired (true);
                    personDAO.merge (person);
                }

                return ServiceResult.successResult (person.getId ());
            }

            WorkerPosition position = getValidPosition (rec.getPositionName (), item.getCompanyId ());

            WorkerEntry worker = workerEntryDAO.getByCondition ("worker_extId=? and companyId=? and personId=?", rec.getWorkerId(), item.getCompanyId (), person.getId ());
            if (worker == null)
                return ServiceResult.failResult (En_ErrorCode.UNKNOWN_WOR.getCode (), En_ErrorCode.UNKNOWN_WOR.getMessage (), null);

            worker.setDepartmentId (department.getId ());
            worker.setPositionId (position.getId ());
            worker.setHireDate (rec.getHireDate () != null && rec.getHireDate ().trim ().length () > 0 ? HelperService.DATE.parse (rec.getHireDate ()) : null);
            worker.setHireOrderNo (rec.getHireOrderNo () != null && rec.getHireOrderNo ().trim ().length () > 0 ? rec.getHireOrderNo ().trim () : null);
            worker.setActiveFlag (rec.getActive ());

            workerEntryDAO.merge (worker);

            //wsMigrationManager.mergePerson (person);

            return ServiceResult.successResult (person.getId ());

        } catch (Exception e) {
            logger.error ("error while update worker's record", e);
        }

        return ServiceResult.failResult (En_ErrorCode.NOT_UPDATE.getCode (), En_ErrorCode.NOT_UPDATE.getCode (), null);
    }

    @Override
    public List<ServiceResult> updateWorkers(List<WorkerRecord> list) {

        logger.debug("=== updateWorkers ===");

        List<ServiceResult> results = new ArrayList<> ();

        if (list != null && !list.isEmpty ())
            for (WorkerRecord wr : list)
                results.add (updateWorker (wr));

        return results;
    }

    @Override
    public ServiceResult deleteWorker(WorkerRecord rec) {

        logger.debug("=== deleteWorker ===");

        try {

            BeanInfo infoRec = Introspector.getBeanInfo(rec.getClass());

            logger.debug("=== properties from 1C ===");
            for (PropertyDescriptor pl : infoRec.getPropertyDescriptors()) {
                logger.debug(pl.getDisplayName() + " = " + (pl.getReadMethod() != null ? pl.getReadMethod().invoke(rec,null) : null));
            }
            logger.debug("==========================");

            if (HelperFunc.isEmpty(rec.getWorkerId()))
                return ServiceResult.failResult (En_ErrorCode.EMPTY_WOR_ID.getCode (), En_ErrorCode.EMPTY_WOR_ID.getMessage (), null);

            CompanyHomeGroupItem item = companyGroupHomeDAO.getByCondition ("external_code=?", rec.getCompanyCode ().trim ());
            if (item == null)
                return ServiceResult.failResult (En_ErrorCode.UNKNOWN_COMP.getCode (), En_ErrorCode.UNKNOWN_COMP.getMessage (), rec.getId ());

            WorkerEntry worker = workerEntryDAO.getByCondition ("worker_extId=? and companyId=?", rec.getWorkerId(), item.getCompanyId ());
            if (worker == null)
                return ServiceResult.failResult (En_ErrorCode.UNKNOWN_WOR.getCode (), En_ErrorCode.UNKNOWN_WOR.getMessage (), null);

            Long personId = worker.getPersonId ();

            workerEntryDAO.remove (worker);

            List<WorkerEntry> list = workerEntryDAO.getListByCondition ("personId=?",personId);
            if (list == null || list.isEmpty ()){
                Person person = personDAO.get (personId);
                person.setDeleted (true);
                personDAO.merge (person);
                //wsMigrationManager.removePerson (person);
            }

            return ServiceResult.successResult (worker.getId());

        } catch (Exception e) {
            logger.error ("error while remove worker's record", e);
        }

        return ServiceResult.failResult (En_ErrorCode.NOT_DELETE.getCode (), En_ErrorCode.NOT_DELETE.getMessage (), null);
    }

    @Override
    public ServiceResult updatePhoto(Long id, byte[] buf) {

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

            String fileName = WSConfig.getInstance ().getDirPhotos () + id + ".jpg";
            logger.debug("=== fileName = " + fileName);

            out = new BufferedOutputStream(new FileOutputStream (fileName));
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

    @Override
    public List<Photo> getPhotos(List<Long> list) {

        logger.debug("=== getPhotos ===");
        logger.debug("=== properties from 1C ===");
        logger.debug("list = " + list);
        logger.debug("==========================");

        InputStream in = null;
        List<Photo> photos = new ArrayList<> ();

        try {

            for (Long id : list) {

                Photo photo = new Photo ();

                logger.debug("=== personId = " + id);
                String fileName = WSConfig.getInstance ().getDirPhotos () + id + ".jpg";
                logger.debug("=== fileName = " + fileName);
                File file = new File(fileName);
                if (file.exists()) {
                    in = new BufferedInputStream(new FileInputStream(file));
                    Long size = file.length();
                    byte[] buf = new byte[size.intValue()];
                    in.read(buf);
                    photo.setContent (new String(buf));
                    logger.debug("=== file exists");
                    logger.debug("=== photo's length = " + (buf != null ? buf.length : null));
                } else {
                    photo.setContent (null);
                    logger.debug ("=== file doesn't exist");
                }
                photo.setId (id);
                photos.add (photo);
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

    @Override
    public ServiceResult updateDepartment(DepartmentRecord rec) {

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

            CompanyHomeGroupItem item = companyGroupHomeDAO.getByCondition ("external_code=?", rec.getCompanyCode ().trim ());
            if (item == null)
                return ServiceResult.failResult (En_ErrorCode.EMPTY_COMP_CODE.getCode (), En_ErrorCode.EMPTY_COMP_CODE.getMessage (), null);

            CompanyDepartment parentDepartment = null;
            if (HelperFunc.isNotEmpty(rec.getParentId ())) {
                parentDepartment = companyDepartmentDAO.getByExternalId(rec.getParentId ().trim(), item.getCompanyId ());
                if (parentDepartment == null) {
                    logger.debug("=== error result, " + En_ErrorCode.UNKNOWN_PAR_DEP.getMessage());
                    return ServiceResult.failResult(En_ErrorCode.UNKNOWN_PAR_DEP.getCode(), En_ErrorCode.UNKNOWN_PAR_DEP.getMessage(), null);
                }
            }

            CompanyDepartment department = companyDepartmentDAO.getByCondition ("dep_extId=? and company_id=?", rec.getDepartmentId(), item.getCompanyId ());
            if (department == null) {
                department = new CompanyDepartment ();
                department.setCreated (new Date ());
                department.setCompanyId (item.getCompanyId ());
                department.setTypeId (1);
                department.setExternalId(rec.getDepartmentId());
            }

            WorkerEntry headWorker = null;
            if (HelperFunc.isNotEmpty(rec.getHeadId())) {
                headWorker = workerEntryDAO.getByExternalId(rec.getHeadId().trim(), item.getCompanyId ());
                if (headWorker == null) {
                    logger.debug("error result, " + En_ErrorCode.UNKNOWN_WOR.getMessage());
                    return ServiceResult.failResult(En_ErrorCode.UNKNOWN_WOR.getCode(), En_ErrorCode.UNKNOWN_WOR.getMessage(), null);
                }
            }

            department.setName (rec.getDepartmentName ().trim ());
            department.setParentId (parentDepartment == null ? null : parentDepartment.getId());
            department.setHeadId (headWorker == null ? null : headWorker.getId());

            if (department.getId () == null)
                companyDepartmentDAO.persist (department);
            else
                companyDepartmentDAO.merge (department);

            return ServiceResult.successResult (department.getId());

        } catch (Exception e) {
            logger.error ("error while update department's record", e);
        }

        return ServiceResult.failResult (En_ErrorCode.NOT_UPDATE.getCode (), En_ErrorCode.NOT_UPDATE.getMessage (), null);
    }

    @Override
    public ServiceResult deleteDepartment(DepartmentRecord rec) {

        logger.debug("=== deleteDepartment ===");

        try {

            BeanInfo infoRec = Introspector.getBeanInfo(rec.getClass());

            logger.debug("=== properties from 1C ===");
            for (PropertyDescriptor pl : infoRec.getPropertyDescriptors()) {
                logger.debug(pl.getDisplayName() + " = " + (pl.getReadMethod() != null ? pl.getReadMethod().invoke(rec,null) + ";" : "null;"));
            }
            logger.debug("==========================");

            if (HelperFunc.isEmpty(rec.getDepartmentId()))
                return ServiceResult.failResult (En_ErrorCode.EMPTY_DEP_ID.getCode (), En_ErrorCode.EMPTY_DEP_ID.getMessage (), null);

            CompanyHomeGroupItem item = companyGroupHomeDAO.getByCondition ("external_code=?", rec.getCompanyCode ().trim ());
            if (item == null)
                return ServiceResult.failResult (En_ErrorCode.EMPTY_COMP_CODE.getCode (), En_ErrorCode.EMPTY_COMP_CODE.getMessage (), null);

            CompanyDepartment department = companyDepartmentDAO.getByCondition ("dep_extId=? and company_id=?", rec.getDepartmentId(), item.getCompanyId ());
            if (department == null)
                return ServiceResult.failResult (En_ErrorCode.UNKNOWN_DEP.getCode (), En_ErrorCode.UNKNOWN_DEP.getMessage (), null);

            if (companyDepartmentDAO.checkExistsByCondition ("parent_dep=?", department.getId ()))
                return ServiceResult.failResult (En_ErrorCode.EXIST_CHILD_DEP.getCode (), En_ErrorCode.EXIST_CHILD_DEP.getMessage (), null);

            if (workerEntryDAO.checkExistsByCondition ("dep_id=?", department.getId ()))
                return ServiceResult.failResult (En_ErrorCode.EXIST_DEP_WOR.getCode (), En_ErrorCode.EXIST_DEP_WOR.getMessage (), null);

            companyDepartmentDAO.remove (department);

            return ServiceResult.successResult (department.getId());

        } catch (Exception e) {
            logger.error ("error while remove department's record", e);
        }

        return ServiceResult.failResult (En_ErrorCode.NOT_DELETE.getCode (), En_ErrorCode.NOT_DELETE.getMessage (), null);
    }

    private ServiceResult isValidWorkerRecord(WorkerRecord rec) {

        if (rec.getCompanyCode () == null || rec.getCompanyCode ().trim ().equals (""))
            return ServiceResult.failResult (En_ErrorCode.EMPTY_COMP_CODE.getCode (), En_ErrorCode.EMPTY_COMP_CODE.getMessage (), rec.getId ());

        if (HelperFunc.isEmpty(rec.getDepartmentId()))
            return ServiceResult.failResult (En_ErrorCode.EMPTY_DEP_ID.getCode (), En_ErrorCode.EMPTY_DEP_ID.getMessage (), rec.getId ());

        if (rec.getPositionName () == null || rec.getPositionName ().trim ().length () < 1)
            return ServiceResult.failResult (En_ErrorCode.EMPTY_POS.getCode (), En_ErrorCode.EMPTY_POS.getMessage (), rec.getId ());

        if (HelperFunc.isEmpty(rec.getWorkerId()))
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
            return ServiceResult.failResult (En_ErrorCode.EMPTY_COMP_CODE.getCode (), En_ErrorCode.EMPTY_COMP_CODE.getMessage (), null);

        if (HelperFunc.isEmpty(rec.getDepartmentId()))
            return ServiceResult.failResult (En_ErrorCode.EMPTY_DEP_ID.getCode (), En_ErrorCode.EMPTY_DEP_ID.getMessage (), null);

        if (rec.getDepartmentName () == null || rec.getDepartmentName ().trim ().length () < 1)
            return ServiceResult.failResult (En_ErrorCode.EMPTY_DEP_NAME.getCode (), En_ErrorCode.EMPTY_DEP_NAME.getMessage (), null);

        if (rec.getHeadId () != null && !personDAO.checkExistsByCondition ("id=?", rec.getHeadId ()))
            return ServiceResult.failResult (En_ErrorCode.UNKNOWN_HEAD_DEP.getCode (), En_ErrorCode.UNKNOWN_HEAD_DEP.getMessage (), null);

        return ServiceResult.successResult (null);
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

        WorkerPosition position = workerPositionDAO.getByCondition ("pos_name=? and company_id=?", positionName, companyId);

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
