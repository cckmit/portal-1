package ru.protei.portal.tools.migrate.parts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import ru.protei.portal.core.model.dao.DevUnitBranchDAO;
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dao.DevUnitVersionDAO;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.tools.migrate.tools.MigrateAction;
import ru.protei.portal.tools.migrate.tools.MigrateUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by michael on 23.05.16.
 */
public class MigrateDevUnits implements MigrateAction {

    @Autowired
    private DevUnitDAO unitDAO;

    @Autowired
    private DevUnitBranchDAO branchDAO;

    @Autowired
    private DevUnitVersionDAO versionDAO;



    @Override
    public void migrate(Connection src, AbstractApplicationContext ctx) throws SQLException {

        List<DevUnit> stlist = new ArrayList<>();

        for (Map<String,Object> row : MigrateUtils.buildListForTable(src,"\"Resource\".Tm_Project", "strName")) {

            DevUnit u = new DevUnit(En_DevUnitType.COMPONENT.getId(), (String)row.get("strName"),(String)row.get("strInfo"));
            u.setCreated((Date)row.get("dtCreation"));
            u.setCreatorId(MigrateUtils.DEFAULT_CREATOR_ID);
            u.setLastUpdate(new Date());
            u.setStateId(En_DevUnitState.ACTIVE.getId());
            u.setOldId((Long)row.get("nID"));
            stlist.add(u);
        }

        unitDAO.persistBatch(stlist);

        System.out.println("projects converted");


        // products
        stlist.clear();
        for (Map<String,Object> row : MigrateUtils.buildListForTable(src,"\"Resource\".Tm_Product", "nID")) {
            DevUnit u = new DevUnit(En_DevUnitType.PRODUCT.getId(), (String)row.get("strValue"),(String)row.get("strInfo"));
            u.setCreated((Date)row.get("dtCreation"));
            u.setCreatorId(MigrateUtils.DEFAULT_CREATOR_ID);
            u.setLastUpdate(new Date());
            u.setStateId(En_DevUnitState.ACTIVE.getId());
            u.setOldId((Long)row.get("nID"));
            stlist.add(u);
        }

        unitDAO.persistBatch(stlist);

        System.out.println("products converted");
    }

    @Override
    public int orderOfExec() {
        return 1;
    }
}
