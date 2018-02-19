package ru.protei.portal.tools.migrate.export;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CompanyDAO;
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


    @Override
    public En_ResultStatus exportCompany(Company company) {
        try {
            return legacyDAO.runAction(transaction -> {
                _doExportCompany(company, transaction.dao(ExternalCompany.class));
                transaction.commit();
                return En_ResultStatus.OK;
            });
        }
        catch (SQLException e) {
            logger.error("unable to export company", e);
            return En_ResultStatus.DB_COMMON_ERROR;
        }
    }

    private ExternalCompany _doExportCompany(Company company, LegacySystemDAO.LegacyEntityDAO<ExternalCompany> legacyDAO) throws SQLException {
        ExternalCompany externalCompany = legacyDAO.get(company.getOldId());

        if (externalCompany != null) {
            // found company, update
            legacyDAO.update(externalCompany.contactDataFrom(company));
            logger.debug("legacy record updated, our id={}, legacy-id={}", company.getId(), externalCompany.getId());
        } else {
            // no company exists, create it
            logger.debug("export company, new record for {}", company);
            externalCompany = new ExternalCompany(company);
            legacyDAO.insert(externalCompany);
            company.setOldId(externalCompany.getId());
            companyDAO.partialMerge(company, "old_id");
            logger.debug("export new company done, our id={}, legacy-id={}", company.getId(), externalCompany.getId());
        }

        return externalCompany;
    }

    @Override
    public En_ResultStatus exportPerson(Person person) {
        if (personDAO.isEmployee(person))
            return En_ResultStatus.INCORRECT_PARAMS;

        try {
            Company personCompany = companyDAO.get(person.getCompanyId());

            return legacyDAO.runAction(transaction -> {
                ExternalCompany company = transaction.dao(ExternalCompany.class).get(personCompany.getOldId());

                if (company == null) {
                    // no company created yet for this person, let's fix it
                    company = new ExternalCompany(companyDAO.get(person.getCompanyId()));
                    transaction.dao(ExternalCompany.class).insert(company);
                }

                ExternalPerson externalPerson = transaction.dao(ExternalPerson.class).findExportEntry(person.getId());
                if (externalPerson != null) {
                    externalPerson.updateContactFrom(person).setCompanyId(company.getId());
                    transaction.dao(ExternalPerson.class).update(externalPerson);
                    transaction.commit();
                    return En_ResultStatus.OK;
                }

                externalPerson = new ExternalPerson(person, legacyDAO.getOurHost());
                externalPerson.setCompanyId(company.getId());
                transaction.dao(ExternalPerson.class).insert(externalPerson);
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
                ExternalProduct externalProduct = transaction.dao(ExternalProduct.class).findExportEntry(product.getId());
                if (externalProduct != null) {
                    externalProduct.updateFrom(product);
                    transaction.dao(ExternalProduct.class).update(externalProduct);
                }
                else {
                    externalProduct = new ExternalProduct(product);
                    transaction.dao(ExternalProduct.class).insert(externalProduct);
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
