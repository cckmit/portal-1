package ru.protei.portal.tools.migrate.parts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.tools.migrate.utils.MigrateAction;
import ru.protei.portal.tools.migrate.utils.MigrateUtils;
import ru.protei.winter.jdbc.JdbcDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by michael on 01.04.16.
 */
public class MigrateCrmSessions implements MigrateAction {


    public static final String CRM_SESSION_MIGRATION_ID = "CRM.Session";
    public static final String CRM_SESSION_COMMENT_MIGRATION_ID = "CRM.SessionComment";

    private static Logger logger = LoggerFactory.getLogger(MigrateCrmSessions.class);

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Autowired
    CaseCommentDAO caseCommentDAO;

    @Autowired
    private CaseStateMatrixDAO stateMatrixDAO;

    @Autowired
    private DevUnitDAO devUnitDAO;


    @Autowired
    private MigrationEntryDAO migrateDAO;


    @Override
    public int orderOfExec() {
        return 10;
    }


    private Map<Long, Long> getSession2ContactMap (Connection conn, Long minId, Long maxId) {

        try (PreparedStatement p = conn.prepareStatement(
                "select nSessionID, nContactID from CRM.Tm_ContactToSession where nSessionID between ? and ?"
        )) {
            p.setLong(1, minId);
            p.setLong(2, maxId);

            ResultSet rs = p.executeQuery();

            Map<Long,Long> result = new HashMap<>();

            while (rs.next()) {
                result.putIfAbsent(rs.getLong("nSessionID"), rs.getLong("nContactID"));
            }

            rs.close();

            return result;
        }
        catch (SQLException e) {
            logger.debug("",e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void migrate(Connection sourceConnection) throws SQLException {

        final Map<Long, Long> supportStatusMap = stateMatrixDAO.getOldToNewStateMap(En_CaseType.CRM_SUPPORT);
        final Map<Long, Long> marketStatusMap = stateMatrixDAO.getOldToNewStateMap(En_CaseType.CRM_MARKET);

        final Map<Long, Long> productIdMap = new HashMap<>();



        devUnitDAO.listByQuery (new ProductQuery("", En_SortField.id, En_SortDir.ASC))
                .forEach(record -> productIdMap.put(record.getOldId(),record.getId()));


        BaseBatchProcess<CaseObject> sessionsBatchProcess = new BaseBatchProcess<CaseObject>() {
            @Override
            protected void processInsert(JdbcDAO<Long, CaseObject> dao, List<CaseObject> entries) {
                Long minId = entries.stream().mapToLong(item -> item.getCaseNumber()).min().getAsLong();
                Long maxId = entries.stream().mapToLong(item -> item.getCaseNumber()).max().getAsLong();

                logger.debug("handle crm-session contacts, min-id : {}, max-id : {}", minId, maxId);

                Map<Long,Long> session2contact = getSession2ContactMap(sourceConnection, minId, maxId);

                logger.debug("got session2contact map, size : {}", session2contact.size());

                entries.forEach(item -> {
                    if (item.getCreatorId() == null) {
                        item.setCreatorId((Long) MigrateUtils.nvl(session2contact.get(item.getCaseNumber()), item.getManagerId(), MigrateUtils.DEFAULT_CREATOR_ID));
                    }

                    if (item.getInitiatorId() == null) {
                        item.setInitiatorId((Long) MigrateUtils.nvl(session2contact.get(item.getCaseNumber()), item.getManagerId(), item.getCreatorId()));
                    }
                });

                logger.debug("crm-sessions, post-process done");

                super.processInsert(dao, entries);
            }
        };

        MigrateUtils.runDefaultMigration (sourceConnection, CRM_SESSION_MIGRATION_ID, "CRM.tm_session",
                migrateDAO, caseObjectDAO, sessionsBatchProcess,
                row -> {
                    CaseObject obj = new CaseObject();
                    obj.setId(null);
                    obj.setCreated((Date) row.get("dtCreation"));
                    obj.setCaseNumber((Long) row.get("nID"));
                    obj.setInitiatorCompanyId((Long)row.get("nCompanyID"));

                    Long oldProdID = (Long)row.get("nProductId");

                    obj.setProductId(oldProdID == null ? null : productIdMap.get(oldProdID));

                    logger.debug("import crm-session, id = {}, product = {}", obj.getCaseNumber(), obj.getProductId());

//                    obj.setCreatorId((Long) row.get("nCreatorID"));
//                    obj.setCreatorIp((String)row.get("strClientIp"));
//                    obj.setCreatorInfo((String)row.get("str"));
                    obj.setEmails((String) row.get("strRecipients"));

                    obj.setDeleted(row.get("lDeleted") != null && row.get("lDeleted").toString().equals("1"));
                    obj.setPrivateCase(row.get("lPrivate") != null && ((Number)row.get("lPrivate")).intValue() != 0);

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

                    return obj;
                });


        /**
         *
         * в принципе, можно сделать объединение в одну мапу, потому что в старом CRM эти записи в одной таблице и не пересекаются по ID
         */
        Map<Long,Long> crmSupportIdMap = caseObjectDAO.getNumberToIdMap(En_CaseType.CRM_SUPPORT);
        Map<Long,Long> crmMarketIdMap = caseObjectDAO.getNumberToIdMap(En_CaseType.CRM_MARKET);


        new BatchInsertTask(migrateDAO, CRM_SESSION_COMMENT_MIGRATION_ID)
                .forQuery("select com.*, s.nCategoryID from CRM.Tm_SessionComment com join CRM.Tm_Session s on (s.nID=com.nSessionID) where com.nID > ? order by com.nID", "nID", "dtLastUpdate")
                .process(sourceConnection, caseCommentDAO, new BaseBatchProcess<>(), from -> {
                    CaseComment comment = new CaseComment();

                    if (((Number)MigrateUtils.nvl(from.get("nCategoryID"), 8)).intValue() == 8) {
                        comment.setCaseId(crmSupportIdMap.get(from.get("nSessionID")));
                        comment.setCaseStateId(supportStatusMap.get(from.get("nStatusID")));
                    }
                    else {
                        comment.setCaseId(crmMarketIdMap.get(from.get("nSessionID")));
                        comment.setCaseStateId(marketStatusMap.get(from.get("nStatusID")));
                    }


                    if (comment.getCaseId() == null) {
                        logger.debug("case object not found, crm.session.id={}, comment.id = {}", from.get("nSessionID"), from.get("nID"));

                        /**
                         * we need to stop this migration batch
                         */
                        return null;
                    }

                    comment.setCreated((Date)from.get("dtCreation"));
                    comment.setClientIp((String)from.get("strClientIP"));
                    comment.setAuthorId((Long)from.get("nCreatorID"));
                    comment.setText((String)from.get("strComment"));
                    comment.setOldId((Long) from.get("nID"));

                    return comment;
                })
                .dumpStats();

    }
}
