package ru.protei.portal.tools.migrate.parts;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.tools.migrate.tools.MigrateAction;
import ru.protei.portal.tools.migrate.tools.MigrateUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

/**
 * Created by michael on 01.04.16.
 */
public class MigrateCrmSessions implements MigrateAction {


    @Autowired
    CaseObjectDAO dao;

    @Autowired
    private CaseStateMatrixDAO stateMatrixDAO;


    @Autowired
    private MigrationEntryDAO migrateDAO;


    @Override
    public int orderOfExec() {
        return 10;
    }

    @Override
    public void migrate(Connection sourceConnection) throws SQLException {

        final Map<Long, Long> supportStatusMap = stateMatrixDAO.getOldToNewStateMap(En_CaseType.CRM_SUPPORT);
        final Map<Long, Long> marketStatusMap = stateMatrixDAO.getOldToNewStateMap(En_CaseType.CRM_MARKET);


        MigrateUtils.runDefaultMigration (sourceConnection, "CRM.Session", "CRM.tm_session",
                migrateDAO, dao,
                row -> {
                    CaseObject obj = new CaseObject();
                    obj.setId(null);
                    obj.setCreated((Date) row.get("dtCreation"));
                    obj.setCaseNumber((Long) row.get("nID"));
                    obj.setInitiatorCompanyId((Long)row.get("nCompanyID"));
//                    obj.setCreatorId((Long) row.get("nCreatorID"));
//                    obj.setCreatorIp((String)row.get("strClientIp"));
//                    obj.setCreatorInfo((String)row.get("str"));
                    obj.setEmails((String) row.get("strRecipients"));


                    obj.setImpLevel(((Number)MigrateUtils.nvl(row.get("nCriticalityId"),3)).intValue());
                    obj.setInfo((String) row.get("strDescription"));
//                    obj.setInitiatorId((Long) row.get("nDeclarantId"));
//                    obj.setKeywords((String)row.get("strKeyWord"));
//                    obj.setLocal(row.get("lIsLocal") == null ? 1 : ((Number) row.get("lIsLocal")).intValue());
                    obj.setName("CRM-" + obj.getCaseNumber());
                    obj.setManagerId((Long) row.get("nManagerId"));
                    obj.setModified((Date)MigrateUtils.nvl(row.get("dtLastUpdate"), new Date ()));

                    /**
                     * @TODO Мы создаем свой "внешний ID", а у сессий crm он также есть и видимо используется
                     * для интеграции со сторонними системами.
                     * Это вопрос к модели данных старого и нового портала
                     */
                    // (String)row.get("strExtID")

                    if (((Number)MigrateUtils.nvl(row.get("nCategoryID"), 8)).intValue() == 8) {
                        obj.setExtId(En_CaseType.CRM_SUPPORT.makeGUID(obj.getCaseNumber()));
                        obj.setTypeId(En_CaseType.CRM_SUPPORT.getId());
                        obj.setStateId(supportStatusMap.get(row.get("nStatusID")));
                    }
                    else {
                        obj.setExtId(En_CaseType.CRM_MARKET.makeGUID(obj.getCaseNumber()));
                        obj.setTypeId(En_CaseType.CRM_MARKET.getId());
                        obj.setStateId(marketStatusMap.get(row.get("nStatusID")));
                    }


                    if (obj.getCreatorId() == null) {
                        obj.setCreatorId((Long) MigrateUtils.nvl(obj.getManagerId(), MigrateUtils.DEFAULT_CREATOR_ID));
                    }

                    if (obj.getInitiatorId() == null) {
                        obj.setInitiatorId((Long) MigrateUtils.nvl(obj.getManagerId(), obj.getCreatorId()));
                    }

                    return obj;
                });

    }
}
