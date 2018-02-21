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
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.tools.migrate.struct.ExternalCompany;
import ru.protei.portal.tools.migrate.struct.ExternalPerson;
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


    class InitialImport {

        private Set<UserRole> employeeRoleSet;

        public InitialImport() {
            employeeRoleSet = MigrateUtils.defaultEmployeeRoleSet(userRoleDAO);
        }

        public int importPersons (LegacySystemDAO.LegacyDAO_Transaction transaction) throws SQLException {
            List<ExternalPerson> src = transaction.dao(ExternalPerson.class).list("nCompanyID=?", 1L);
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
                    Person person = MigrateUtils.fromExternalPerson(info);

                    if (info.proteiExtension != null) {
                        UserLogin ulogin = MigrateUtils.externalEmployeeLogin(info, employeeRoleSet);

                        if (ulogin.getUlogin() == null) {
                            logger.warn("unable to create user login for person {}", info.getDisplayName());
                        } else {
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

