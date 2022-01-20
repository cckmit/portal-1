package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.api.struct.Workers;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_AdminState;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.WorkerEntryQuery;
import ru.protei.portal.core.model.struct.AuditObject;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.ent.WorkerEntry.Columns.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;

public class WorkerEntryServiceImpl implements WorkerEntryService {

    private static final Logger log = LoggerFactory.getLogger(WorkerEntryServiceImpl.class);

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
    @Autowired
    WorkerPositionDAO workerPositionDAO;
    
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
        for (WorkerEntry worker: workerEntryDAO.getForUpdatePositionByDate(now)) {
            log.debug(String.format("Update worker with id %s position from '%s' to '%s'",
                                     worker.getId(), worker.getPositionName(), worker.getNewPositionName()));

            String newPositionName = worker.getNewPositionName();
            Long companyId = worker.getCompanyId();

            WorkerPosition workerPosition = workerPositionDAO.getByName(newPositionName, companyId);
            if (workerPosition == null) {
                workerPosition = createWorkerPosition(newPositionName, companyId);
                workerPositionDAO.persist(workerPosition);
            }

            worker.setPositionId(workerPosition.getId());
            worker.setNewPositionName(null);
            worker.setNewPositionDepartmentId(null);
            worker.setNewPositionTransferDate(null);
            boolean updated = workerEntryDAO.partialMerge(worker, POSITION_ID, NEW_POSITION_NAME,
                                                                  NEW_POSITION_DEPARTMENT_ID, NEW_POSITION_TRANSFER_DATE);
            if (updated) {
                log.debug(String.format("Worker with id %s position changed to '%s'", worker.getId(), newPositionName));
            } else {
                return error(En_ResultStatus.NOT_UPDATED, String.format("Worker with id %s position not updated!", worker.getId()));
            }
        }
        return ok();
    }

    private WorkerPosition createWorkerPosition(String newWorkerPosition, Long companyId) {
        WorkerPosition workerPosition = new WorkerPosition();
        workerPosition.setName(newWorkerPosition);
        workerPosition.setCompanyId(companyId);
        return workerPosition;
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
