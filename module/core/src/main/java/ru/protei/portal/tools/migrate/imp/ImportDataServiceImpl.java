package ru.protei.portal.tools.migrate.imp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.tools.migrate.struct.ExtContactLogin;
import ru.protei.portal.tools.migrate.struct.ExternalCompany;
import ru.protei.portal.tools.migrate.struct.ExternalPerson;
import ru.protei.portal.tools.migrate.struct.ExternalPersonInfo;
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
    JdbcManyRelationsHelper jdbcManyRelationsHelper;



    @Override
    public void importEmployes() {

    }


    @Override
    @Transactional
    public void importInitialData() {
        logger.debug("Full import mode run");

        InitialImport initialImport = new InitialImport();

        MigrateUtils.checkNoCompanyRecord(companyDAO);

        int _count = legacySystemDAO.runActionRTE(transaction -> initialImport.importCompanies(transaction));
        logger.debug("handled {} companies", _count);

        MigrateUtils.defaultProteiHomeSetup(companyGroupHomeDAO);

        _count = legacySystemDAO.runActionRTE(transaction -> initialImport.importPersons(transaction));
        logger.debug("handled {} persons", _count);

        _count = legacySystemDAO.runActionRTE(transaction -> initialImport.importClientLogins(transaction));
        logger.debug("handled {} external logins", _count);
    }


    class InitialImport {

        private Set<UserRole> employeeRoleSet;
        private Set<UserRole> customerRoleSet;

        private Set<String> exlogins;

        public InitialImport() {
            employeeRoleSet = MigrateUtils.defaultEmployeeRoleSet(userRoleDAO);
            customerRoleSet = MigrateUtils.defaultCustomerRoleSet(userRoleDAO);

            exlogins = new HashSet<>(userLoginDAO.listColumnValue("ulogin", String.class));
        }


        public int importClientLogins (LegacySystemDAO.LegacyDAO_Transaction transaction) throws SQLException {
            // import client and companies logins
            List<ExtContactLogin> src = transaction.dao(ExtContactLogin.class).list();
            src.removeIf(x -> exlogins.contains(x.translatedLogin()));
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
                UserLogin ulogin = MigrateUtils.externalCustomerLogin(extLogin, customerRoleSet);

                if (extLogin.getPersonId() == null) {
                    // common company account
                    Person common = companyCommonAccounts.computeIfAbsent(extLogin.getCompanyId(), id-> {
                        Company company = companyMap.get(id);
                        Person commonPerson = personDAO.findContactByName(id, company.getCname());
                        if (commonPerson == null) {
                            commonPerson = MigrateUtils.createCompanyCommonPerson(company);
                            personDAO.persist(commonPerson);
                        }
                        return commonPerson;
                    });

                    extLogin.setPersonId(common.getId());
                }

                customerLogins.add(ulogin);
            });


            if (!customerLogins.isEmpty()) {
                userLoginDAO.persistBatch(customerLogins);
                jdbcManyRelationsHelper.persist( customerLogins, "roles" );
            }
        }


        public int importPersons (LegacySystemDAO.LegacyDAO_Transaction transaction) throws SQLException {
            List<ExternalPerson> src = transaction.dao(ExternalPerson.class).list();
            Set<Long> existingIds = new HashSet<>(personDAO.keys());
            src.removeIf(imp -> existingIds.contains(imp.getId()));

            HelperFunc.splitBatch(src, 100, importList -> doImportPersonBatch(transaction, importList));

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
                    personBatch.add (MigrateUtils.fromExternalPerson(info));

                    if (info.proteiExtension != null && !info.proteiExtension.isFired()) {
                        UserLogin ulogin = MigrateUtils.externalEmployeeLogin(info, employeeRoleSet);

                        if (exlogins.contains(ulogin.getUlogin())) {
                            logger.error("login {} is used! duplicated data for {}", ulogin.getUlogin(), impPerson);
                            continue;
                        }

                        if (ulogin.getUlogin() == null) {
                            logger.error("unable to create user login for person {}", info.getDisplayName());
                        } else {
                            exlogins.add(ulogin.getUlogin());
                            loginBatch.add(ulogin);
                        }
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


        public int importCompanies(LegacySystemDAO.LegacyDAO_Transaction transaction) throws SQLException {

            List<ExternalCompany> src = transaction.dao(ExternalCompany.class).list();
            Set<Long> compIdSet = new HashSet<>(companyDAO.keys());
            // excludes already existing
            src.removeIf(imp -> compIdSet.contains(imp.getId()));

            HelperFunc.splitBatch(src, 100, importList ->
                    companyDAO.persistBatch(importList.stream().map(imp -> MigrateUtils.fromExternalCompany(imp)).collect(Collectors.toList()))
            );

            return src.size();
        }
    }
}

