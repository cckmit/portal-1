package ru.protei.portal.tools.migrate.parts;

import org.apache.log4j.Logger;
import protei.sql.Tm_SqlHelper;
import protei.sql.query.Tm_BaseQueryCmd;
import protei.sql.utils.Tm_QueryExecutor;
import ru.protei.portal.core.model.dao.MigrationEntryDAO;
import ru.protei.portal.core.model.dict.En_MigrationEntry;
import ru.protei.portal.core.model.ent.MigrationEntry;
import ru.protei.portal.tools.migrate.utils.BatchProcess;
import ru.protei.portal.tools.migrate.utils.MigrateAdapter;
import ru.protei.winter.jdbc.JdbcDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 20.05.16.
 */
public class BatchUpdateTask {

//    String query;

    private static Logger logger = Logger.getLogger(BatchUpdateTask.class);

    int fetchSize = 1000;
    int batchSize = 1000;

    String idFieldName;
    String lastUpdateFieldName;

//    Long lastIdValue;

    String stateEntryId;

    long records_handled = 0L;

    private Tm_BaseQueryCmd queryCmd;

    private MigrationEntryDAO migrationDAO;


    public BatchUpdateTask(MigrationEntryDAO migrationDAO, String entryId) {
        this.queryCmd = new Tm_BaseQueryCmd();
        this.migrationDAO = migrationDAO;
        this.withStateEntry(entryId);
    }

    public BatchUpdateTask withStateEntry (String id) {
        this.stateEntryId = id;
        return this;
    }

    public BatchUpdateTask forTable (String tableName) {
        return forTable (tableName, "nID", "dtLastUpdate");
    }

    public BatchUpdateTask forTable (String tableName, String idFieldName, String lastUpdateFieldName) {
        queryCmd.setCommad(
                "select * from " + tableName
                + " where " + idFieldName + " <= ? and " + lastUpdateFieldName + " > ? order by " + lastUpdateFieldName
        );
        this.idFieldName = idFieldName;
        this.lastUpdateFieldName = lastUpdateFieldName;
        return this;
    }

    public BatchUpdateTask forQuery (String query, String idFieldName, String lastUpdateFieldName) {
        // assume it's well-formed query, that takes 2 arguments for last_id and last_update values
        this.queryCmd.setCommad(query);
        this.idFieldName = idFieldName;
        this.lastUpdateFieldName = lastUpdateFieldName;
        return this;
    }


    public <T> BatchUpdateTask process (Connection conn, JdbcDAO<Long, T> dao, BatchProcess<T> batchProcess, MigrateAdapter<T> adapter) throws SQLException {
        ResultSet rs = null;

        logger.debug("Update task for + " + stateEntryId + ", running query : " + queryCmd.getCommand());

        List<T> updateBatchSet = new ArrayList<>(batchSize);

        records_handled = 0;

        MigrationEntry migrationEntry = migrationDAO.getOrCreateEntry(En_MigrationEntry.find(this.stateEntryId));

        queryCmd.addParam(migrationEntry.getLastId());
        queryCmd.addParam(migrationEntry.getLastUpdate());

        try (PreparedStatement st = conn.prepareStatement(queryCmd.getCommand(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY)) {

            Tm_QueryExecutor.setupStatement(st, queryCmd);
            st.setFetchSize(fetchSize);
            rs = st.executeQuery();

            logger.debug("loop over result-set::begin");

            while (rs.next()) {
                records_handled++;
                updateBatchSet.add(adapter.createEntity(Tm_SqlHelper.fetchRowAsMap(rs)));

                Timestamp ts = rs.getTimestamp(lastUpdateFieldName);
                if (ts != null) {
                    java.util.Date lastUpdate = Tm_SqlHelper.timestampToDate(ts);
                    /** update only when it is younger **/
                    if (lastUpdate.getTime() > migrationEntry.getLastUpdate().getTime())
                        migrationEntry.setLastUpdate(lastUpdate);
                }

                if (updateBatchSet.size() >= batchSize) {
                    executeBatch(dao, batchProcess, updateBatchSet, migrationEntry);
                    logger.debug("Handled update rows : " + records_handled);
                }
            }

            // остатки
            if (!updateBatchSet.isEmpty()) {
                executeBatch(dao, batchProcess, updateBatchSet, migrationEntry);
                logger.debug("Handled update rows : " + records_handled);
            }


            logger.debug("loop over result-set::end");
        }
        finally {
            Tm_SqlHelper.safeCloseResultSet(rs);
        }

        return this;
    }

    private <T> void executeBatch(JdbcDAO<Long, T> dao, BatchProcess<T> batchProcess, List<T> updateBatchSet, MigrationEntry migrationEntry) {
        batchProcess.doUpdate(dao, updateBatchSet);
        migrationDAO.merge(migrationEntry);
        updateBatchSet.clear();
    }

    public void dumpStats () {
        logger.debug(String.format(this.stateEntryId + ", updated: %d", records_handled));
    }

    public String getQuery() {
        return queryCmd.getCommand();
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }
}
