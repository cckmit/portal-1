package ru.protei.portal.tools.migrate.parts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import ru.protei.portal.core.model.dao.DevUnitBranchDAO;
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dao.DevUnitVersionDAO;
import ru.protei.portal.core.model.dao.MigrationEntryDAO;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.tools.migrate.tools.MigrateAction;
import ru.protei.portal.tools.migrate.tools.MigrateUtils;

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

    @Autowired
    private DevUnitBranchDAO branchDAO;

    @Autowired
    private DevUnitVersionDAO versionDAO;

    @Autowired
    private MigrationEntryDAO migrateDAO;



    @Override
    public void migrate(Connection src, AbstractApplicationContext ctx) throws SQLException {

//        List<DevUnit> stlist = new ArrayList<>();

       long lastOldDateUpdate = migrateDAO.getMigratedLastUpdate(TM_PROJECT_ITEM_CODE, 0L);
       migrateDAO.confirmMigratedLastUpdate(TM_PROJECT_ITEM_CODE, new Date().getTime());


        new BatchProcessTask<DevUnit>("\"Resource\".Tm_Project", "dtLastUpdate", lastOldDateUpdate)
                .withIdFieldName("nID")
                .setLastId(migrateDAO.getMigratedLastId(TM_PROJECT_ITEM_CODE, 0L))
                .setLastUpdate(lastOldDateUpdate)

                .onBatchEnd(lastIdValue -> migrateDAO.confirmMigratedLastId(TM_PROJECT_ITEM_CODE, lastIdValue))
                .process(src, unitDAO, row -> {
                   DevUnit u = new DevUnit(En_DevUnitType.COMPONENT.getId(), (String) row.get("strName"), (String) row.get("strInfo"));
                   u.setCreated((Date) row.get("dtCreation"));
                   u.setCreatorId(MigrateUtils.DEFAULT_CREATOR_ID);
                   u.setLastUpdate(new Date());
                   u.setStateId(En_DevUnitState.ACTIVE.getId());
                   u.setOldId((Long) row.get("nID"));
                   return u;
                }).dumpStats(TM_PROJECT_ITEM_CODE);


/*
        for (Map<String,Object> row : MigrateUtils.buildListForTable(src,"\"Resource\".Tm_Project", "strName")) {

            if (unitDAO.getByCondition("UTYPE_ID=? and UNIT_NAME=?", En_DevUnitType.COMPONENT.getId(),row.get("strName")) != null)
                continue;

            DevUnit u = new DevUnit(En_DevUnitType.COMPONENT.getId(), (String)row.get("strName"),(String)row.get("strInfo"));
            u.setCreated((Date)row.get("dtCreation"));
            u.setCreatorId(MigrateUtils.DEFAULT_CREATOR_ID);
            u.setLastUpdate(new Date());
            u.setStateId(En_DevUnitState.ACTIVE.getId());
            u.setOldId((Long)row.get("nID"));
            stlist.add(u);
        }

        unitDAO.persistBatch(stlist);
*/
     //   System.out.println("projects converted");


        // products


       lastOldDateUpdate = migrateDAO.getMigratedLastUpdate(TM_PRODUCT_ITEM_CODE, 0L);
       migrateDAO.confirmMigratedLastUpdate(TM_PRODUCT_ITEM_CODE, new Date().getTime());

        new BatchProcessTask<DevUnit>("\"Resource\".Tm_Product", "dtLastUpdate", lastOldDateUpdate)
                .withIdFieldName("nID")
                .setLastId(migrateDAO.getMigratedLastId(TM_PRODUCT_ITEM_CODE, 0L))
                .setLastUpdate(lastOldDateUpdate)

                .onBatchEnd(lastIdValue -> migrateDAO.confirmMigratedLastId(TM_PRODUCT_ITEM_CODE, lastIdValue))
                .process(src, unitDAO, row -> {
                   DevUnit u = new DevUnit(En_DevUnitType.PRODUCT.getId(), (String) row.get("strValue"), (String) row.get("strInfo"));
                   u.setCreated((Date) row.get("dtCreation"));
                   u.setCreatorId(MigrateUtils.DEFAULT_CREATOR_ID);
                   u.setLastUpdate(new Date());
                   u.setStateId(En_DevUnitState.ACTIVE.getId());
                   u.setOldId((Long) row.get("nID"));
                   return u;
                })
                .dumpStats(TM_PRODUCT_ITEM_CODE);

        /*
//        stlist.clear();

        for (Map<String,Object> row : MigrateUtils.buildListForTable(src,"\"Resource\".Tm_Product", "nID")) {

            if (unitDAO.getByCondition("UTYPE_ID=? and UNIT_NAME=?", En_DevUnitType.PRODUCT.getId(),row.get("strValue")) != null)
                continue;

            DevUnit u = new DevUnit(En_DevUnitType.PRODUCT.getId(), (String)row.get("strValue"),(String)row.get("strInfo"));
            u.setCreated((Date)row.get("dtCreation"));
            u.setCreatorId(MigrateUtils.DEFAULT_CREATOR_ID);
            u.setLastUpdate(new Date());
            u.setStateId(En_DevUnitState.ACTIVE.getId());
            u.setOldId((Long)row.get("nID"));
            stlist.add(u);
        }

        unitDAO.persistBatch(stlist);
*/

//        System.out.println("products converted");
    }

    @Override
    public int orderOfExec() {
        return 1;
    }
}
