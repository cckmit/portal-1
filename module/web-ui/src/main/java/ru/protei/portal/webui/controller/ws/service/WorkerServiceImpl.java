package ru.protei.portal.webui.controller.ws.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.webui.controller.ws.model.DepartmentRecord;
import ru.protei.portal.webui.controller.ws.model.FotoByte;
import ru.protei.portal.webui.controller.ws.model.ServiceResult;
import ru.protei.portal.webui.controller.ws.model.WorkerRecord;
import ru.protei.portal.webui.controller.ws.utils.HelperService;

import javax.jws.WebService;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by turik on 18.08.16.
 */
@WebService(endpointInterface = "ru.protei.portal.webui.controller.ws.service.WorkerService", serviceName = "WorkerService")
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

    @Override
    public WorkerRecord getWorker(Long id) {

        logger.debug("=== getWorker ===");
        logger.debug("=== id == " + id);


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
        return null;
    }

    @Override
    public ServiceResult addWorker(WorkerRecord rec) {

        logger.debug("=== addWorker ===");

        try {

            BeanInfo infoRec = Introspector.getBeanInfo(rec.getClass());

            logger.debug("=== properties from 1C ===");
            for (PropertyDescriptor pl : infoRec.getPropertyDescriptors()) {
                logger.debug(pl.getDisplayName() + " = " + (pl.getReadMethod() != null ? pl.getReadMethod().invoke(rec,null) + ";" : "null;"));
            }
            logger.debug("==========================");

            ServiceResult isValid = isValidWorkerRecord (rec);
            if (!isValid.isSuccess ())
                return isValid;

            CompanyHomeGroupItem item = companyGroupHomeDAO.getByCondition ("external_code=?", rec.getCompanyCode ().trim ());
            if (item == null)
                return ServiceResult.failResult ("PE-10002", "Unknown company's code ", rec.getId ());

            CompanyDepartment department = companyDepartmentDAO.getByCondition ("dep_extId=? and company_id=?",rec.getDepartmentId (),item.getCompanyId ());
            if (department == null)
                return ServiceResult.failResult ("PE-10004", "Unknown company's department ", null);

            Person person = null;
            if (rec.getId () != null) {
                person = personDAO.get (rec.getId ());
            }

            if (person == null) {
                person = new Person ();
                person.setCreated (new Date ());
                person.setCreator ("");
                person.setCompanyId (item.getCompanyId ());
            }

            copy (rec, person);

            if (person.getId () == null) {
                Long id = personDAO.persist (person);
                person.setId (id);
            } else {
                personDAO.merge (person);
            }

            WorkerPosition position = getValidPosition (rec.getPositionId (), rec.getPositionName (), item.getCompanyId ());

            if (workerEntryDAO.checkExistsByCondition ("worker_extId=?",rec.getWorkerId ()))
                return ServiceResult.failResult ("PE-10009", "Worker already exist ", null);

            WorkerEntry worker = new WorkerEntry ();
            worker.setCreated (new Date ());
            worker.setPersonId (person.getId ());
            worker.setCompanyId (item.getCompanyId ());
            worker.setDepartmentId (department.getId ());
            worker.setPositionId (position.getId ());
            worker.setHireDate (rec.getHireDate () != null && rec.getHireDate ().trim ().length () > 0 ? HelperService.DATE.parse (rec.getHireDate ()) : null);
            worker.setHireOrderNo (rec.getHireOrderNo () != null && rec.getHireOrderNo ().trim ().length () > 0 ? rec.getHireOrderNo ().trim () : null);
            worker.setFireDate (rec.getFireDate () != null && rec.getFireDate ().trim ().length () > 0 ? HelperService.DATE.parse (rec.getFireDate ()) : null);
            worker.setFireOrderNo (rec.getFireOrderNo () != null && rec.getFireOrderNo ().trim ().length () > 0 ? rec.getFireOrderNo ().trim () : null);
            worker.setActiveFlag (rec.getActive ());
            worker.setExternalId (rec.getWorkerId ());

            Long workerId = workerEntryDAO.persist (worker);

            return ServiceResult.successResult (person.getId ());

        } catch (Exception e) {
            logger.error ("error while read worker's record", e);
        }

        return ServiceResult.failResult ("PE-10010", "Can not create", null);
    }

    @Override
    public ServiceResult updateWorker(WorkerRecord rec) {

        logger.debug("=== updateWorker ===");

        try {

            BeanInfo infoRec = Introspector.getBeanInfo(rec.getClass());

            logger.debug("=== properties from 1C ===");
            for (PropertyDescriptor pl : infoRec.getPropertyDescriptors()) {
                logger.debug(pl.getDisplayName() + " = " + (pl.getReadMethod() != null ? pl.getReadMethod().invoke(rec,null) + ";" : "null;"));
            }
            logger.debug("==========================");

            ServiceResult isValid = isValidWorkerRecord (rec);
            if (!isValid.isSuccess ())
                return isValid;

            if (rec.getId () == null || rec.getId () < 0)
                return ServiceResult.failResult ("PE-10011", "Person's identifier is empty ", rec.getId ());

            CompanyHomeGroupItem item = companyGroupHomeDAO.getByCondition ("external_code=?", rec.getCompanyCode ().trim ());
            if (item == null)
                return ServiceResult.failResult ("PE-10002", "Unknown company's code ", rec.getId ());

            CompanyDepartment department = companyDepartmentDAO.getByCondition ("dep_extId=? and company_id=?",rec.getDepartmentId (),item.getCompanyId ());
            if (department == null)
                return ServiceResult.failResult ("PE-10004", "Unknown company's department ", null);

            Person person = personDAO.get (rec.getId ());
            if (person == null)
                return ServiceResult.failResult ("PE-10012", "Unknown person ", null);

            copy (rec, person);

            personDAO.merge (person);

            if (rec.isFired ())
                return fireWorker (rec.getWorkerId ());

            WorkerPosition position = getValidPosition (rec.getPositionId (), rec.getPositionName (), item.getCompanyId ());

            WorkerEntry worker = workerEntryDAO.getByCondition ("worker_extId=?", rec.getWorkerId ());
            if (worker == null)
                return ServiceResult.failResult ("PE-10014", "Unknown worker ", null);

            worker.setDepartmentId (department.getId ());
            worker.setPositionId (position.getId ());
            worker.setHireDate (rec.getHireDate () != null && rec.getHireDate ().trim ().length () > 0 ? HelperService.DATE.parse (rec.getHireDate ()) : null);
            worker.setHireOrderNo (rec.getHireOrderNo () != null && rec.getHireOrderNo ().trim ().length () > 0 ? rec.getHireOrderNo ().trim () : null);
            worker.setFireDate (rec.getFireDate () != null && rec.getFireDate ().trim ().length () > 0 ? HelperService.DATE.parse (rec.getFireDate ()) : null);
            worker.setFireOrderNo (rec.getFireOrderNo () != null && rec.getFireOrderNo ().trim ().length () > 0 ? rec.getFireOrderNo ().trim () : null);
            worker.setActiveFlag (rec.getActive ());

            workerEntryDAO.merge (worker);

            return ServiceResult.successResult (person.getId ());

        } catch (Exception e) {
            logger.error ("error while read worker's record", e);
        }

        return ServiceResult.failResult ("PE-10015", "Can not update", null);
    }

    @Override
    public List<ServiceResult> updateWorkers(List<WorkerRecord> group_rec) {
        return null;
    }

    @Override
    public ServiceResult deleteWorker(Long id) {

        logger.debug("=== deleteWorker ===");
        logger.debug("=== properties from 1C ===");
        logger.debug("workerId = " + id);
        logger.debug("==========================");

        try {

            if (id == null || id < 0)
                return ServiceResult.failResult ("PE-10016", "Worker's identifier is empty ", id);

            WorkerEntry worker = workerEntryDAO.getByCondition ("worker_extId=?",id);
            if (worker == null)
                return ServiceResult.failResult ("PE-10018", "Unknown worker ", null);

            Long personId = worker.getPersonId ();

            workerEntryDAO.remove (worker);

            List<WorkerEntry> list = workerEntryDAO.getListByCondition ("personId=?",personId);
            if (list == null || list.isEmpty ()){
                Person person = personDAO.get (personId);
                person.setDeleted (true);
                personDAO.merge (person);
            }

            return ServiceResult.successResult (id);

        } catch (Exception e) {
            logger.error ("error while remove", e);
        }

        return ServiceResult.failResult ("PE-10015", "Can not delete", id);
    }

    @Override
    public String updateFoto(Long id, byte[] buf) {
        return null;
    }

    @Override
    public List<FotoByte> getFotos(List<Long> list) {
        return null;
    }

    @Override
    public ServiceResult addDepartment(DepartmentRecord rec) {

        logger.debug("=== addDepartment ===");

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
                return ServiceResult.failResult ("PE-10002", "Unknown company's code ", rec.getDepartmentId ());

            if (companyDepartmentDAO.checkExistsByCondition ("dep_extId=?",rec.getDepartmentId ()))
                return ServiceResult.failResult ("PE-10009", "Department already exist ", null);

            CompanyDepartment department = new CompanyDepartment ();
            department.setCreated (new Date ());
            department.setName (rec.getDepartmentName ().trim ());
            department.setCompanyId (item.getCompanyId ());
            department.setTypeId (1);
            department.setParentId (rec.getParentId ());
            department.setHeadId (rec.getHeadId ());
            department.setExternalId (rec.getDepartmentId ());

            Long departmentId = companyDepartmentDAO.persist (department);

            return ServiceResult.successResult (departmentId);

        } catch (Exception e) {
            logger.error ("error while read department's record", e);
        }

        return ServiceResult.failResult ("PE-10010", "Can not create", null);
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
                return ServiceResult.failResult ("PE-10002", "Unknown company's code ", rec.getDepartmentId ());

            CompanyDepartment department = companyDepartmentDAO.getByCondition ("dep_extId=?", rec.getDepartmentId ());
            if (department == null)
                return ServiceResult.failResult ("PE-10021", "Unknown department ", null);

            department.setName (rec.getDepartmentName ().trim ());
            department.setParentId (rec.getParentId ());
            department.setHeadId (rec.getHeadId ());
            companyDepartmentDAO.merge (department);

            return ServiceResult.successResult (department.getId ());

        } catch (Exception e) {
            logger.error ("error while read department's record", e);
        }

        return ServiceResult.failResult ("PE-10010", "Can not create", null);
    }

    @Override
    public ServiceResult deleteDepartment(Long id) {

        logger.debug("=== deleteDepartment ===");
        logger.debug("=== properties from 1C ===");
        logger.debug("departmentId = " + id);
        logger.debug("==========================");

        try {

            if (id == null || id < 0)
                return ServiceResult.failResult ("PE-10003", "Department's identifier is empty ", id);

            CompanyDepartment department = companyDepartmentDAO.getByCondition ("dep_extId=?", id);
            if (department == null)
                return ServiceResult.failResult ("PE-10021", "Unknown department ", null);

            if (companyDepartmentDAO.checkExistsByCondition ("parent_department=?", department.getId ()))
                return ServiceResult.failResult ("PE-10022", "The department has child departments.", null);

            if (workerEntryDAO.checkExistsByCondition ("dep_id=?", department.getId ()))
                return ServiceResult.failResult ("PE-10022", "The department has workers.", null);

            companyDepartmentDAO.remove (department);

            return ServiceResult.successResult (id);

        } catch (Exception e) {
            logger.error ("error while remove", e);
        }

        return ServiceResult.failResult ("PE-10015", "Can not delete", id);
    }

    private ServiceResult isValidWorkerRecord(WorkerRecord rec) {

        if (rec.getCompanyCode () == null || rec.getCompanyCode ().trim ().equals (""))
            return ServiceResult.failResult ("PE-10001", "Company's code is empty ", rec.getId ());

        if (rec.getDepartmentId () == null || rec.getDepartmentId () < 0)
            return ServiceResult.failResult ("PE-10003", "Department's identifier is empty ", rec.getId ());

        if (rec.getPositionId () == null || rec.getPositionId () < 0 ||
                rec.getPositionName () == null || rec.getPositionName ().trim ().length () < 1)
            return ServiceResult.failResult ("PE-10005", "Position is empty ", rec.getId ());

        if (rec.getWorkerId () == null || rec.getWorkerId () < 0)
            return ServiceResult.failResult ("PE-10016", "Worker's identifier is empty ", rec.getId ());

        if (rec.getFirstName () == null || rec.getFirstName ().trim ().length () < 1)
            return ServiceResult.failResult ("PE-10006", "First name is empty ", rec.getId ());

        if (rec.getLastName () == null || rec.getLastName ().trim ().length () < 1)
            return ServiceResult.failResult ("PE-10007", "Last name is empty ", rec.getId ());

        if (rec.getIpAddress () != null && !rec.getIpAddress ().trim ().equals("") &&
                !rec.getIpAddress ().trim ().matches("^[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}$"))
            return ServiceResult.failResult ("PE-10008", "Invalid format's ip-address ", rec.getId ());

        return ServiceResult.successResult (rec.getId ());
    }

    private ServiceResult isValidDepartmentRecord(DepartmentRecord rec) {

        if (rec.getCompanyCode () == null || rec.getCompanyCode ().trim ().equals (""))
            return ServiceResult.failResult ("PE-10001", "Company's code is empty ", rec.getDepartmentId ());

        if (rec.getDepartmentId () == null || rec.getDepartmentId () < 0)
            return ServiceResult.failResult ("PE-10003", "Department's identifier is empty ", rec.getDepartmentId ());

        if (rec.getDepartmentName () == null || rec.getDepartmentName ().trim ().length () < 1)
            return ServiceResult.failResult ("PE-10007", "Department's name is empty ", rec.getDepartmentId ());

        if (rec.getParentId () != null && !companyDepartmentDAO.checkExistsByCondition ("id=?",rec.getParentId ()))
            return ServiceResult.failResult ("PE-10020", "Unknown parent of department ", null);

        if (rec.getHeadId () != null && !personDAO.checkExistsByCondition ("id=?",rec.getHeadId ()))
            return ServiceResult.failResult ("PE-10020", "Unknown head of department ", null);

        return ServiceResult.successResult (rec.getDepartmentId ());
    }

    private void copy(WorkerRecord rec, Person person) throws ParseException {

        person.setFirstName (rec.getFirstName ().trim ());
        person.setLastName (rec.getLastName ().trim ());
        person.setSecondName (rec.getSecondName () != null && rec.getSecondName ().trim ().length () > 0 ? rec.getSecondName ().trim () : null);
        person.setDisplayName (HelperService.generateDisplayName (person.getFirstName (), person.getLastName (), person.getSecondName ()));
        person.setDisplayShortName (HelperService.generateDisplayShortName (person.getFirstName (), person.getLastName (), person.getSecondName ()));
        person.setSex (rec.getSex () == null ? "-" : rec.getSex () == 1 ? "M" : "F");
        person.setBirthday (rec.getBirthday() != null && rec.getBirthday().trim ().length () > 0 ? HelperService.DATE.parse(rec.getBirthday()) : null);
        person.setIpAddress (rec.getIpAddress () != null && rec.getIpAddress ().trim ().length () > 0 ? rec.getIpAddress ().trim () : null);
        person.setWorkPhone (rec.getPhoneWork () != null && rec.getPhoneWork ().trim ().length () > 0 ? rec.getPhoneWork ().trim () : null);
        person.setMobilePhone (rec.getPhoneMobile () != null && rec.getPhoneMobile ().trim ().length () > 0 ? rec.getPhoneMobile ().trim () : null);
        person.setHomePhone (rec.getPhoneHome () != null && rec.getPhoneHome ().trim ().length () > 0 ? rec.getPhoneHome ().trim () : null);
        person.setPassportInfo (rec.getPassportInfo () != null && rec.getPassportInfo ().trim ().length () > 0 ? rec.getPassportInfo ().trim () : null);
        person.setInfo (rec.getInfo () != null && rec.getInfo ().trim ().length () > 0 ? rec.getInfo ().trim () : null);
        person.setAddress (rec.getAddress () != null && rec.getAddress ().trim ().length () > 0 ? rec.getAddress ().trim () : null);
        person.setAddressHome (rec.getAddressHome () != null && rec.getAddressHome ().trim ().length () > 0 ? rec.getAddressHome ().trim () : null);
        person.setEmail (rec.getEmail () != null && rec.getEmail ().trim ().length () > 0 ? rec.getEmail ().trim () : null);
        person.setEmail_own (rec.getEmailOwn () != null && rec.getEmailOwn ().trim ().length () > 0 ? rec.getEmailOwn ().trim () : null);
        person.setFax (rec.getFax () != null && rec.getFax ().trim ().length () > 0 ? rec.getFax ().trim () : null);
        person.setDeleted (rec.isDeleted ());
        //person.setDepartment ();
        //person.setPosition ();

    }

    private WorkerPosition getValidPosition(Long positionId, String positionName, Long companyId) {

        WorkerPosition position = workerPositionDAO.getByCondition ("pos_extId=?",positionId);

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

    private ServiceResult fireWorker(Long id) {

        logger.debug("=== fireWorker ===");
        logger.debug("workerId = " + id);

        try {

            if (id == null || id < 0)
                return ServiceResult.failResult ("PE-10016", "Worker's identifier is empty ", id);

            WorkerEntry worker = workerEntryDAO.getByCondition ("worker_extId=?",id);
            if (worker == null)
                return ServiceResult.failResult ("PE-10018", "Unknown worker ", null);

            Long personId = worker.getPersonId ();

            workerEntryDAO.remove (worker);

            List<WorkerEntry> list = workerEntryDAO.getListByCondition ("personId=?",personId);
            if (list == null || list.isEmpty ()){
                Person person = personDAO.get (personId);
                person.setFired (true);
                personDAO.merge (person);
            }

            return ServiceResult.successResult (id);

        } catch (Exception e) {
            logger.error ("error while fire", e);
        }

        return ServiceResult.failResult ("PE-10015", "Can not fire", id);
    }
}
