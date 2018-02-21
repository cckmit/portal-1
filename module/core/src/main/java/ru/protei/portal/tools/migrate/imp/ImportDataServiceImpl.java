package ru.protei.portal.tools.migrate.imp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.dao.UserRoleDAO;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.tools.migrate.HelperService;
import ru.protei.portal.tools.migrate.parts.ContactInfoMigrationFacade;
import ru.protei.portal.tools.migrate.struct.ExternalCompany;
import ru.protei.portal.tools.migrate.struct.ExternalPerson;
import ru.protei.portal.tools.migrate.struct.ExternalPersonExtension;
import ru.protei.portal.tools.migrate.struct.ExternalPersonInfo;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;
import ru.protei.portal.tools.migrate.utils.MigrateUtils;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@EnableTransactionManagement
public class ImportDataServiceImpl implements ImportDataService {

    private static Logger logger = LoggerFactory.getLogger(ImportDataServiceImpl.class);

    @Autowired
    LegacySystemDAO legacySystemDAO;

    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    PersonDAO personDAO;

    @Autowired
    UserRoleDAO userRoleDAO;

    @Autowired
    UserLoginDAO userLoginDAO;

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;



    @Override
    public void importEmployes() {

    }


    @Override
    @Transactional
    public void importInitialData() {
        logger.debug("Full import mode run");

        InitialImport initialImport = new InitialImport();

        int _count = legacySystemDAO.runActionRTE(transaction -> initialImport.importCompanies(transaction));
        logger.debug("handled {} companies", _count);

/*        _count = legacySystemDAO.runActionRTE(transaction -> fullImport.importEmployes(transaction));
        logger.debug("handled {} persons", _count);*/
    }

    private Company fromExternalCompany(ExternalCompany imp) {
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

    class InitialImport {

        private UserRole employeeRole;
        private Set<UserRole> employeeRoleSet;
        private Map<String, String> email2loginMap;


        private String createLogin(ExternalPersonExtension ext) {
            String email = ext.getEmail();

            if (email == null || email.indexOf('@') <= 0)
                return null;

            String special = email2loginMap.get(email);

            if (special != null)
                return special;

            return email.substring(0, email.indexOf('@'));
        }

        public InitialImport() {
            employeeRole  = userRoleDAO.ensureExists(EMPLOYEE_ROLE_CODE, DEF_EMPL_SCOPE, DEF_EMPL_PRIV);
            employeeRoleSet = new HashSet<>();
            employeeRoleSet.add(employeeRole);

            email2loginMap = MigrateUtils.getMail2LoginRules();
        }

        public int importPersons (LegacySystemDAO.LegacyDAO_Transaction transaction) throws SQLException {
            List<ExternalPerson> src = transaction.dao(ExternalPerson.class).list("nCompanyID=?", 1L);
            Set<Long> existingIds = new HashSet<>(personDAO.keys());
            src.removeIf(imp -> existingIds.contains(imp.getId()));

            HelperService.splitBatch(src, 100, importList -> doImportPersonBatch(transaction, importList));

            return src.size();
        }

        private void doImportPersonBatch (LegacySystemDAO.LegacyDAO_Transaction transaction, List<ExternalPerson> impListSrc) {
            try {
                Map<Long, ExternalPersonInfo> infoMap = legacySystemDAO.personCollector(impListSrc).asMap(transaction);

                //Person person = new Person();
                List<UserLogin> loginBatch = new ArrayList<>();
                List<Person> personBatch = new ArrayList<>();


                for (ExternalPerson impPerson : impListSrc) {
                    ExternalPersonInfo info = infoMap.get(impPerson.getId());

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
                    person.setGender(impPerson.getSex() == null ? En_Gender.UNDEFINED : impPerson.getSex().intValue() == 1 ? En_Gender.MALE : En_Gender.FEMALE);
                    person.setInfo(impPerson.getInfo());
                    person.setLastName(impPerson.getLastName());

                    if (info.proteiExtension != null) {
                        // employee
                        person.setFired(info.proteiExtension.isRetired());
                        person.setIpAddress(info.proteiExtension.getIpAddress());
                        person.setPassportInfo(info.proteiExtension.get);

                        UserLogin ulogin = new UserLogin();
                        ulogin.setAdminStateId(En_AdminState.UNLOCKED.getId());
                        ulogin.setAuthTypeId(En_AuthType.LDAP.getId());
                        ulogin.setCreated(new Date());
                        ulogin.setInfo(info.getDisplayName());
                        ulogin.setPersonId(info.personData.getId());
                        ulogin.setUlogin(createLogin(info.proteiExtension));
                        ulogin.setRoles(employeeRoleSet);

                        if (ulogin.getUlogin() == null) {
                                logger.warn("unable to create user login for person {}", info.getDisplayName());
                        } else {
                                loginBatch.add(ulogin);
                        }
                    }

                    if (!loginBatch.isEmpty()) {
                        userLoginDAO.persistBatch(loginBatch);
                        jdbcManyRelationsHelper.persist( loginBatch, "roles" );
                    }
                }



            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }


        public int importCompanies(LegacySystemDAO.LegacyDAO_Transaction transaction) throws SQLException {
            List<ExternalCompany> src = transaction.dao(ExternalCompany.class).list();
            Set<Long> compIdSet = new HashSet<>(companyDAO.keys());
            // excludes already existing
            src.removeIf(imp -> compIdSet.contains(imp.getId()));

            HelperService.splitBatch(src, 100, importList ->
                    companyDAO.persistBatch(importList.stream().map(imp -> fromExternalCompany(imp)).collect(Collectors.toList()))
            );

            return src.size();
        }
    }




    public static final String EMPLOYEE_ROLE_CODE = "employee";

    private final static En_Privilege[] DEF_EMPL_PRIV = {
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

    private final static En_Scope DEF_EMPL_SCOPE = En_Scope.ROLE;

}

