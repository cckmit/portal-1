package ru.protei.portal.tools.migrate.parts;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dao.MigrationEntryDAO;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.tools.migrate.utils.MigrateAction;
import ru.protei.portal.tools.migrate.utils.MigrateUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by michael on 23.05.16.
 */
public class MigrateDevUnits implements MigrateAction {

    public static final String TM_PROJECT_ITEM_CODE = "Tm_Project";
    public static final String TM_PRODUCT_ITEM_CODE = "Tm_Product";

    @Autowired
    private DevUnitDAO unitDAO;

//    @Autowired
//    private DevUnitBranchDAO branchDAO;
//
//    @Autowired
//    private DevUnitVersionDAO versionDAO;

    @Autowired
    private MigrationEntryDAO migrateDAO;


    @Override
    public void migrate(Connection sourceConnection) throws SQLException {

        // projects
        MigrateUtils.runDefaultMigration(sourceConnection, TM_PROJECT_ITEM_CODE, "\"Resource\".Tm_Project",
                migrateDAO, unitDAO,
                row -> {
                    DevUnit u = new DevUnit(En_DevUnitType.COMPONENT.getId(), (String) row.get("strName"), (String) row.get("strInfo"));
                    u.setCreated((Date) row.get("dtCreation"));
                    u.setCreatorId(MigrateUtils.DEFAULT_CREATOR_ID);
                    u.setLastUpdate(new Date());
                    u.setStateId(En_DevUnitState.ACTIVE.getId());
                    u.setOldId((Long) row.get("nID"));
                    return u;
                });


        // products
        MigrateUtils.runDefaultMigration(sourceConnection, TM_PRODUCT_ITEM_CODE, "\"Resource\".Tm_Product",
                migrateDAO, unitDAO,
                row -> {
                    DevUnit u = new DevUnit(En_DevUnitType.PRODUCT.getId(), (String) row.get("strValue"), (String) row.get("strInfo"));
                    u.setCreated((Date) row.get("dtCreation"));
                    u.setCreatorId(MigrateUtils.DEFAULT_CREATOR_ID);
                    u.setLastUpdate(new Date());
                    u.setStateId(En_DevUnitState.ACTIVE.getId());
                    u.setOldId((Long) row.get("nID"));
                    return u;
                });

//        System.out.println("products converted");
    }

    @Override
    public int orderOfExec() {
        return 1;
    }
}
