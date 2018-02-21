package ru.protei.portal.tools.migrate.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import protei.sql.Tm_SqlHelper;
import ru.protei.portal.core.model.dao.MigrationEntryDAO;
import ru.protei.portal.core.model.dao.PortalBaseDAO;
import ru.protei.portal.core.model.dao.UserRoleDAO;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.tools.migrate.HelperService;
import ru.protei.portal.tools.migrate.parts.BaseBatchProcess;
import ru.protei.portal.tools.migrate.parts.BatchInsertTask;
import ru.protei.portal.tools.migrate.parts.BatchUpdateTask;
import ru.protei.portal.tools.migrate.parts.ContactInfoMigrationFacade;
import ru.protei.portal.tools.migrate.struct.*;

import java.net.URL;
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
    private static Logger logger = Logger.getLogger(MigrateUtils.class);

    public static final String MIGRATE_ACCOUNTS_FIX_JSON = "/migrate_accounts_fix.json";
    private static Map<String,String> _mail2loginRules;

    public static Long MICHAEL_Z_ID = 18L;
    public static Long DEFAULT_CREATOR_ID = MICHAEL_Z_ID;


    public static final String EMPLOYEE_ROLE_CODE = "employee";

    public final static En_Privilege[] DEF_EMPL_PRIV = {
            En_Privilege.ISSUE_CREATE,
            En_Privilege.ISSUE_EDIT,
            En_Privilege.ISSUE_EXPORT,
            En_Privilege.ISSUE_VIEW,
            En_Privilege.ISSUE_REPORT,
            En_Privilege.DASHBOARD_VIEW,
            En_Privilege.CONTACT_VIEW,
            En_Privilege.COMMON_PROFILE_VIEW,
            En_Privilege.COMPANY_VIEW
    };

    public final static En_Scope DEF_EMPL_SCOPE = En_Scope.ROLE;



    private static ObjectMapper jsonMapper;
    static {
        jsonMapper = new ObjectMapper();
        jsonMapper.setVisibilityChecker(jsonMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
    }

    public static Set<UserRole> defaultEmployeeRoleSet (UserRoleDAO userRoleDAO) {
        UserRole employeeRole  = userRoleDAO.ensureExists(EMPLOYEE_ROLE_CODE, DEF_EMPL_SCOPE, DEF_EMPL_PRIV);
        Set<UserRole> employeeRoleSet = new HashSet<>();
        employeeRoleSet.add(employeeRole);
        return employeeRoleSet;
    }


    public static UserLogin externalEmployeeLogin (ExternalPersonInfo info, Set<UserRole> roleSet) {
        UserLogin ulogin = new UserLogin();
        ulogin.setAdminStateId(En_AdminState.UNLOCKED.getId());
        ulogin.setAuthTypeId(En_AuthType.LDAP.getId());
        ulogin.setCreated(new Date());
        ulogin.setInfo(info.getDisplayName());
        ulogin.setPersonId(info.personData.getId());
        ulogin.setUlogin(createLogin(info.proteiExtension));
        ulogin.setRoles(roleSet);
        return ulogin;
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

    public static Person fromExternalPerson (ExternalPersonInfo info) {
        ExternalPerson impPerson = info.personData;
        Person person = new Person();
        person.setId(impPerson.getId());
        person.setOldId(impPerson.getId());
        person.setBirthday(impPerson.getBirthday());
        person.setCompanyId(impPerson.getCompanyId());
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



    public static Object nvl (Object...arr) {
        for (Object v : arr) {
            if (v != null)
                return v;
        }

        return null;
    }


    private static String createLogin(ExternalPersonExtension ext) {
        String email = ext.getEmail();

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
                URL url = MigrateUtils.class.getResource(MIGRATE_ACCOUNTS_FIX_JSON);
                logger.debug("use accounts map config: " + url);
                for (Mail2Login entry : jsonMapper.readValue(url, Mail2Login[].class)) {
                    _mail2loginRules.put(entry.mail, entry.uid);
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
