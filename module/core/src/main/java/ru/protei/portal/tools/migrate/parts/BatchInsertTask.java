package ru.protei.portal.tools.migrate.parts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class BatchInsertTask {

//    String query;

    private static Logger logger = LoggerFactory.getLogger(BatchInsertTask.class);

    int fetchSize = 1000;
    int batchSize = 1000;

    String idFieldName;
    String lastUpdateFieldName;
    String stateEntryId;

    long records_handled = 0L;

    private Tm_BaseQueryCmd queryCmd;

    private MigrationEntryDAO migrationDAO;

    private boolean skipEmptyEntity = false;


    public BatchInsertTask(MigrationEntryDAO migrationDAO, String entryId) {
        this.queryCmd = new Tm_BaseQueryCmd();
        this.migrationDAO = migrationDAO;
        this.withStateEntry(entryId);
    }

    public BatchInsertTask skipEmptyEntity (boolean skip) {
        this.skipEmptyEntity = skip;
        return this;
    }

    public BatchInsertTask withStateEntry (String id) {
        this.stateEntryId = id;
        return this;
    }

    public BatchInsertTask forTable (String tableName) {
        return forTable (tableName, "nID", "dtLastUpdate");
    }

    public BatchInsertTask forTable (String tableName, String idFieldName, String lastUpdateFieldName) {
        queryCmd.setCommad("select * from " + tableName + " where " + idFieldName + " > ? order by " + idFieldName);
        this.idFieldName = idFieldName;
        this.lastUpdateFieldName = lastUpdateFieldName;
        return this;
    }

    public BatchInsertTask forQuery (String query, String idFieldName, String lastUpdateFieldName) {
        // assume it's well-formed query
        this.queryCmd.setCommad(query);
        this.idFieldName = idFieldName;
        this.lastUpdateFieldName = lastUpdateFieldName;
        return this;
    }


    public <T> BatchInsertTask process (Connection conn, JdbcDAO<Long, T> dao, BatchProcess<T> batchProcess, MigrateAdapter<T> adapter) throws SQLException {
        ResultSet rs = null;

        logger.debug("Insert task for " + stateEntryId + ", running query : " + queryCmd.getCommand());

        List<T> insertBatchSet = new ArrayList<>(batchSize);

        records_handled = 0;

        MigrationEntry migrationEntry = migrationDAO.getOrCreateEntry(En_MigrationEntry.find(this.stateEntryId));

        queryCmd.addParam(migrationEntry.getLastId());

        logger.debug("insert task for entry {}, last-id={}", stateEntryId, migrationEntry.getLastId());

        try (PreparedStatement st = conn.prepareStatement(queryCmd.getCommand(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY)) {

            Tm_QueryExecutor.setupStatement(st, queryCmd);
            st.setFetchSize(fetchSize);
            rs = st.executeQuery();

            logger.debug("loop over result-set::begin");

//            Long lastId = 0L;

            while (rs.next()) {
                records_handled++;
                T object = adapter.createEntity(Tm_SqlHelper.fetchRowAsMap(rs));
                /**
                 * logic reports that we have to stop or skip
                 */
                if (object == null) {
                    if (skipEmptyEntity)
                        continue;
                    else
                        break;
                }
                /**
                 * records sorted by ID, so just store the last one
                 */
                migrationEntry.setLastId(rs.getLong(this.idFieldName));

                insertBatchSet.add(object);

                Timestamp ts = lastUpdateFieldName == null ? null : rs.getTimestamp(lastUpdateFieldName);
                if (ts != null) {
                   java.util.Date lastUpdate = Tm_SqlHelper.timestampToDate(ts);
                   /** update only when it is younger **/
                   if (lastUpdate.getTime() > migrationEntry.getLastUpdate().getTime())
                        migrationEntry.setLastUpdate(lastUpdate);
                }

                if (insertBatchSet.size() >= batchSize) {
                    executeBatch(dao, batchProcess, insertBatchSet, migrationEntry);
                    logger.debug("Rows inserted: " + records_handled);
                }
            }

            // остатки
            if (!insertBatchSet.isEmpty()) {
                executeBatch(dao, batchProcess, insertBatchSet, migrationEntry);
                logger.debug("Rows inserted : " + records_handled);
            }


            logger.debug("loop over result-set::end");
        }
        finally {
            Tm_SqlHelper.safeCloseResultSet(rs);
        }

        return this;
    }

    private <T> void executeBatch(JdbcDAO<Long, T> dao, BatchProcess<T> batchProcess, List<T> insertBatchSet, MigrationEntry migrationEntry) {
        batchProcess.doInsert(dao, insertBatchSet);
        migrationDAO.merge(migrationEntry);
        insertBatchSet.clear();
    }

    public void dumpStats () {
        logger.debug(String.format(this.stateEntryId + ", imported: %d", records_handled));
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
