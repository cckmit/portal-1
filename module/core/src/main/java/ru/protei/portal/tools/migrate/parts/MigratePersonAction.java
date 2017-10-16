package ru.protei.portal.tools.migrate.parts;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_AdminState;
import ru.protei.portal.core.model.dict.En_AuthType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.tools.migrate.tools.BatchProcess;
import ru.protei.portal.tools.migrate.tools.MigrateAction;
import ru.protei.portal.tools.migrate.tools.MigrateAdapter;
import ru.protei.portal.tools.migrate.tools.MigrateUtils;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by michael on 04.04.16.
 */
public class MigratePersonAction implements MigrateAction {

    public static final String EMPLOYEE_ROLE_CODE = "employee";
    private static Logger logger = LoggerFactory.getLogger(MigratePersonAction.class);

    public static final String TM_PERSON_ITEM_CODE = "Tm_Person";
    public static final String TM_PERSON_PROTEI_ITEM_CODE = "Tm_Person_Protei";
    private static Pattern PERSON_PROP = Pattern.compile("!begin!([^=]+)=(.*?)!end!;?", Pattern.DOTALL);

    @Autowired
    private PersonDAO dao;

    @Autowired
    private MigrationEntryDAO migrateDAO;

    @Autowired
    private UserLoginDAO userLoginDAO;

    @Autowired
    CompanyGroupHomeDAO groupHomeDAO;

    @Autowired
    UserRoleDAO userRoleDAO;

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;


    Map<String, String> email2loginMap;


    public MigratePersonAction() {
        this.email2loginMap = MigrateUtils.getMail2LoginRules();
    }

    @Override
    public int orderOfExec() {
        return 1;
    }

    private static Map<String, String> splitProps(String props) {
        if (props == null)
            return Collections.emptyMap();

        Map<String, String> rez = new HashMap<>();

        Matcher m = PERSON_PROP.matcher(props);

        while (m.find()) {
            rez.put(m.group(1), m.group(2));
        }

        return rez;
    }

    private static String nvl(String x, String d) {
        return x != null ? x : d;
    }

    private String makeNewItemsQuery(String idExpression) {
        return getBaseSelectQuery() +
                " where  p.nID > ? and " + idExpression + " order by p.nID";
    }

    private String makeUpdatedItemsQuery() {
        return getBaseSelectQuery() +
                " where p.nID <= ? and p.dtLastUpdate > ? order by p.dtLastUpdate";
    }

    private String getBaseSelectQuery() {
        return " select p.*, p.strClient||'@'||p.strClientIP strCreatorID, \"resource\".func_getfullfio (p.strLastName,p.strFirstName,p.strPatronymic) fullFio, prop.properties, cat.nCategoryID, cd.strValue category," +
                " pext.strIP_Address,pext.strE_Mail, pext.strOther_E_mail, pext.strHomeTel, pext.strWorkTel, pext.strMobileTel, pext.strFaxTel, pext.strOficialAddress, pext.strActualAddress, pext.strICQ, pext.nJID, pext.lRetired, dep.strDescription" +
                " from \"resource\".tm_person p" +
                " left outer join ( select nPersonID, LIST('!begin!'||pp.strValue||' '||c.strValue||'='||p.strValue||'!end!',';') properties from \"resource\".tm_person2property p" +
                " join \"resource\".tm_category c on (p.nCategoryID=c.nID)" +
                " join \"resource\".tm_personproperty pp on (pp.nID=p.nPropertyID) group by nPersonID ) prop on (prop.nPersonID=p.nID)" +
                " left outer join \"resource\".tm_person2category cat on (cat.nPersonID=p.nID)" +
                " left outer join \"resource\".tm_category cd on (cd.nID=cat.nCategoryID)" +
                " left outer join \"resource\".Tm_PersonPROTEI_Extension pext on (pext.nID=p.nID)" +
                " left outer join \"OK\".Tm_Department dep on (dep.nID=pext.nDepartmentID)";
    }


    private String createLogin(Person p) {
        String email = p == null ? null : new PlainContactInfoFacade(p.getContactInfo()).getEmail();
        if (email == null || email.indexOf('@') <= 0)
            return null;

        String special = email2loginMap.get(email);

        if (special != null)
            return special;

        return email.substring(0, email.indexOf('@'));
    }


    private final static En_Privilege [] DEF_EMPL_PRIV = {
            En_Privilege.ISSUE_EDIT,
            En_Privilege.ISSUE_EXPORT,
            En_Privilege.CONTACT_VIEW,
            En_Privilege.ISSUE_VIEW,
            En_Privilege.ISSUE_REPORT,
            En_Privilege.COMMON_PROFILE_VIEW,
            En_Privilege.ISSUE_CREATE,
            En_Privilege.COMPANY_VIEW
    };

    @Override
    public void migrate(Connection sourceConnection) throws SQLException {



        BatchProcess<Person> workersBatchProcess = new BaseBatchProcess<Person>() {
            public void afterInsert(List<Person> insertedEntries) {
                List<UserLogin> loginBatch = new ArrayList<>();

                UserRole employeeRole = userRoleDAO.ensureExists(EMPLOYEE_ROLE_CODE, DEF_EMPL_PRIV);

                Set<UserRole> roles = new HashSet<>();
                roles.add(employeeRole);

                for (Person p : insertedEntries) {
                    if (groupHomeDAO.checkIfHome(p.getCompanyId()) && !p.isDeleted() && !p.isFired()) {

                        UserLogin ulogin = new UserLogin();
                        ulogin.setAdminStateId(En_AdminState.UNLOCKED.getId());
                        ulogin.setAuthTypeId(En_AuthType.LDAP.getId());
                        ulogin.setCreated(new Date());
                        ulogin.setInfo(p.getDisplayName());
                        ulogin.setPersonId(p.getId());
                        ulogin.setUlogin(createLogin(p));
                        ulogin.setRoles(roles);

                        if (ulogin.getUlogin() == null) {
                            logger.warn("unable to create user login for person {}", p.toDebugString());
                        }
                        else if (userLoginDAO.findByLogin(ulogin.getUlogin()) != null) {
                            logger.warn("duplicated login {}", ulogin.getUlogin());
                        }
                        else
                            loginBatch.add(ulogin);
                    }
                }

                if (!loginBatch.isEmpty()) {
//                    loginBatch.forEach(user -> userLoginDAO.persist(user));
                    userLoginDAO.persistBatch(loginBatch);
                    jdbcManyRelationsHelper.persist( loginBatch, "roles" );
                }
            }
        };


        MigrateAdapter<Person> migrateAdapter = row -> {
            Person x = new Person();
            x.setId(((Number) row.get("nID")).longValue());
            x.setBirthday((Date) row.get("dtBirthday"));

            if (row.get("nCompanyID") == null) {
                logger.warn("company is null for: {}", row);
                x.setCompanyId(-1L);
            } else
                x.setCompanyId(((Number) row.get("nCompanyID")).longValue());

            x.setCreated((Date) row.get("dtCreation"));

            x.setCreator((String) row.get("strCreatorID"));

            x.setDisplayName((String) row.get("fullFio"));

            x.setFirstName((String) row.get("strFirstName"));
            if (x.getFirstName() == null)
                x.setFirstName("-");

            x.setLastName((String) row.get("strLastName"));
            if (x.getLastName() == null)
                x.setLastName("-");

            x.setSecondName((String) row.get("strPatronymic"));

            x.setDisplayShortName(generateDisplayShortName(x.getFirstName(), x.getLastName(), x.getSecondName()));

            x.setInfo((String) row.get("strInfo"));

            x.setDeleted( ((Number) row.get("lDeleted")).intValue() != 0 );

            x.setFired(
                    row.get("lRetired") != null && ((Number) row.get("lRetired")).intValue() != 0
            );
            x.setPassportInfo((String) row.get("strPassportInfo"));
            x.setGender(row.get("nSexID") == null ? En_Gender.UNDEFINED : ((Number) row.get("nSexID")).intValue() == 1 ? En_Gender.MALE : En_Gender.FEMALE);
            x.setPosition(nvl((String) row.get("strPosition"), (String) row.get("category")));
            x.setDepartment((String) row.get("strDescription"));

            x.setIpAddress((String) row.get("strIP_Address"));

            ContactInfoMigrationFacade contactInfoFacade = new ContactInfoMigrationFacade (x.getContactInfo());

            contactInfoFacade.addEmail((String) row.get("strE_Mail"), "Основной");

            contactInfoFacade.addPrivateEmail((String) row.get("strOther_E_mail"), "Персональный");

            contactInfoFacade.addHomePhone((String) row.get("strHomeTel"), "Домашний");

            contactInfoFacade.addWorkPhone((String) row.get("strWorkTel"), "Рабочий");

            contactInfoFacade.addMobilePhone((String) row.get("strMobileTel"), "Мобильный");

            contactInfoFacade.addFax((String) row.get("strFaxTel"), "");

            contactInfoFacade.addLegalAddress((String) row.get("strOficialAddress"), "Официальный адрес");

            contactInfoFacade.addAddress((String) row.get("strActualAddress"), "Фактический адрес");

            contactInfoFacade.addIcq((String) row.get("strICQ"), "");

            contactInfoFacade.addJabber(
                    row.get("nJID") != null && ((Number) row.get("nJID")).longValue() > 0 ? row.get("nJID").toString() : null
                    , "");


            if (row.get("properties") != null) {
                logger.debug("properties: ", row.get("properties"));

                Map<String, String> xp = splitProps((String) row.get("properties"));

                contactInfoFacade.addAddress(xp.get("Адрес рабочий"), "Адрес рабочий");
                contactInfoFacade.addAddress(xp.get("Адрес без категории"), "Адрес без категории");
                contactInfoFacade.addPrivateAddress(xp.get("Адрес домашний"), "Адрес домашний");

                contactInfoFacade.addEmail(xp.get("E-mail рабочий"), "рабочий");
                contactInfoFacade.addEmail(xp.get("E-mail без категории"), "без категории");
                contactInfoFacade.addPrivateEmail(xp.get("E-mail домашний"), "персональный");


                contactInfoFacade.addFax(xp.get("Факс рабочий"), "Факс рабочий");
                contactInfoFacade.addFax(xp.get("Факс без категории"), "Факс без категории");
                contactInfoFacade.addHomeFax(xp.get("Факс домашний"), "Факс домашний");

                contactInfoFacade.addWorkPhone(xp.get("Телефон рабочий"), "рабочий");
                contactInfoFacade.addWorkPhone(xp.get("Телефон без категории"), "без категории");
                contactInfoFacade.addMobilePhone(xp.get("Телефон мобильный"), "мобильный");
                contactInfoFacade.addHomePhone(xp.get("Телефон домашний"), "домашний");

                contactInfoFacade.addJabber(
                        nvl(
                                xp.get("Интернет рабочий"),
                                nvl(xp.get("Интернет без категории"), xp.get("Интернет домашний"))
                        ), "");

                contactInfoFacade.addIcq(nvl(xp.get("ICQ рабочий"), nvl(xp.get("ICQ без категории"), xp.get("ICQ домашний"))), "");
            }

            logger.debug("id={} / {}", x.getId() , x.getDisplayName());
            return x;
        };


        /** migrate workers **/
        new BatchInsertTask(migrateDAO, TM_PERSON_PROTEI_ITEM_CODE)
                .forQuery(makeNewItemsQuery("p.nID <= 1000"), "nID", "dtLastUpdate")
                .process(sourceConnection, dao, workersBatchProcess, migrateAdapter)
                .dumpStats();

        new BatchUpdateTask(migrateDAO, TM_PERSON_PROTEI_ITEM_CODE)
                .forQuery(makeUpdatedItemsQuery(), "nID", "dtLastUpdate")
                .process(sourceConnection, dao, workersBatchProcess, migrateAdapter)
                .dumpStats();


        /** migrate crm-contacts */
        new BatchInsertTask(migrateDAO, TM_PERSON_ITEM_CODE)
                .forQuery(makeNewItemsQuery("p.nID > 1000"), "nID", "dtLastUpdate")
                .process(sourceConnection, dao, new BaseBatchProcess<> (), migrateAdapter)
                .dumpStats();

        new BatchUpdateTask(migrateDAO, TM_PERSON_ITEM_CODE)
                .forQuery(makeUpdatedItemsQuery(), "nID", "dtLastUpdate")
                .process(sourceConnection, dao, new BaseBatchProcess<> (), migrateAdapter)
                .dumpStats();
    }


    private String generateDisplayShortName(String firstName, String lastName, String secondName) {
        return lastName + " " + (!firstName.isEmpty() ? firstName.charAt(0) + "." : "") + (secondName != null && !secondName.isEmpty() ? secondName.charAt(0) + "." : "");
    }


//   private HashMap getDepartmentIdPairs(){
//      int[] oldIds = {1,45,16,10,11,9,12,8,50,166,161,160,53,170,162,29,167,21,19,37,26,41,164,31,33,159,23,171,165,46,49,47,48,43,17,15,13,169,4,168,3,42,28,34,25,20,22,38,30,163,40,32,27,7,39,5,2,18,6,54,52,14}; // Id подразделений отсортированных по strDescription
//      HashMap<Integer, Integer> conformity = new HashMap<>();
//
//
//      for(int i= 0; i < oldIds.length; i++){
//         conformity.put(oldIds[i], i+1); // заменяем их на новые id начиная с 1 в порядке возрастания
//      }
//      return conformity;
//   }


}
