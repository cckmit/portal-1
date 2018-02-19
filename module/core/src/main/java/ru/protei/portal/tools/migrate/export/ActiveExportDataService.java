package ru.protei.portal.tools.migrate.export;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.tools.migrate.LegacySystemDAO;
import ru.protei.portal.tools.migrate.struct.ExternalCompany;
import ru.protei.portal.tools.migrate.struct.ExternalPerson;
import ru.protei.portal.tools.migrate.struct.ExternalProduct;

import java.sql.SQLException;

public class ActiveExportDataService implements ExportDataService {

    private static Logger logger = LoggerFactory.getLogger(ActiveExportDataService.class);

    @Autowired
    LegacySystemDAO legacyDAO;

    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    PersonDAO personDAO;

    @Autowired
    DevUnitDAO devUnitDAO;

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

    private ExternalCompany _doExportCompany(Company company, LegacySystemDAO.LegacyDAO_Transaction transaction) throws SQLException {
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

    private ExternalCompany createNewExternalCompany(Company company, LegacySystemDAO.LegacyDAO_Transaction transaction) throws SQLException {
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
        if (personDAO.isEmployee(person))
            return En_ResultStatus.INCORRECT_PARAMS;

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
