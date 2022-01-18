package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.api.struct.Workers;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_AdminState;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.ent.LongAuditableObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.WorkerEntry;
import ru.protei.portal.core.model.query.WorkerEntryQuery;
import ru.protei.portal.core.model.struct.AuditObject;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.ent.WorkerEntry.Columns.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;

public class WorkerEntryServiceImpl implements WorkerEntryService {

    @Autowired
    WorkerEntryDAO workerEntryDAO;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    UserLoginDAO userLoginDAO;
    @Autowired
    AuditObjectDAO auditObjectDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    ContactItemDAO contactItemDAO;
    @Autowired
    LegacySystemDAO migrationManager;
    @Autowired
    PortalConfig portalConfig;
    @Autowired
    CompanyDepartmentDAO companyDepartmentDAO;
    
    @Override
    @Transactional
    public Result<Void> updateFiredByDate(Date now) {
        List<WorkerEntry> entryForFire = workerEntryDAO.getForFireByDate(now);
        for (WorkerEntry entry : entryForFire) {
            workerEntryDAO.remove(entry);
            if (!workerEntryDAO.checkExistsByPersonId(entry.getPersonId())) {
                Person person = personDAO.get(entry.getPersonId());
                List<UserLogin> userLogins = userLoginDAO.findByPersonId(entry.getPersonId());
                firePerson(person, true, entry.getFiredDate(),
                        entry.getDeleted(), userLogins,
                        portalConfig.data().legacySysConfig().isExportEnabled());
            }
        }
        return ok();
    }

    @Override
    @Transactional
    public Result<Void> updatePositionByDate(Date now) {
        for (WorkerEntry entry: workerEntryDAO.getForUpdatePositionByDate(now)) {
            Person person = personDAO.get(entry.getPersonId());
            person.setPosition(entry.getNewPositionName());
            person.setDepartment(companyDepartmentDAO.get(entry.getNewPositionDepartmentId()).getName());
            personDAO.partialMerge(person, "department", "displayPosition");

            entry.setNewPositionName(null);
            entry.setNewPositionDepartmentId(null);
            entry.setNewPositionTransferDate(null);
            workerEntryDAO.partialMerge(entry, POSITION_NAME, POSITION_DEPARTMENT_ID);
        }
        return ok();
    }

    @Override
    @Transactional
    public Result<Void> firePerson(Person person, Boolean isFired, Date fireDate,
                                   Boolean isDeleted, List<UserLogin> userLogins,
                                   Boolean isNeedMigrationAtFire) {
        person.setFired(isFired, fireDate);
        person.setDeleted(isDeleted != null ? isDeleted : false);
        person.setIpAddress(person.getIpAddress() == null ? null : person.getIpAddress().replace(".", "_"));

        if(isNotEmpty(userLogins)) {
            if (person.isDeleted()) {
                for (UserLogin userLogin : userLogins) {
                    removeAccount(userLogin);
                }
            } else {
                for (UserLogin userLogin : userLogins) {
                    userLogin.setAdminStateId(En_AdminState.LOCKED.getId());
                    saveAccount(userLogin);
                }
            }
        }

        mergePerson(person);

        if (isNeedMigrationAtFire) {
            Workers workers = new Workers(workerEntryDAO.getWorkers(new WorkerEntryQuery(person.getId())));
            String departmentName = workers.getAnyDepartment("");
            String positionName = workers.getAnyPosition("");
            migrationManager.saveExternalEmployee(person, departmentName, positionName);
        }

        return ok();
    }

    private void removeAccount(UserLogin userLogin) {
        if (userLoginDAO.remove(userLogin))
            makeAudit(new LongAuditableObject(userLogin.getId()), En_AuditType.ACCOUNT_REMOVE);
    }

    private void saveAccount(UserLogin userLogin) {
        if (userLoginDAO.saveOrUpdate(userLogin)) {
            jdbcManyRelationsHelper.persist( userLogin, "roles" );
            makeAudit(userLogin, userLogin.getId() == null ? En_AuditType.ACCOUNT_CREATE : En_AuditType.ACCOUNT_MODIFY);
        }
    }

    private void makeAudit(AuditableObject object, En_AuditType type) {
        AuditObject auditObject = new AuditObject();
        auditObject.setCreated( new Date() );
        auditObject.setType(type);
        auditObject.setCreatorId( 0L );
        try {
            auditObject.setCreatorIp( Inet4Address.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            auditObject.setCreatorIp("0.0.0.0");
        }
        auditObject.setCreatorShortName("portal-api scheduled");
        auditObject.setEntryInfo(object);

        auditObjectDAO.insertAudit(auditObject);
    }

    private void mergePerson(Person person) {
        personDAO.merge(person);
        contactItemDAO.saveOrUpdateBatch(person.getContactItems());
        jdbcManyRelationsHelper.persist(person, Person.Fields.CONTACT_ITEMS);
        makeAudit(person, En_AuditType.EMPLOYEE_MODIFY);
    }
}
