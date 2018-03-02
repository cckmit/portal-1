package ru.protei.portal.tools.migrate.imp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_MigrationEntry;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.tools.migrate.struct.*;
import ru.protei.portal.tools.migrate.sybase.LegacyDAO_Transaction;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;
import ru.protei.portal.tools.migrate.utils.MigrateUtils;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ImportDataServiceImpl implements ImportDataService {

    private static Logger logger = LoggerFactory.getLogger(ImportDataServiceImpl.class);

    @Autowired
    LegacySystemDAO legacySystemDAO;

    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    CompanyGroupHomeDAO companyGroupHomeDAO;

    @Autowired
    PersonDAO personDAO;

    @Autowired
    UserRoleDAO userRoleDAO;

    @Autowired
    UserLoginDAO userLoginDAO;

    @Autowired
    DevUnitDAO devUnitDAO;

    @Autowired
    CompanySubscriptionDAO companySubscriptionDAO;

    @Autowired
    MigrationEntryDAO migrationEntryDAO;

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Autowired
    CaseCommentDAO caseCommentDAO;

    @Autowired
    CaseStateMatrixDAO caseStateMatrixDAO;

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Autowired
    PortalConfig portalConfig;


    @Scheduled(fixedRate = 60000L, initialDelay = 60000L)
    public void incrementalImport () {
        if (!portalConfig.data().legacySysConfig().isImportEnabled()) {
            logger.debug("import is disabled, exit now");
            return;
        }

        runIncrementalImport();

        logger.debug("incremental import done");
    }

    @Transactional
    public void runIncrementalImport () {
        logger.debug("incremental import run");

        legacySystemDAO.runActionRTE(transaction -> {
            importCompanies(transaction);
            importProducts(transaction);

            if (portalConfig.data().legacySysConfig().isImportEmployeesEnabled()) {
                importEmployes(transaction);
            }
            else {
                logger.debug("import of employees from legacy db is disabled, skip");
            }

            return true;
        });
    }


    private void importProducts (LegacyDAO_Transaction transaction) throws SQLException {
        MigrationEntry migrationEntry = migrationEntryDAO.getOrCreateEntry(En_MigrationEntry.PRODUCT);

        List<ExternalProduct> products = transaction.dao(ExternalProduct.class).list("nID > ?", migrationEntry.getLastId());

        if (products.isEmpty())
            return;

        migrationEntry = migrationEntryDAO.updateEntry(En_MigrationEntry.PRODUCT, HelperFunc.last(products));

        logger.debug("migrate productst, count = {}, last-id={}", products.size(), migrationEntry.getLastId());

        products.removeIf(p -> devUnitDAO.checkExistsByCondition("UTYPE_ID=? and old_id=?", En_DevUnitType.PRODUCT.getId(), p.getId()));

        if (products.isEmpty())
            return;

        HelperFunc.splitBatch(products, 100,
                list -> devUnitDAO.persistBatch(
                        list.stream().map(p -> MigrateUtils.fromExternalProduct(p)).collect(Collectors.toList())
                )
        );
    }

    private void importCompanies (LegacyDAO_Transaction transaction) throws SQLException {
        MigrationEntry migrationEntry = migrationEntryDAO.getOrCreateEntry(En_MigrationEntry.COMPANY);

        logger.debug("incremental company import, get from id = {}", migrationEntry.getLastId());

        List<ExternalCompany> src = transaction.dao(ExternalCompany.class).list("nID > ?", migrationEntry.getLastId());

        if (src.isEmpty())
            return;

        migrationEntryDAO.updateEntry(En_MigrationEntry.COMPANY, HelperFunc.last(src));
        src.removeIf(c -> companyDAO.checkExistsByCondition("old_id=?", c.getId()));

        if (src.isEmpty())
            return;

        logger.debug("handled {} companies", src.size());

        HelperFunc.splitBatch(src, 100, importList ->
                companyDAO.persistBatch(importList.stream().map(imp -> MigrateUtils.fromExternalCompany(imp)).collect(Collectors.toList()))
        );
    }

    private void importEmployes(LegacyDAO_Transaction transaction) throws SQLException {
        logger.debug("incremental import, employees");

        ImportPersonBatch importPersonBatch = new ImportPersonBatch(userRoleDAO.getDefaultEmployeeRoles(),
                userRoleDAO.getDefaultManagerRoles(),
                new StrictLoginUniqueController());

            MigrationEntry migrationEntry = migrationEntryDAO.getOrCreateEntry(En_MigrationEntry.PERSON_EMPLOYEE);

            List<ExternalPerson> processList = transaction.dao(ExternalPerson.class).list("nID > ? and nCompanyID=?", migrationEntry.getLastId(), 1L);

            if (processList.isEmpty()) {
                logger.debug("No new employees in legacy db, exit now");
                return;
            }

            logger.debug("got new external person list, size = {}", processList.size());

            migrationEntry = migrationEntryDAO.updateEntry(En_MigrationEntry.PERSON_EMPLOYEE, HelperFunc.last(processList));

            processList.removeIf(e -> personDAO.existsByLegacyId(e.getId()));

            logger.debug("external person list after filtering, size = {}", processList.size());

            HelperFunc.splitBatch(
                    processList,
                    100, list -> importPersonBatch.doImport(transaction, list)
            );

            logger.debug("processed new employees records: {}, last-id: {}", processList.size(), migrationEntry.getLastId());

        logger.debug("incremental import, employees, done");
    }


    /*
    *
    * Full import mode below
    *
    *
    *
    *
    *
     */




    private boolean isRequireToReImport (UserLogin userLogin) {
        if (userLogin.getPerson().getCompanyId() != 1L) {
            return true;
        }

        jdbcManyRelationsHelper.fill(userLogin, "roles");

        for (UserRole role : userLogin.getRoles()) {
            if (role.getCode().contains("ДН :"))
                return false;

            if (role.getCode().contains("Администратор"))
                return false;
        }

        return true;
    }


    private void removeLoginsForReimport () {
        List<UserLogin> logins =  userLoginDAO.getAll();
        List<UserLogin> toRemove = logins.stream().filter(this::isRequireToReImport).collect(Collectors.toList());
        userLoginDAO.removeByKeys(HelperFunc.keys(toRemove, e->e.getId()));
    }

    @Override
    @Transactional
    public void importInitialCommonData() {
        logger.debug("Full import mode run");

        removeLoginsForReimport();

        InitialImport initialImport = new InitialImport();
        MigrateUtils.checkNoCompanyRecord(companyDAO);

        legacySystemDAO.runActionRTE(transaction -> {

            int _count = initialImport.importCompanies(transaction);
            logger.debug("handled {} companies", _count);

            MigrateUtils.defaultProteiHomeSetup(companyGroupHomeDAO);

            _count = initialImport.importPersons(transaction);
            logger.debug("handled {} persons", _count);

            _count = initialImport.importClientLogins(transaction);
            logger.debug("handled {} external logins", _count);

            _count = initialImport.importProjects(transaction);
            logger.debug("handled {} projects", _count);

            _count = initialImport.importProduct(transaction);
            logger.debug("handled {} products", _count);

            _count = initialImport.importCompanySubscriptions(transaction);
            logger.debug("handled {} emails-subscriptions", _count);

            return true;
        });
    }


    @Override
    @Transactional
    public void importInitialSupportSessions() {
        logger.debug("import CRM support sessions");

        CaseImport caseImport = new CaseImport();

        legacySystemDAO.runActionRTE(transaction -> {
            int _count = caseImport.initialCrmSupportSessionsImport(transaction);
            logger.debug("handled {} crm sessions", _count);

            return true;
        });
    }

    class CaseImport {

        public CaseImport() {
        }

        public int initialCrmSupportSessionsImport(LegacyDAO_Transaction transaction) throws SQLException {
            Long startId = migrationEntryDAO.getOrCreateEntry(En_MigrationEntry.CRM_SUPPORT_SESSION).getLastId();
            if (startId == null)
                startId = 0L;

            List<ExtCrmSession> src = transaction.dao(ExtCrmSession.class).list("nCategoryId=? and nID > ?", 8, startId);
            migrationEntryDAO.updateEntry(En_MigrationEntry.CRM_SUPPORT_SESSION, HelperFunc.last(src));


            final Map<Long, Long> supportStatusMap = caseStateMatrixDAO.getOldToNewStateMap(En_CaseType.CRM_SUPPORT);
            final Map<Long, Long> productIdMap = devUnitDAO.getProductOldToNewMap();

            HelperFunc.splitBatch(src, 1000, procList -> {
                List<CaseObject> caseObjects = procList.stream().map(e -> MigrateUtils.fromSupportSession(e,supportStatusMap,productIdMap)).collect(Collectors.toList());

                Long minOldId = procList.get(0).getId();
                Long maxOldId = HelperFunc.last(procList).getId();

                logger.debug("process crm-sessions, from={}, to={}", minOldId, maxOldId);

                Map<Long,Long> session2contact = legacySystemDAO.getSession2ContactMap(minOldId, maxOldId);

                caseObjects.forEach(obj -> {
                    Long contactId = session2contact.get(obj.getCaseNumber());
                    if (obj.getCreatorId() == null) {
                        obj.setCreatorId(HelperFunc.nvlt(contactId, obj.getManagerId()));
                    }

                    if (obj.getInitiatorId() == null) {
                        obj.setInitiatorId(HelperFunc.nvlt(contactId, obj.getManagerId()));
                    }
                });

                caseObjectDAO.persistBatch(caseObjects);

                Map<Long, Long> caseNumber2IdMap = new HashMap<>();
                caseObjects.forEach(c -> caseNumber2IdMap.put(c.getCaseNumber(), c.getId()));

                List<ExtCrmComment> srcComments = legacySystemDAO.getCrmComments(minOldId, maxOldId);

                List<CaseComment> sessionComments = srcComments.stream()
                        .map(c->MigrateUtils.fromCrmSupportComment(c,supportStatusMap,caseNumber2IdMap))
                        .collect(Collectors.toList());

                int fullCommentsCount = sessionComments.size();
                sessionComments.removeIf(c -> c.getCaseId() == null);

                int filtered = sessionComments.size();

                logger.debug("session comments, full = {}, filtered = {}", fullCommentsCount, filtered);

                caseCommentDAO.persistBatch(sessionComments);

                migrationEntryDAO.updateEntry(En_MigrationEntry.CRM_SUPPORT_SESSION_COMMENT, HelperFunc.last(srcComments));
            });

            return src.size();
        }
    }


    class InitialImport {

        private Set<UserRole> employeeRoleSet;
        private Set<UserRole> customerRoleSet;
        private UniqueController<String> loginController;
        private ImportPersonBatch importPersonBatch;


        public InitialImport() {
            employeeRoleSet = userRoleDAO.getDefaultEmployeeRoles();
            customerRoleSet = userRoleDAO.getDefaultCustomerRoles();
            loginController = new CachedLoginUniqueControl();
            importPersonBatch = new ImportPersonBatch(employeeRoleSet, userRoleDAO.getDefaultManagerRoles(), loginController);
        }


        public int importCompanySubscriptions (LegacyDAO_Transaction transaction) throws SQLException {
            List<ExtCompanyEmailSubs> src = transaction.dao(ExtCompanyEmailSubs.class)
                    .list("strSystem=?", "CRM");

            migrationEntryDAO.updateEntry(En_MigrationEntry.COMPANY_EMAIL_SUBS, HelperFunc.last(src));

            companySubscriptionDAO.removeAll();

            Set<Long> companyKeys = new HashSet<>(companyDAO.keys());

            src.removeIf(e -> !companyKeys.contains(e.getCompanyId()));

            List<CompanySubscription> target = src.stream().map(e -> MigrateUtils.fromExternalSubscription(e)).distinct().collect(Collectors.toList());

            HelperFunc.splitBatch(target, 100, list-> companySubscriptionDAO.persistBatch(list));

            return src.size();
        }

        public int importProjects (LegacyDAO_Transaction transaction) throws SQLException {
            List<ExternalDevProject> projects = transaction.dao(ExternalDevProject.class).list();

            migrationEntryDAO.updateEntry(En_MigrationEntry.PROJECT, HelperFunc.last(projects));

            Set<Long> imported = new HashSet<>(
                    devUnitDAO.listColumnValue("old_id", Long.class, "UTYPE_ID=?", En_DevUnitType.COMPONENT.getId())
            );
            projects.removeIf(p -> imported.contains(p.getId()));

            HelperFunc.splitBatch(projects, 100,
                    list -> devUnitDAO.persistBatch(
                            list.stream().map(p -> MigrateUtils.fromExternalProject(p)).collect(Collectors.toList())
                    )
            );

            return projects.size();
        }

        public int importProduct (LegacyDAO_Transaction transaction) throws SQLException {
            List<ExternalProduct> products = transaction.dao(ExternalProduct.class).list();

            migrationEntryDAO.updateEntry(En_MigrationEntry.PRODUCT, HelperFunc.last(products));

            Set<Long> imported = new HashSet<>(
                    devUnitDAO.listColumnValue("old_id", Long.class, "UTYPE_ID=?", En_DevUnitType.PRODUCT.getId())
            );
            products.removeIf(p -> imported.contains(p.getId()));

            HelperFunc.splitBatch(products, 100,
                    list -> devUnitDAO.persistBatch(
                            list.stream().map(p -> MigrateUtils.fromExternalProduct(p)).collect(Collectors.toList())
                    )
            );

            return products.size();
        }


        public int importClientLogins (LegacyDAO_Transaction transaction) throws SQLException {
            // import client and companies logins
            List<ExtContactLogin> src = transaction.dao(ExtContactLogin.class).list();

            migrationEntryDAO.updateEntry(En_MigrationEntry.CLIENT_LOGIN, HelperFunc.last(src));

            src.removeIf(x -> !loginController.isUnique(x.translatedLogin()));
            HelperFunc.splitBatch(src, 100, importList -> doImportClientLogins(importList));

            return src.size();
        }

        private void doImportClientLogins (List<ExtContactLogin> extLogins) {

            Map<Long,Person> personMap = HelperFunc.map(
                    personDAO.getListByKeys(HelperFunc.keys(extLogins, x -> x.getPersonId())),
                    p -> p.getId()
            );

            Map<Long, Company> companyMap = HelperFunc.map(
                    companyDAO.getListByKeys(HelperFunc.keys(extLogins, x -> x.getCompanyId())),
                    c -> c.getId()
            );

            Map<Long, Person> companyCommonAccounts = new HashMap<>();

            List<UserLogin> customerLogins = new ArrayList<>();

            extLogins.forEach(extLogin -> {
                if (!loginController.isUnique(extLogin.translatedLogin())) {
                    logger.error("duplicated customer login {}", extLogin.translatedLogin());
                }
                else {
                    UserLogin ulogin = MigrateUtils.externalCustomerLogin(extLogin, customerRoleSet);

                    if (ulogin.getPersonId() == null) {
                        // common company account
                        Person common = companyCommonAccounts.computeIfAbsent(extLogin.getCompanyId(), id -> {
                            Company company = companyMap.get(id);
                            Person commonPerson = personDAO.findContactByName(id, company.getCname());
                            if (commonPerson == null) {
                                commonPerson = MigrateUtils.createCompanyCommonPerson(company);
                                personDAO.persist(commonPerson);
                            }
                            return commonPerson;
                        });

                        ulogin.setPersonId(common.getId());
                    }

                    loginController.register(ulogin.getUlogin());
                    customerLogins.add(ulogin);
                }
            });


            if (!customerLogins.isEmpty()) {
                userLoginDAO.persistBatch(customerLogins);
                jdbcManyRelationsHelper.persist( customerLogins, "roles" );
            }
        }


        public int importPersons (LegacyDAO_Transaction transaction) throws SQLException {
            List<ExternalPerson> src = transaction.dao(ExternalPerson.class).list();

            migrationEntryDAO.updateEntry(En_MigrationEntry.PERSON_EMPLOYEE, MigrateUtils.lastEmployee(src));
            migrationEntryDAO.updateEntry(En_MigrationEntry.PERSON_CUSTOMER, MigrateUtils.lastCustomer(src));

            HelperFunc.splitBatch(src, 100, importList -> importPersonBatch.doImport(transaction, importList));

            return src.size();
        }



        public int importCompanies(LegacyDAO_Transaction transaction) throws SQLException {

            List<ExternalCompany> src = transaction.dao(ExternalCompany.class).list();

            migrationEntryDAO.updateEntry(En_MigrationEntry.COMPANY, HelperFunc.last(src));

            Set<Long> compIdSet = new HashSet<>(companyDAO.keys());
            // excludes already existing
            src.removeIf(imp -> compIdSet.contains(imp.getId()));

            HelperFunc.splitBatch(src, 100, importList ->
                    companyDAO.persistBatch(importList.stream().map(imp -> MigrateUtils.fromExternalCompany(imp)).collect(Collectors.toList()))
            );

            return src.size();
        }
    }


    interface UniqueController<T> {
        boolean isUnique (T data);
        void register (T data);
    }

    class CachedLoginUniqueControl implements UniqueController<String> {

        private Set<String> usedLogins;

        public CachedLoginUniqueControl() {
            usedLogins = new HashSet<>(userLoginDAO.listColumnValue("ulogin", String.class));
        }

        @Override
        public boolean isUnique(String data) {
            return !usedLogins.contains(data);
        }

        @Override
        public void register(String data) {
            usedLogins.add(data);
        }
    }

    class StrictLoginUniqueController implements UniqueController<String> {
        @Override
        public boolean isUnique(String data) {
            return userLoginDAO.isUnique(data);
        }

        @Override
        public void register(String data) {
            // nothing to do
        }
    }


    class ImportPersonBatch {

        Set<UserRole> employeeRoleSet;
        Set<UserRole> managerRoleSet;
        UniqueController<String> loginUniqueController;

        public ImportPersonBatch(Set<UserRole> employeeRoleSet, Set<UserRole> managerRoleSet, UniqueController<String> loginUniqueController) {
            this.employeeRoleSet = employeeRoleSet;
            this.managerRoleSet = managerRoleSet;
            this.loginUniqueController = loginUniqueController;
        }

        private void doImport (LegacyDAO_Transaction transaction, List<ExternalPerson> impListSrc) {
            try {
                Map<Long, ExternalPersonInfo> infoMap = legacySystemDAO.personCollector(impListSrc).asMap(transaction);
                Set<Long> existingIds = new HashSet<>(personDAO.existingKeys(infoMap.keySet()));

                //Person person = new Person();
                List<UserLogin> loginBatch = new ArrayList<>();
                List<Person> personBatch = new ArrayList<>();


                for (ExternalPerson impPerson : impListSrc) {
                    ExternalPersonInfo info = infoMap.get(impPerson.getId());

                    if (!existingIds.contains(impPerson.getId())) {
                        personBatch.add(MigrateUtils.fromExternalPerson(info));
                    }

                    if (info.proteiExtension != null && !info.proteiExtension.isFired()) {
                        UserLogin ulogin = MigrateUtils.externalEmployeeLogin(info);

                        if (ulogin == null || ulogin.getUlogin() == null) {
                            logger.error("unable to create user login for person {}", info.getDisplayName());
                            continue;
                        }

                        if (!loginUniqueController.isUnique(ulogin.getUlogin())) {
                            logger.error("login {} is used! duplicated data for {}", ulogin.getUlogin(), impPerson);
                            continue;
                        }

                        if (MigrateUtils.checkPersonIsManager(info.personData))
                            ulogin.setRoles(managerRoleSet);
                        else
                            ulogin.setRoles(employeeRoleSet);


                        loginUniqueController.register(ulogin.getUlogin());
                        loginBatch.add(ulogin);
                    }
                }

                if (!personBatch.isEmpty()) {
                    personDAO.persistBatch(personBatch);
                }

                if (!loginBatch.isEmpty()) {
                    userLoginDAO.persistBatch(loginBatch);
                    jdbcManyRelationsHelper.persist( loginBatch, "roles" );
                }
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

