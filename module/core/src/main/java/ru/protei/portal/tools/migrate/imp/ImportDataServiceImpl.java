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
import ru.protei.portal.tools.migrate.parts.ContactInfoMigrationFacade;
import ru.protei.portal.tools.migrate.struct.ExternalCompany;
import ru.protei.portal.tools.migrate.struct.ExternalPerson;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
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

        FullImport fullImport = new FullImport();

        int _count = legacySystemDAO.runActionRTE(transaction -> fullImport.importCompanies(transaction));
        logger.debug("handled {} companies", _count);

        _count = legacySystemDAO.runActionRTE(transaction -> fullImport.importEmployes(transaction));
        logger.debug("handled {} persons", _count);


    }

    class FullImport {

        public int importEmployes (LegacySystemDAO.LegacyDAO_Transaction transaction) throws SQLException {
            List<ExternalPerson> src = transaction.dao(ExternalPerson.class).list("nCompanyID=?", 1L);

            handleAsBatch(src, 100, importList -> {
                Map<Long, Person> localMap = map (
                        personDAO.getListByKeys(keys(importList, imp -> imp.getId())),
                        person -> person.getId()
                );

                importList.forEach(imp -> {
                    Person local = localMap.get(imp.getId());
                    if (local != null) {
                        //
                        if (!equalsNotNull(local.getOldId(), imp.getId())) {
                            local.setOldId(imp.getId());
                            personDAO.partialMerge(local, "old_id");
                        }
                        else {
                            //
                        }
                    }
                });
            });

            return  0;
        }

        public int importCompanies(LegacySystemDAO.LegacyDAO_Transaction transaction) throws SQLException {
                List<ExternalCompany> src = transaction.dao(ExternalCompany.class).list();
                handleAsBatch (src, 100, importList -> {

                    Map<Long, Company> companyMap = map(
                            companyDAO.getListByKeys (keys(importList, c -> c.getId())),
                            company -> company.getId()
                    );

                    List<Company> toInsert = new ArrayList<>();

                    importList.forEach(imp -> {
                        Company local = companyMap.get(imp.getId());
                        if (local != null) {
                            if (!equalsNotNull(local.getOldId(), imp.getId())) {
                                local.setOldId(imp.getId());
                                companyDAO.partialMerge(local, "old_id");
                            }
                        }
                        else {
                            toInsert.add(fromExternalCompany(imp));
                        }
                    });

                    companyDAO.persistBatch(toInsert);
                });

                return src.size();
        }
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

    public static boolean equalsNotNull (Number a, Number b) {
        return a != null && b != null && a.equals(b);
    }

    private static <K,T> List<K> keys (List<T> src, Function<T,K> keyExtractor) {
        return src.stream().map(item -> keyExtractor.apply(item)).collect(Collectors.toList());
    }

    private static <K,T> Map<K,T> map (List<T> src, Function<T,K> keyExtractor) {
        Map<K,T> result = new HashMap<>();
        src.forEach(item -> result.put(keyExtractor.apply(item), item));
        return result;
    }

    private <T> void handleAsBatch (List<T> full_list, int batchSize, Consumer<List<T>> consumer) {
        int full_batches = full_list.size()/batchSize;

        if (full_batches == 0)
            consumer.accept(full_list);
        else {
            for (int i = 0; i < full_batches; i++) {
                consumer.accept(full_list.subList(i * batchSize, (i + 1) * batchSize));
            }
            // rest
            consumer.accept(full_list.subList(full_batches*batchSize, full_list.size()));
        }
    }
}
