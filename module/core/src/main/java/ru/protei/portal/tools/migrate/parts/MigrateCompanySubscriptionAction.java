package ru.protei.portal.tools.migrate.parts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.tools.migrate.tools.MigrateAction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 01.04.16.
 */
public class MigrateCompanySubscriptionAction implements MigrateAction {

    private static Logger logger = LoggerFactory.getLogger(MigrateCompanySubscriptionAction.class);

    public static final String MIGRATE_ITEM_CODE = "Tm_CompanyEmailSubscription";

    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    CompanySubscriptionDAO companySubscriptionDAO;

    @Autowired
    private MigrationEntryDAO migrationEntryDAO;


    @Override
    public int orderOfExec() {
        return 2;
    }

    @Override
    public void migrate(Connection sourceConnection) throws SQLException {

        final Map<Long, Company> companyMap = new HashMap<>();

        companyDAO.getAll().forEach(c -> companyMap.put(c.getId(), c));

        new BatchInsertTask(migrationEntryDAO, MIGRATE_ITEM_CODE)
                .forTable("\"resource\".Tm_Emails", "nID", null)
                .skipEmptyEntity(true)
                .process(sourceConnection, companySubscriptionDAO, new BaseBatchProcess<>(), row -> {

                    String system = (String)row.get("strSystem");

                    if (system == null || !system.equalsIgnoreCase("CRM")) {
                        logger.warn("skip import company-email (is not for CRM), company-id={} / email={}", row.get("nCompanyID"), row.get("strEmail"));
                        return null;
                    }

                    CompanySubscription x = new CompanySubscription();

                    x.setCompanyId((Long)row.get("nCompanyID"));
                    x.setEmail((String)row.get("strEmail"));
                    x.setLangCode((String) row.get("strInfo"));

                    if (x.getCompanyId() == null && x.getEmail().contains("@protei.ru")) {
                        /* @TODO replace it
                        **/
                        x.setCompanyId(1L);
                    }


                    if (x.getCompanyId() == null || !companyMap.containsKey(x.getCompanyId())) {
                        logger.warn("no company with id {} exists, skip email {}", x.getCompanyId(), x.getEmail());
                        return null;
                    }

                    return x;
                })
                .dumpStats();

    }
}
