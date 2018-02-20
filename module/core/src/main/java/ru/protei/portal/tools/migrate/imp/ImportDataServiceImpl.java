package ru.protei.portal.tools.migrate.imp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.tools.migrate.HelperService;
import ru.protei.portal.tools.migrate.parts.ContactInfoMigrationFacade;
import ru.protei.portal.tools.migrate.struct.ExternalCompany;
import ru.protei.portal.tools.migrate.struct.ExternalPerson;
import ru.protei.portal.tools.migrate.struct.ExternalPersonInfo;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;

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

}
