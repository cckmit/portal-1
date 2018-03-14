package ru.protei.portal.tools.migrate.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protei.sql.Tm_SqlHelper;
import ru.protei.portal.config.LocalizedTemplateCreator;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.CompanyGroupHomeDAO;
import ru.protei.portal.core.model.dao.MigrationEntryDAO;
import ru.protei.portal.core.model.dao.PortalBaseDAO;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.tools.migrate.HelperService;
import ru.protei.portal.tools.migrate.parts.BaseBatchProcess;
import ru.protei.portal.tools.migrate.parts.BatchInsertTask;
import ru.protei.portal.tools.migrate.parts.BatchUpdateTask;
import ru.protei.portal.tools.migrate.struct.*;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static ru.protei.portal.core.model.helper.HelperFunc.nvlt;

/**
 * Created by michael on 01.04.16.
 */
public class MigrateUtils {
    private static Logger logger = LoggerFactory.getLogger(MigrateUtils.class);

    public static final String MIGRATE_ACCOUNTS_FIX_JSON = "migrate_accounts_fix.json";

    public static final Long DEFAULT_PROTEI_ID = 1L;

    private static Map<String,String> _mail2loginRules;

    public static Long MICHAEL_Z_ID = 18L;
    public static Long DEFAULT_CREATOR_ID = MICHAEL_Z_ID;


    private static ObjectMapper jsonMapper;
    static {
        jsonMapper = new ObjectMapper();
        jsonMapper.setVisibilityChecker(jsonMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
    }

    public static void checkNoCompanyRecord (CompanyDAO companyDAO) {
        if (companyDAO.get(-1L) == null) {
            Company no_comp_rec = new Company();
            no_comp_rec.setCreated(new Date());
            no_comp_rec.setCname("no_company");
            no_comp_rec.setId(-1L);
            companyDAO.persist(no_comp_rec);
        }
    }

    public static void defaultProteiHomeSetup (CompanyGroupHomeDAO homeDAO) {
        /**
         * hardcoded import, I have no idea how to do it better
         */
        if (!homeDAO.checkIfHome(1L)) {
            CompanyHomeGroupItem protei_entry = new CompanyHomeGroupItem();
            protei_entry.setCompanyId(1L);
            protei_entry.setExternalCode("protei");
            homeDAO.persist(protei_entry);
        }
    }

    public static CompanySubscription fromExternalSubscription (ExtCompanyEmailSubs ext) {
        CompanySubscription x = new CompanySubscription();
        if (ext.getCompanyId() == null && ext.getEmail().contains("@protei.ru")) {
            x.setCompanyId(DEFAULT_PROTEI_ID);
        }
        x.setCompanyId(ext.getCompanyId());
        x.setEmail(ext.getEmail());
        x.setLangCode(ext.getLangInfo());

        return x;
    }

    public static Person createCompanyCommonPerson (Company company) {
        //
        Person p = new Person();
        p.setCompanyId(company.getId());
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
        return p;
    }

    public static UserLogin externalCustomerLogin (ExtContactLogin extLogin, Set<UserRole> roleSet) {
        UserLogin ulogin = new UserLogin();
        ulogin.setUlogin(extLogin.translatedLogin());
        ulogin.setAdminStateId(En_AdminState.UNLOCKED.getId());
        ulogin.setAuthTypeId(En_AuthType.LOCAL.getId());
        ulogin.setCreated(new Date());
        ulogin.setInfo(extLogin.getInfo());
        ulogin.setPersonId(extLogin.getPersonId());
        ulogin.setUpass(extLogin.getPassword());
        ulogin.setRoles(roleSet);
        return ulogin;
    }

    public static UserLogin externalEmployeeLogin (ExternalPersonInfo info) {
        UserLogin ulogin = new UserLogin();
        ulogin.setAdminStateId(En_AdminState.UNLOCKED.getId());
        ulogin.setAuthTypeId(En_AuthType.LDAP.getId());
        ulogin.setCreated(new Date());
        ulogin.setInfo(info.getDisplayName());
        ulogin.setPersonId(info.personData.getId());
        ulogin.setUlogin(createLogin(info.proteiExtension));
        return ulogin;
    }

    public static DevUnit fromExternalProject (ExternalDevProject project) {
        DevUnit u = new DevUnit(En_DevUnitType.COMPONENT.getId(), project.getName(), project.getInfo());
        u.setCreated(project.getCreated());
        u.setCreatorId(null);
        u.setLastUpdate(project.getLastUpdate());
        u.setStateId(En_DevUnitState.ACTIVE.getId());
        u.setOldId(project.getId());
        return u;
    }

    public static DevUnit fromExternalProduct (ExternalProduct product) {
        DevUnit u = new DevUnit(En_DevUnitType.PRODUCT.getId(), product.getName(), product.getInfo());
        u.setCreated(product.getCreated());
        u.setCreatorId(null);
        u.setLastUpdate(product.getLastUpdate());
        u.setStateId(En_DevUnitState.ACTIVE.getId());
        u.setOldId(product.getId());
        return u;
    }

    public static Company fromExternalCompany(ExternalCompany imp) {
        Company x = new Company();
        x.setId(imp.getId());
        x.setOldId(imp.getId());
        x.setCategory(new CompanyCategory(En_CompanyCategory.CUSTOMER.getId()));
        x.setInfo(imp.getInfo());
        x.setCname(imp.getName());
        x.setCreated(imp.getCreated());
        ContactInfoMigrationFacade infoFacade = new ContactInfoMigrationFacade(x.getContactInfo());

        infoFacade.addItem(En_ContactItemType.ADDRESS_LEGAL, imp.getLegalAddress());
        infoFacade.addItem(En_ContactItemType.ADDRESS, imp.getAddress());
        infoFacade.addItem(En_ContactItemType.EMAIL, imp.getEmail());
        infoFacade.addItem(En_ContactItemType.WEB_SITE, imp.getWebsite());
        return x;
    }

    public static Person fromExternalPerson (ExternalPersonInfo info, Map<Long,Long> companyIdMap) {
        ExternalPerson impPerson = info.personData;
        Person person = new Person();
        person.setId(impPerson.getId());
        person.setOldId(impPerson.getId());
        person.setBirthday(impPerson.getBirthday());
        person.setCompanyId(companyIdMap.get(impPerson.getCompanyId()));
        person.setCreated(impPerson.getCreated());
        person.setCreator(impPerson.getCreator());
        person.setDeleted(impPerson.isDeleted());
        person.setDepartment(impPerson.getDepartment());
        person.setDisplayName(impPerson.getDisplayName());
        person.setDisplayShortName(HelperService.generateDisplayShortName(impPerson));
        person.setFirstName(impPerson.getFirstName());
        person.setGender(impPerson.getGender());
        person.setInfo(impPerson.getInfo());
        person.setLastName(impPerson.getLastName());
        person.setPassportInfo(impPerson.getPassportInfo());
        person.setPosition(impPerson.getPosition());
        person.setSecondName(person.getSecondName());
        person.setCompanyId(impPerson.getCompanyId());

        if (person.getCompanyId() == null) {
            person.setCompanyId(-1L);
        }

        ContactInfoMigrationFacade contactInfoFacade = new ContactInfoMigrationFacade (person.getContactInfo());

        if (info.proteiExtension != null) {
            // employee
            person.setFired(info.proteiExtension.isFired());
            person.setIpAddress(info.proteiExtension.getIpAddress());

            contactInfoFacade.addEmail(info.proteiExtension.getEmail(), "Основной");
            contactInfoFacade.addPrivateEmail(info.proteiExtension.getOtherEmail(), "Персональный");
            contactInfoFacade.addHomePhone(info.proteiExtension.getHomeTel(), "Домашний");
            contactInfoFacade.addWorkPhone(info.proteiExtension.getWorkTel(), "Рабочий");
            contactInfoFacade.addMobilePhone(info.proteiExtension.getMobileTel(), "Мобильный");

            contactInfoFacade.addFax(info.proteiExtension.getFax(), "");
            contactInfoFacade.addLegalAddress(info.proteiExtension.getOfficialAddress(), "Официальный адрес");
            contactInfoFacade.addAddress(info.proteiExtension.getActualAddress(), "Фактический адрес");
            contactInfoFacade.addIcq(info.proteiExtension.getIcq(), "");
        }
        else {
            // contact person
            contactInfoFacade.addAddress(info.contactField("Адрес рабочий"), "Адрес рабочий");
            contactInfoFacade.addAddress(info.contactField("Адрес без категории"), "Адрес без категории");
            contactInfoFacade.addPrivateAddress(info.contactField("Адрес домашний"), "Адрес домашний");

            contactInfoFacade.addEmail(info.contactField("E-mail рабочий"), "рабочий");
            contactInfoFacade.addEmail(info.contactField("E-mail без категории"), "без категории");
            contactInfoFacade.addPrivateEmail(info.contactField("E-mail домашний"), "персональный");


            contactInfoFacade.addFax(info.contactField("Факс рабочий"), "Факс рабочий");
            contactInfoFacade.addFax(info.contactField("Факс без категории"), "Факс без категории");
            contactInfoFacade.addHomeFax(info.contactField("Факс домашний"), "Факс домашний");

            contactInfoFacade.addWorkPhone(info.contactField("Телефон рабочий"), "рабочий");
            contactInfoFacade.addWorkPhone(info.contactField("Телефон без категории"), "без категории");
            contactInfoFacade.addMobilePhone(info.contactField("Телефон мобильный"), "мобильный");
            contactInfoFacade.addHomePhone(info.contactField("Телефон домашний"), "домашний");

            contactInfoFacade.addJabber(
                    nvlt(
                            info.contactField("Интернет рабочий"),
                            nvlt(info.contactField("Интернет без категории"),
                                    info.contactField("Интернет домашний"))
                    ), "");

            contactInfoFacade.addIcq(
                    nvlt(info.contactField("ICQ рабочий"),
                        nvlt(info.contactField("ICQ без категории"), info.contactField("ICQ домашний"))
            ), "");
        }

        return person;
    }


    public static ExternalPerson lastEmployee (List<ExternalPerson> src) {
        return src.stream().filter(e -> e.getCompanyId() == MigrateUtils.DEFAULT_PROTEI_ID)
                .max(Comparator.comparing(ExternalPerson::getId)).orElse(null);
    }

    public static ExternalPerson lastCustomer (List<ExternalPerson> src) {
        return src.stream().filter(e -> e.getCompanyId() != MigrateUtils.DEFAULT_PROTEI_ID)
                .max(Comparator.comparing(ExternalPerson::getId)).orElse(null);
    }


    public static CaseComment fromCrmSupportComment (ExtCrmComment ext, Map<Long,Long> statusMap, Map<Long,Long> caseNumber2IdMap) {
        CaseComment comment = new CaseComment();

//        if (((Number)MigrateUtils.nvl(from.get("nCategoryID"), 8)).intValue() == 8) {
            comment.setCaseId(caseNumber2IdMap.get(ext.getSessionId()));
            comment.setCaseStateId(statusMap.get(ext.getStatusId()));
//        }
//        else {
//            comment.setCaseId(crmMarketIdMap.get(from.get("nSessionID")));
//            comment.setCaseStateId(marketStatusMap.get(from.get("nStatusID")));
//        }


        comment.setCreated(ext.getCreated());
        comment.setClientIp(ext.getClientIp());
        comment.setAuthorId(ext.getCreatorId());
        comment.setText(ext.getComment());
        comment.setOldId(ext.getId());

        return comment;
    }

    public static Object nvl (Object...arr) {
        for (Object v : arr) {
            if (v != null)
                return v;
        }

        return null;
    }

    private static final Set<String> MANAGER_KEYWORDS = new HashSet<>(Arrays.asList(
       "менеджер", "руководитель", "директор", "директора"
    ));

    public static boolean checkPersonIsManager (ExternalPerson person) {
        String position = person.getPosition();
        if (position == null)
            return false;

        for(String s : position.toLowerCase().split("\\s+"))
            if (MANAGER_KEYWORDS.contains(s.trim()))
                return true;

        return false;
    }

    public static String createLogin(ExternalPersonExtension ext) {
        return createLoginForEmail(ext.getEmail());
    }

    public static String createLoginForEmail(String email) {
        if (email == null || email.indexOf('@') <= 0)
            return null;

        String special = getMail2LoginRules().get(email);

        if (special != null)
            return special;

        return email.substring(0, email.indexOf('@'));
    }

    public static Map<String,String> getMail2LoginRules () {
        if (_mail2loginRules == null) {
            _mail2loginRules = new HashMap<>();

            try {
                File basePath = Paths.get(LocalizedTemplateCreator.class.getResource("/").toURI()).toFile();
//                URL url = ConfigUtils.locateFileOrDirectory(MIGRATE_ACCOUNTS_FIX_JSON);
//                if (url == null)
//                    return _mail2loginRules;

                File file = new File(basePath, MIGRATE_ACCOUNTS_FIX_JSON); //url == null ? null : Paths.get(url.toURI()).toFile();

                if (file.exists()) {
                    logger.debug("use accounts map config: " + file.getAbsolutePath());
                    for (Mail2Login entry : jsonMapper.readValue(file, Mail2Login[].class)) {
                        _mail2loginRules.put(entry.mail, entry.uid);
                    }
                }
                else {
                    logger.error("file {} doesn't exists", file.getAbsolutePath());
                }
            }
            catch (Throwable e) {
                logger.error("error while read accounts map", e);
            }
        }

        return _mail2loginRules;
    }


    /*
    *
    *
    * SQL
    *
    *
    */

    public static List<Map<String,Object>> buildListForTable (Connection conn, String tableName, String orderBy) throws SQLException {
        String sql = "select * from " + tableName + " order by " + orderBy;
        return mapSqlQuery(conn, sql, (Object[]) null);
    }


    public static List<Map<String, Object>> mapSqlQuery(Connection conn, String sql, Object... args) throws SQLException {
        List<Map<String,Object>> rez = new ArrayList<Map<String,Object>>();
        ResultSet rs = null;

        logger.debug("running query : " + sql);

        try (PreparedStatement st = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY)) {

            st.setFetchSize(1000);

            if (args != null && args.length > 0) {
                Tm_SqlHelper.configureStatement(st, args);
            }

            rs = st.executeQuery();


            logger.debug("loop over result-set::begin");

            while (rs.next()) {
                rez.add(Tm_SqlHelper.fetchRowAsMap(rs));
            }

            logger.debug("loop over result-set::end");
            return rez;
        }
        finally {
            Tm_SqlHelper.safeCloseResultSet(rs);
        }
    }



    /*
     * Deprecated migration helpers
     */

    public static <T> void runDefaultMigration (Connection sourceConnection,
                                                String entryId,
                                                String tableName,
                                                MigrationEntryDAO migrationEntryDAO,
                                                PortalBaseDAO<T> dao,
                                                MigrateAdapter<T> adapter) throws SQLException {

        runDefaultMigration(sourceConnection, entryId, tableName, migrationEntryDAO, dao, new BaseBatchProcess<>(), adapter);

    }

    public static <T> void runDefaultMigration (Connection sourceConnection,
                                                String entryId,
                                                String tableName,
                                                MigrationEntryDAO migrationEntryDAO,
                                                PortalBaseDAO<T> dao,
                                                BatchProcess<T> batchProcess,
                                                MigrateAdapter<T> adapter) throws SQLException {

        new BatchInsertTask(migrationEntryDAO, entryId)
                .forTable(tableName, "nID", "dtLastUpdate")
                .process(sourceConnection, dao, batchProcess, adapter)
                .dumpStats();

        new BatchUpdateTask(migrationEntryDAO, entryId)
                .forTable(tableName, "nID", "dtLastUpdate")
                .process(sourceConnection, dao, batchProcess, adapter)
                .dumpStats();

    }
}