package ru.protei.portal.tools.migrate.parts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.MigrationEntryDAO;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.tools.migrate.tools.MigrateAction;

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
    private MigrationEntryDAO migrateDAO;

    @Override
    public int orderOfExec() {
        return 0;
    }

    @Override
    public void migrate(Connection src, AbstractApplicationContext ctx) throws SQLException {

        if (dao.get(-1L) == null) {
            Company no_comp_rec = new Company();
            no_comp_rec.setCreated(new Date());
            no_comp_rec.setCname("no_company");
            no_comp_rec.setId(-1L);
            dao.persist(no_comp_rec);
        }

        long lastOldDateUpdate = migrateDAO.getMigratedLastUpdate(TM_COMPANY_ITEM_CODE, 0L);
        migrateDAO.confirmMigratedLastUpdate(TM_COMPANY_ITEM_CODE, new Date().getTime());

        new BatchProcessTask<Company>(
                "\"resource\".tm_company", "dtLastUpdate", lastOldDateUpdate
        )
                .setLastUpdate(lastOldDateUpdate)
                .process(src, dao, row -> {
                    Company x = new Company();
                    x.setAddressDejure((String) row.get("strDeJureAddress"));
                    x.setAddressFact((String) row.get("strPhysicalAddress"));
                    x.setEmail((String) row.get("strE_mail"));
                    x.setFax(null);
                    x.setId(((Number) row.get("nID")).longValue());
                    x.setInfo((String) row.get("strInfo"));
                    x.setCname((String) row.get("strName"));
                    x.setPhone(null);
                    x.setCreated((Date) row.get("dtCreation"));
                    x.setWebsite((String) row.get("strHTTP_url"));
                    return x;
                })
                .dumpStats(TM_COMPANY_ITEM_CODE);

    }
}
