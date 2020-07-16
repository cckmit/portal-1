package ru.protei.portal.tools.migrate.imp;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_MigrationEntry;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.tools.migrate.struct.*;
import ru.protei.portal.tools.migrate.sybase.LegacyDAO_Transaction;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;
import ru.protei.portal.tools.migrate.utils.MigrateUtils;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.sql.SQLException;
import java.util.*;
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

    @Transactional
    public void runIncrementalImport () {
        logger.debug("incremental import run");

        legacySystemDAO.runActionRTE(transaction -> {
            importCompanies(transaction);
            importProducts(transaction);
            importContacts(transaction);

            /**
             * Теперь это делает API для 1C
             */
//            if (portalConfig.data().legacySysConfig().isImportEmployeesEnabled()) {
//                importEmployes(transaction);
//            }
//            else {
//                logger.debug("import of employees from legacy db is disabled, skip");
//            }

            CaseImport caseImport = new CaseImport();
            caseImport.loadIncremental(transaction);
            caseImport.loadCommentsIncremental(transaction);

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

    private void importContacts (LegacyDAO_Transaction transaction) throws SQLException {
        logger.debug("incremental import, persons (contacts)");
        ImportPersonBatch importPersonBatch = new ImportPersonBatch(userRoleDAO.getDefaultEmployeeRoles(),
                userRoleDAO.getDefaultManagerRoles(),
                new StrictLoginUniqueController());

        MigrationEntry migrationEntry = migrationEntryDAO.getOrCreateEntry(En_MigrationEntry.PERSON_CUSTOMER);

        List<ExternalPerson> processList = transaction.dao(ExternalPerson.class).list("nID > ? and nCompanyID<>?", migrationEntry.getLastId(), 1L);

        if (processList.isEmpty()) {
            logger.debug("No new contacts in legacy db, exit now");
            return;
        }

        logger.debug("got new external person list, size = {}", processList.size());

        migrationEntry = migrationEntryDAO.updateEntry(En_MigrationEntry.PERSON_CUSTOMER, HelperFunc.last(processList));

        HelperFunc.splitBatch(
                processList,
                100, list -> importPersonBatch.doImport(transaction, list)
        );

        logger.debug("processed new contacts records: {}, last-id: {}", processList.size(), migrationEntry.getLastId());
        logger.debug("incremental import, contacts, done");
    }


    private void importEmployes(LegacyDAO_Transaction transaction) throws SQLException {
        /**
         * Теперь это делает API
         */
//        logger.debug("incremental import, employees");
//
//        ImportPersonBatch importPersonBatch = new ImportPersonBatch(userRoleDAO.getDefaultEmployeeRoles(),
//                userRoleDAO.getDefaultManagerRoles(),
//                new StrictLoginUniqueController());
//
//            MigrationEntry migrationEntry = migrationEntryDAO.getOrCreateEntry(En_MigrationEntry.PERSON_EMPLOYEE);
//
//            List<ExternalPerson> processList = transaction.dao(ExternalPerson.class).list("nID > ? and nCompanyID=?", migrationEntry.getLastId(), 1L);
//
//            if (processList.isEmpty()) {
//                logger.debug("No new employees in legacy db, exit now");
//                return;
//            }
//
//            logger.debug("got new external person list, size = {}", processList.size());
//
//            migrationEntry = migrationEntryDAO.updateEntry(En_MigrationEntry.PERSON_EMPLOYEE, HelperFunc.last(processList));
//
//            processList.removeIf(e -> personDAO.existsByLegacyId(e.getId()));
//
//            logger.debug("external person list after filtering, size = {}", processList.size());
//
//            HelperFunc.splitBatch(
//                    processList,
//                    100, list -> importPersonBatch.doImport(transaction, list)
//            );
//
//            logger.debug("processed new employees records: {}, last-id: {}", processList.size(), migrationEntry.getLastId());
//
//        logger.debug("incremental import, employees, done");
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
        if (userLogin.getCompanyId() != 1L) {
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
    public void forceCompanyUpdate() {
        InitialImport initialImport = new InitialImport();
        MigrateUtils.checkNoCompanyRecord(companyDAO);

        legacySystemDAO.runActionRTE(transaction -> {
            int _count = initialImport.importCompanies(transaction);
            logger.debug("handled {} companies", _count);
            return true;
        });
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
        final Map<Long, Long> supportStatusMap;
        final Map<Long, Long> productIdMap;
        final Map<Long, Long> companyMap;
        final Map<Long, Long> personMap;
        final Map<Long, Long> caseIdMap = new HashMap<>();

        public CaseImport() {
            supportStatusMap = caseStateMatrixDAO.getOldToNewStateMap(En_CaseType.CRM_SUPPORT);
            productIdMap = devUnitDAO.getProductOldToNewMap();
            companyMap = companyDAO.mapLegacyId();
            personMap = personDAO.mapLegacyId();
        }

        public CaseObject fromSupportSession (ExtCrmSession ext) {
            CaseObject obj = new CaseObject();

            obj.setId(null);
            obj.setCreated(ext.getCreated());
            obj.setCaseNumber(ext.getId());
            obj.setInitiatorCompanyId(companyMap.get(ext.getCompanyId()));
            obj.setCreatorInfo(ext.getCreator());
            obj.setCreatorIp(ext.getClientIp());
            obj.setProductId(ext.getProductId() == null ? null : productIdMap.get(ext.getProductId()));
            obj.setEmails(ext.getRecipients());
            obj.setDeleted(ext.isDeleted());
            obj.setPrivateCase(ext.isPrivate());

            obj.setImpLevel(HelperFunc.nvlt(ext.getImportance(),3).intValue());
            obj.setInfo(ext.getDescription());
//                    obj.setInitiatorId((Long) row.get("nDeclarantId"));
//                    obj.setKeywords((String)row.get("strKeyWord"));
//                    obj.setLocal(row.get("lIsLocal") == null ? 1 : ((Number) row.get("lIsLocal")).intValue());
            obj.setName("CRM-" + obj.getCaseNumber());
            obj.setManagerCompanyId(CrmConstants.Company.HOME_COMPANY_ID);
            obj.setManagerId(ext.getManagerId() != null ? personMap.get(ext.getManagerId()) : null);
            obj.setModified(ext.getLastUpdate());

            // (String)row.get("strExtID")
//        if (((Number)MigrateUtils.nvl(row.get("nCategoryID"), 8)).intValue() == 8) {

            obj.setExtId(En_CaseType.CRM_SUPPORT.makeGUID(obj.getCaseNumber()));
            obj.setType(En_CaseType.CRM_SUPPORT);

            Long stateId = supportStatusMap.get(ext.getStatusId());
            if (stateId == null) {
                logger.error("unable to map legacy state {} for crm-session {}", ext.getStatusId(), ext.getId());
                throw new RuntimeException("unable to map legacy state " + ext.getStatusId());
            }

            obj.setStateId(stateId);

//        }
//        else {
//            obj.setExtId(En_CaseType.CRM_MARKET.makeGUID(obj.getCaseNumber()));
//            obj.setTypeId(En_CaseType.CRM_MARKET.getId());
//            obj.setStateId(marketStatusMap.get(row.get("nStatusID")));
//        }

            return obj;
        }


        public int loadCommentsIncremental (LegacyDAO_Transaction transaction) throws SQLException {

            MigrationEntry migrationEntry = migrationEntryDAO.getOrCreateEntry(En_MigrationEntry.CRM_SUPPORT_SESSION_COMMENT);
            logger.debug("run import for crm-session comments from id={}", migrationEntry.getLastId());

            List<ExtCrmComment> src = transaction.dao(ExtCrmComment.class).list("nID > ?", migrationEntry.getLastId());
            if (src.isEmpty()) {
                logger.debug("no new comments for crm-sessions found");
                return 0;
            }

            migrationEntryDAO.updateEntry(En_MigrationEntry.CRM_SUPPORT_SESSION_COMMENT, HelperFunc.last(src));

            logger.debug("handle {} new comments", src.size());

            List<CaseComment> toInsert = new ArrayList<>();

            src.forEach(ext -> {
                Long ourCaseId = caseIdMap.computeIfAbsent(ext.getSessionId(), extId -> caseObjectDAO.getCaseId(En_CaseType.CRM_SUPPORT,extId));
                if (ourCaseId == null) {
                    logger.debug("unable to map external comment {}, case not found for {}", ext.getId(), ext.getSessionId());
                    return;
                }

                CaseComment comment = new CaseComment();
                comment.setCaseId(ourCaseId);
                comment.setCaseStateId(ext.getStatusId() != null ? supportStatusMap.get(ext.getStatusId()) : null);
                comment.setCreated(ext.getCreated());
                comment.setClientIp(ext.getClientIp());
                comment.setAuthorId(personMap.get(ext.getCreatorId()));
                if (comment.getAuthorId() == null) {
                    logger.debug("unable to map legacy person {}, skip comment", ext.getCreatorId());
//                    return;
                }

                comment.setText(ext.getComment());
                comment.setOldId(ext.getId());
                toInsert.add(comment);
            });

            logger.debug("insert batch of comments {}", toInsert.size());
            caseCommentDAO.persistBatch(toInsert);

            logger.debug("import of crm-comments completed");

            return src.size();
        }

        public int loadIncremental (LegacyDAO_Transaction transaction) throws SQLException {
            MigrationEntry migrationEntry = migrationEntryDAO.getOrCreateEntry(En_MigrationEntry.CRM_SUPPORT_SESSION);

            logger.debug("run import crm-support sessions from id={} and time={}", migrationEntry.getLastId(), migrationEntry.getLastUpdate());

            List<ExtCrmSession> src = transaction.dao(ExtCrmSession.class)
                    .list("nCategoryId=? and (nID > ? or dtLastUpdate > ?)", 8, migrationEntry.getLastId(), migrationEntry.getLastUpdate());

            if (src == null || src.isEmpty()) {
                logger.debug("no changes founds for crm-sessions");
                return 0;
            }

            long lastId = HelperFunc.max(src, e -> e.getId());
            Date lastUpdate = DateUtils.addSeconds(HelperFunc.max(src, e -> e.getLastUpdate()), 1);

            logger.debug("process new crm-sessions and updates, id={}, time={}", lastId, lastUpdate);

            migrationEntryDAO.updateEntry(En_MigrationEntry.CRM_SUPPORT_SESSION, lastId, lastUpdate);

            List<CaseObject> toInsert = new ArrayList<>();
            List<CaseObject> toUpdate = new ArrayList<>();

            for (ExtCrmSession ext : src) {
                CaseObject ourObj = caseObjectDAO.getCase(En_CaseType.CRM_SUPPORT, ext.getId());
                if (ourObj == null) {
                    // new case
                    ourObj = fromSupportSession(ext);

                    logger.debug("new crm-session: {}", ext.getId());

                    toInsert.add(ourObj);

                    List<Long> contacts = legacySystemDAO.getSessionContactList(transaction.connection(), ext.getId());
                    Long baseContactId = contacts != null && !contacts.isEmpty() ? personMap.get(contacts.get(0)) : null;

                    if (ourObj.getCreatorId() == null) {
                        ourObj.setCreatorId(HelperFunc.nvlt(baseContactId, ourObj.getManagerId()));
                    }

                    if (ourObj.getInitiatorId() == null) {
                        ourObj.setInitiatorId(HelperFunc.nvlt(baseContactId, ourObj.getManagerId()));
                    }
                }
                else {
                    caseIdMap.put(ext.getId(), ourObj.getId());
                    // update case
                    if (ourObj.getModified().before(ext.getLastUpdate())) {
                        logger.debug("update session {}", ext.getId());
                        // require to update
                        Long newState = supportStatusMap.get(ext.getStatusId());
                        if (newState != null)
                            ourObj.setStateId(newState);

                        if (ext.getManagerId() != null && personMap.containsKey(ext.getManagerId()))
                            ourObj.setManagerId(personMap.get(ext.getManagerId()));

                        if (ext.getProductId() != null && productIdMap.containsKey(ext.getProductId()))
                            ourObj.setProductId(productIdMap.get(ext.getProductId()));

                        if (ext.getCreated() != null && !ext.getCreated().equals(ourObj.getCreated())) {
                            ourObj.setCreated(ext.getCreated());
                        }

                        if (companyMap.containsKey(ext.getCompanyId()) && !companyMap.get(ext.getCompanyId()).equals(ourObj.getInitiatorCompanyId())) {
                            ourObj.setInitiatorCompanyId(companyMap.get(ext.getCompanyId()));
                        }

                        if (ext.getCreator() != null && !ext.getCreator().equals(ourObj.getCreatorInfo())) {
                            ourObj.setCreatorInfo(ext.getCreator());
                        }

                        if (ext.getClientIp() != null && !ext.getClientIp().equals(ourObj.getCreatorIp())) {
                            ourObj.setCreatorIp(ext.getClientIp());
                        }

                        if (ext.getRecipients() != null && !ext.getRecipients().equals(ourObj.getEmails())) {
                            ourObj.setEmails(ext.getRecipients());
                        }

                        if (ext.isDeleted() != ourObj.isDeleted()) {
                            ourObj.setDeleted(ext.isDeleted());
                        }

                        if (ext.isPrivate() != ourObj.isPrivateCase()) {
                            ourObj.setPrivateCase(ext.isPrivate());
                        }

                        if (ext.getImportance() != null && !ext.getImportance().equals(ourObj.getImpLevel())) {
                            ourObj.setImpLevel(ext.getImportance());
                        }

                        if (ext.getDescription() != null && !ext.getDescription().equals(ourObj.getInfo())) {
                            ourObj.setInfo(ext.getDescription());
                        }

                        if (ext.getLastUpdate() != null && !ext.getLastUpdate().equals(ourObj.getModified())) {
                            ourObj.setModified(ext.getLastUpdate());
                        }

                        toUpdate.add(ourObj);
                    }
                }
            }

            logger.debug("process batches...");
            if (!toInsert.isEmpty()) {
                caseObjectDAO.persistBatch(toInsert);
            }

            if (!toUpdate.isEmpty()) {
                caseObjectDAO.mergeBatch(toUpdate);
            }

            logger.debug("incremental import of crm-sessions completed, total handled {} records", src.size());

            return src.size();
        }

        public int initialCrmSupportSessionsImport(LegacyDAO_Transaction transaction) throws SQLException {
            Long startId = migrationEntryDAO.getOrCreateEntry(En_MigrationEntry.CRM_SUPPORT_SESSION).getLastId();
            if (startId == null)
                startId = 0L;

            List<ExtCrmSession> src = transaction.dao(ExtCrmSession.class).list("nCategoryId=? and nID > ?", 8, startId);
            migrationEntryDAO.updateEntry(En_MigrationEntry.CRM_SUPPORT_SESSION, HelperFunc.last(src));

            HelperFunc.splitBatch(src, 1000, procList -> {
                List<CaseObject> caseObjects = procList.stream().map(e -> fromSupportSession(e)).collect(Collectors.toList());

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
            importPersonBatch.setForceUpdate(true);
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

            List<Company> forUpate = new ArrayList<>();

            src.forEach(ext -> {
                if (compIdSet.contains(ext.getId())) {
                    forUpate.add(MigrateUtils.fromExternalCompany(ext));
                }
            });

            companyDAO.mergeBatch(forUpate);

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
        Map<Long,Long> companyIdMap;
        Map<Long,Long> legacyIdMap;
        boolean forceUpdate = false;

        public ImportPersonBatch(Set<UserRole> employeeRoleSet, Set<UserRole> managerRoleSet, UniqueController<String> loginUniqueController) {
            this.employeeRoleSet = employeeRoleSet;
            this.managerRoleSet = managerRoleSet;
            this.loginUniqueController = loginUniqueController;
            this.legacyIdMap = personDAO.mapLegacyId();
            this.companyIdMap = companyDAO.mapLegacyId();
        }

        public void setForceUpdate(boolean forceUpdate) {
            this.forceUpdate = forceUpdate;
        }

        public boolean isForceUpdate() {
            return forceUpdate;
        }

        private void doImport (LegacyDAO_Transaction transaction, List<ExternalPerson> impListSrc) {
            try {
                Map<Long, ExternalPersonInfo> infoMap = legacySystemDAO.personCollector(impListSrc).asMap(transaction);
//                Set<Long> existingIds = new HashSet<>(personDAO.existingKeys(infoMap.keySet()));

                //Person person = new Person();
                List<UserLogin> loginBatch = new ArrayList<>();
                List<Person> personBatch = new ArrayList<>();
                List<Person> updateBatch = new ArrayList<>();

                for (ExternalPerson impPerson : impListSrc) {
                    ExternalPersonInfo info = infoMap.get(impPerson.getId());

                    if (legacyIdMap.containsKey(impPerson.getId())) {
                        if (forceUpdate) {
                            updateBatch.add(MigrateUtils.fromExternalPerson(info, companyIdMap));
                        }
                    }
                    else {
                        personBatch.add(MigrateUtils.fromExternalPerson(info,companyIdMap));
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

                if (!updateBatch.isEmpty()) {
                    personDAO.mergeBatch(updateBatch);
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

