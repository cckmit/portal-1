package ru.protei.portal.tools.migrate.imp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.tools.migrate.parts.ContactInfoMigrationFacade;
import ru.protei.portal.tools.migrate.struct.ExternalCompany;
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


    @Override
    public void importEmployes() {

    }

    @Override
    @Transactional
    public void importInitialData() {
        int comp_count = legacySystemDAO.runActionRTE (transaction ->
            new ImportCompanies ().doImport(transaction)
        );

        logger.debug("handled {} companies", comp_count);
    }



    class ImportCompanies {

        public int doImport (LegacySystemDAO.LegacyDAO_Transaction transaction) {
            try {
                List<ExternalCompany> src = transaction.dao(ExternalCompany.class).list();
                handleAsBatch (src, 100, importList -> {

                    Map<Long, Company> companyMap = map(
                            companyDAO.getListByKeys (keys(importList, c -> c.getId())),
                            company -> company.getId()
                    );

                    List<Company> toInsert = new ArrayList<>();
//                    List<Company> toUpdate = new ArrayList<>();

                    importList.forEach(imp -> {
                        Company local = companyMap.get(imp.getId());
                        if (local != null) {
                            local.setOldId(imp.getId());
                            companyDAO.partialMerge(local, "old_id");
                        }
                        else {
                            toInsert.add(fromExternalCompany(imp));
                        }
                    });

                    companyDAO.persistBatch(toInsert);
                });

                return src.size();
            }
            catch (SQLException e) {
                logger.error("sql-error, import companies", e);
                throw new RuntimeException(e);
            }
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
