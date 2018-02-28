package ru.protei.portal.tools.migrate.export;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.CreateAuditObjectEvent;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dao.ExportSybEntryDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.ExportSybEntry;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.tools.migrate.struct.ExternalCompany;
import ru.protei.portal.tools.migrate.struct.ExternalPerson;
import ru.protei.portal.tools.migrate.struct.ExternalProduct;
import ru.protei.portal.tools.migrate.sybase.LegacyDAO_Transaction;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class ActiveExportDataService implements ExportDataService {

    private static Logger logger = LoggerFactory.getLogger(ActiveExportDataService.class);

    @Autowired
    PortalConfig config;

    @Autowired
    LegacySystemDAO legacyDAO;

    @Autowired
    ExportSybEntryDAO exportSybEntryDAO;

    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    PersonDAO personDAO;

    @Autowired
    DevUnitDAO devUnitDAO;

    private Map<Class<? extends AuditableObject>, ExportHandler> handlerMap;

    private BlockingQueue<ExportSybEntry> queue;
    private ExecutorService executorService;

    private interface ExportHandler {
        boolean export (AuditableObject object);
    }

    public ActiveExportDataService() {
        handlerMap = new HashMap<>();
        queue = new LinkedBlockingQueue<>();
    }

    @PostConstruct
    private void __init () {
        // load after start
        logger.debug("init export data service");

        handlerMap.put(DevUnit.class, object -> {
            DevUnit devUnit = (DevUnit)object;
            return  (devUnit.getType() == En_DevUnitType.PRODUCT && exportProduct(devUnit) == En_ResultStatus.OK);
        });

        handlerMap.put(Company.class, object -> exportCompany((Company)object) == En_ResultStatus.OK);
        handlerMap.put(Person.class, object -> exportPerson((Person) object) == En_ResultStatus.OK);

        queue.addAll(exportSybEntryDAO.getListByCondition("instance_id=?", config.data().legacySysConfig().getInstanceId()));

        logger.debug("preload queue size: {}", queue.size());

        executorService = Executors.newSingleThreadExecutor();

        executorService.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    logger.debug("await data in queue");
                    ExportSybEntry entry = queue.poll(30L, TimeUnit.SECONDS);
                    if (entry == null)
                        continue;

                    logger.debug("got entry from queue : {}", entry);
                    ExportHandler handler = handlerMap.get(entry.getEntry().getClass());
                    if (handler != null) {
                        boolean res = handler.export(entry.getEntry());
                        logger.debug("handler invocation resutl = {}", res);
                    } else {
                        logger.debug("no handler for entry: {}", entry);
                    }

                    exportSybEntryDAO.remove(entry);
                }

                logger.debug("Export-handler task is requested to exit");
            }
            catch (Exception e) {
                logger.error("Error in export-handler task, exit now", e);
            }
        });
    }

    @PreDestroy
    private void _onDestroy () {
        executorService.shutdownNow();
        queue.clear();
    }

    public boolean supportedObject (AuditableObject object) {
        return handlerMap.containsKey(object.getClass());
    }

    @EventListener
    public boolean handleEvent (CreateAuditObjectEvent event) {
        logger.debug("ExportService, audit event handle, object : {}", event.getAuditObject());

        if (supportedObject(event.getAuditObject().getEntryInfo())) {
            ExportSybEntry exportEntry = new ExportSybEntry(event.getAuditObject().getEntryInfo(),
                    config.data().legacySysConfig().getInstanceId());

            if (exportSybEntryDAO.persist(exportEntry) != null) {
                logger.debug("registered exported entry for type {}, obj-id={}, reg-id={}", exportEntry.getEntityType(), exportEntry.getLocalId(), exportEntry.getId());
                queue.offer(exportEntry);
                return true;
            }
            else {
                logger.error("unable to register export-entry, type={}, obj-id={}", exportEntry.getEntityType(), exportEntry.getLocalId());
            }
        }
        else {
            logger.debug("Export is not enabled for type {}, skip event", event.getAuditObject().getEntryInfo().getAuditType());
        }

        return false;
    }


    @Override
    public En_ResultStatus exportCompany(Company company) {
        try {
            return legacyDAO.runAction(transaction -> {
                _doExportCompany(company, transaction);
                transaction.commit();
                return En_ResultStatus.OK;
            });
        }
        catch (SQLException e) {
            logger.error("unable to export company", e);
            return En_ResultStatus.DB_COMMON_ERROR;
        }
    }

    private ExternalCompany _doExportCompany(Company company, LegacyDAO_Transaction transaction) throws SQLException {
        ExternalCompany externalCompany = transaction.dao(ExternalCompany.class).get(company.getOldId());

        if (externalCompany != null) {
            // found company, update
            transaction.dao(ExternalCompany.class).update(externalCompany.contactDataFrom(company));
            logger.debug("legacy record updated, our id={}, legacy-id={}", company.getId(), externalCompany.getId());
        } else {
            // no company exists, create it
            externalCompany = createNewExternalCompany(company, transaction);
        }

        return externalCompany;
    }

    private ExternalCompany createNewExternalCompany(Company company, LegacyDAO_Transaction transaction) throws SQLException {
        ExternalCompany externalCompany;
        logger.debug("export company, new record for {}", company);

        externalCompany = new ExternalCompany(company);

        transaction.dao(ExternalCompany.class).insert(externalCompany);

        company.setOldId(externalCompany.getId());

        companyDAO.partialMerge(company, "old_id");

        logger.debug("export new company done, our id={}, legacy-id={}", company.getId(), externalCompany.getId());
        return externalCompany;
    }

    @Override
    public En_ResultStatus exportPerson(Person person) {
        if (personDAO.isEmployee(person)) {
            return legacyDAO.saveExternalEmployee(person, person.getDepartment(), person.getPosition());
        }

        try {
            Company personCompany = person.getCompany();

            return legacyDAO.runAction(transaction -> {
                ExternalCompany externalCompany = transaction.dao(ExternalCompany.class).get(personCompany.getOldId());

                if (externalCompany == null) {
                    // no extrenal company created yet for this person, let's fix it
                    externalCompany = createNewExternalCompany(personCompany, transaction);
                }

                ExternalPerson externalPerson = transaction.dao(ExternalPerson.class).get(person.getOldId());
                if (externalPerson != null) {
                    externalPerson.setCompanyId(externalCompany.getId());
                    externalPerson.updateContactFrom(person);
                    transaction.dao(ExternalPerson.class).update(externalPerson);
                    transaction.commit();
                    return En_ResultStatus.OK;
                }

                externalPerson = new ExternalPerson(person, legacyDAO.getOurHost());
                externalPerson.setCompanyId(externalCompany.getId());
                transaction.dao(ExternalPerson.class).insert(externalPerson);

                person.setOldId(externalPerson.getId());

                personDAO.partialMerge(person, "old_id");

                transaction.commit();

                return En_ResultStatus.OK;
            });
        }
        catch (SQLException e) {
            logger.error("unable to export person", e);
            return En_ResultStatus.DB_COMMON_ERROR;
        }
    }

    @Override
    public En_ResultStatus exportProduct(DevUnit product) {
        if (product.getType() != En_DevUnitType.PRODUCT)
            return En_ResultStatus.INCORRECT_PARAMS;

        try {
            return legacyDAO.runAction(transaction -> {
                ExternalProduct externalProduct = transaction.dao(ExternalProduct.class).get(product.getOldId());
                if (externalProduct != null) {
                    externalProduct.updateFrom(product);
                    transaction.dao(ExternalProduct.class).update(externalProduct);
                }
                else {
                    externalProduct = new ExternalProduct(product);
                    transaction.dao(ExternalProduct.class).insert(externalProduct);

                    product.setOldId(externalProduct.getId());

                    devUnitDAO.partialMerge(product, "old_id");
                }

                transaction.commit();
                return En_ResultStatus.OK;
            });
        }
        catch (SQLException e) {
            logger.error("unable to export product", e);
            return En_ResultStatus.DB_COMMON_ERROR;
        }
    }
}
