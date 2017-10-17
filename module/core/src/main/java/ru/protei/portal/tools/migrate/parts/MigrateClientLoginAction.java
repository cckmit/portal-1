package ru.protei.portal.tools.migrate.parts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.tools.migrate.tools.BatchProcess;
import ru.protei.portal.tools.migrate.tools.MigrateAction;
import ru.protei.winter.jdbc.JdbcDAO;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by michael on 01.04.16.
 */
public class MigrateClientLoginAction implements MigrateAction {

    private static Logger logger = LoggerFactory.getLogger(MigrateClientLoginAction.class);

    public static final String MIGRATE_ITEM_CODE = "Tm_ClientLogin";

    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    PersonDAO personDAO;

    @Autowired
    UserLoginDAO userLoginDAO;

    @Autowired
    UserRoleDAO userRoleDAO;

    @Autowired
    private MigrationEntryDAO migrationEntryDAO;

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;


    @Override
    public int orderOfExec() {
        return 2;
    }


    private final static String DEF_CLIENT_ROLE_CODE="CRM_CLIENT";

    private final static En_Privilege [] DEF_COMPANY_CLIENT_PRIV = {
            En_Privilege.ISSUE_CREATE,
            En_Privilege.ISSUE_EDIT,
            En_Privilege.ISSUE_VIEW,
            En_Privilege.COMMON_PROFILE_EDIT,
            En_Privilege.COMMON_PROFILE_VIEW
    };

    @Override
    public void migrate(Connection sourceConnection) throws SQLException {

        final Map<String, UserLogin> rtUnique = new HashMap<>();
        userLoginDAO.getAll().forEach(u -> rtUnique.put(u.getUlogin().toLowerCase(), u));

        UserRole crmClientRole = userRoleDAO.ensureExists(DEF_CLIENT_ROLE_CODE, DEF_COMPANY_CLIENT_PRIV);

        Set<UserRole> roles = new HashSet<>();
        roles.add(crmClientRole);


        BatchProcess<UserLogin> processBatch = new BaseBatchProcess<UserLogin>() {
            @Override
            public void afterInsert(List<UserLogin> insertedEntries) {
                logger.debug("persist client login roles");
                jdbcManyRelationsHelper.persist( insertedEntries, "roles" );
            }
        };

        new BatchInsertTask(migrationEntryDAO, MIGRATE_ITEM_CODE)
                .forTable("\"resource\".Tm_CompanyLogin", "nID", null)
                .skipEmptyEntity(true)
                .process(sourceConnection, userLoginDAO, processBatch, row -> {
                    UserLogin ulogin = new UserLogin();

                    ulogin.setUlogin((String) row.get("strLogin"));

                    if (ulogin.getUlogin() == null) {
                        logger.warn("unable to create user with empty login, portal-ID={}, skip", row.get("nID"));
                        return null;
                    }

                    UserLogin existing = rtUnique.get(ulogin.getUlogin().toLowerCase());

                    if (existing != null) {
                        logger.warn("user-login is not unique, portal-id={}, curr-id={}, login={}, skip", row.get("nID"), existing.getId(), existing.getUlogin());
                        return null;
                    }

                    ulogin.setAdminStateId(En_AdminState.UNLOCKED.getId());
                    ulogin.setAuthTypeId(En_AuthType.LOCAL.getId());
                    ulogin.setCreated(new Date());
                    ulogin.setInfo((String) row.get("strInfo"));
                    ulogin.setPersonId((Long)row.get("nPersonID"));
                    ulogin.setUpass((String)row.get("strPassword"));
                    ulogin.setRoles(roles);

                    if (ulogin.getPersonId() == null) {
                        Long companyId = (Long)row.get("nCompanyID");

                        logger.debug("company-login handle, companyId = {}", companyId);

                        if (companyId == null) {
                            logger.debug("no person and company set for login, portal-id={}, skip it", row.get("nID"));
                            return null;
                        }
                        Company company = companyDAO.get(companyId);

                        Person p = personDAO.findContactByName(companyId, company.getCname());

                        if (p == null) {
                            logger.debug("no autocreated person found, create new one");
                            p = new Person();
                            p.setCompanyId(companyId);
                            p.setCreated(new Date());
                            p.setCreator("migration-task");
                            p.setDeleted(false);
                            p.setDisplayName(company.getCname());
                            p.setDisplayShortName("Общий контакт");
                            p.setFirstName("Общий контакт компании");
                            p.setLastName("-");
                            p.setSecondName("-");
                            p.setGender(En_Gender.UNDEFINED);
                            p.setInfo("Создан в процессе миграции данных");
                            p.setPosition("-");
                            personDAO.persist(p);
                        }

                        ulogin.setPersonId(p.getId());
                    }

                    rtUnique.put(ulogin.getUlogin().toLowerCase(), ulogin);

                    return ulogin;
                })
                .dumpStats();

    }
}
