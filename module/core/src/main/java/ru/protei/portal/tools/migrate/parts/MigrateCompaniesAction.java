package ru.protei.portal.tools.migrate.parts;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.CompanyGroupHomeDAO;
import ru.protei.portal.core.model.dao.MigrationEntryDAO;
import ru.protei.portal.core.model.dict.En_ContactDataAccess;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanyCategory;
import ru.protei.portal.core.model.ent.CompanyHomeGroupItem;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.tools.migrate.tools.MigrateAction;
import ru.protei.portal.tools.migrate.tools.MigrateUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by michael on 01.04.16.
 */
public class MigrateCompaniesAction implements MigrateAction {

    public static final String TM_COMPANY_ITEM_CODE = "Tm_Company";

    @Autowired
    CompanyDAO dao;

    @Autowired
    private CompanyGroupHomeDAO companyGroupHomeDAO;

    @Autowired
    private MigrationEntryDAO migrateDAO;


    @Override
    public int orderOfExec() {
        return 0;
    }

    @Override
    public void migrate(Connection sourceConnection) throws SQLException {

        if (dao.get(-1L) == null) {
            Company no_comp_rec = new Company();
            no_comp_rec.setCreated(new Date());
            no_comp_rec.setCname("no_company");
            no_comp_rec.setId(-1L);
            dao.persist(no_comp_rec);
        }

        MigrateUtils.runDefaultMigration (sourceConnection, TM_COMPANY_ITEM_CODE, "\"resource\".tm_company",
                migrateDAO, dao,
                row -> {
                    Company x = new Company();
                    x.setCategory(new CompanyCategory(1L));
                    x.setId(((Number) row.get("nID")).longValue());
                    x.setInfo((String) row.get("strInfo"));
                    x.setCname((String) row.get("strName"));
                    x.setCreated((Date) row.get("dtCreation"));

                    ContactInfoMigrationFacade infoFacade = new ContactInfoMigrationFacade(x.getContactInfo());

                    infoFacade.addItem (En_ContactItemType.ADDRESS_LEGAL, (String) row.get("strDeJureAddress"));
                    infoFacade.addItem (En_ContactItemType.ADDRESS, (String) row.get("strPhysicalAddress"));
                    infoFacade.addItem (En_ContactItemType.EMAIL, (String)row.get("strE_mail"));
                    infoFacade.addItem (En_ContactItemType.WEB_SITE, (String) row.get("strHTTP_url"));
                    return x;
                });


        /**
         * hardcoded import, I have no idea how to do it better
         */
        if (!companyGroupHomeDAO.checkIfHome(1L)) {
            CompanyHomeGroupItem protei_entry = new CompanyHomeGroupItem();
            protei_entry.setCompanyId(1L);
            protei_entry.setExternalCode("protei");
            companyGroupHomeDAO.persist(protei_entry);
        }
        //company_group_home
    }
}
