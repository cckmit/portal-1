package ru.protei.portal.tools.migrate.parts;

import org.apache.log4j.Logger;
import protei.sql.Tm_SqlHelper;
import protei.sql.query.Tm_BaseQueryCmd;
import protei.sql.utils.Tm_QueryExecutor;
import ru.protei.portal.core.model.dao.MigrationEntryDAO;
import ru.protei.portal.core.model.ent.MigrationEntry;
import ru.protei.portal.tools.migrate.tools.BatchProcess;
import ru.protei.portal.tools.migrate.tools.MigrateAdapter;
import ru.protei.winter.jdbc.JdbcDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 20.05.16.
 */
public class BatchProcessTaskExt {

//    String query;

    private static Logger logger = Logger.getLogger(BatchProcessTaskExt.class);

    int fetchSize = 1000;
    int batchSize = 1000;

    String idFieldName;
    String lastUpdateFieldName;

//    Long lastIdValue;

    String stateEntryId;

    long records_handled = 0L;

    private Tm_BaseQueryCmd queryCmd;

    private MigrationEntryDAO migrationDAO;


    public BatchProcessTaskExt (MigrationEntryDAO migrationDAO, String entryId) {
        this.queryCmd = new Tm_BaseQueryCmd();
        this.migrationDAO = migrationDAO;
        this.withStateEntry(entryId);
    }

    public BatchProcessTaskExt withStateEntry (String id) {
        this.stateEntryId = id;
        return this;
    }

    public BatchProcessTaskExt forTable (String tableName) {
        return forTable (tableName, "nID", "dtLastUpdate");
    }

    public BatchProcessTaskExt forTable (String tableName, String idFieldName, String lastUpdateFieldName) {
        queryCmd.setCommad("select * from " + tableName + " where " + lastUpdateFieldName + " > ? order by " + idFieldName);
        this.idFieldName = idFieldName;
        this.lastUpdateFieldName = lastUpdateFieldName;
        return this;
    }

    public BatchProcessTaskExt forQuery (String query, String idFieldName, String lastUpdateFieldName) {
        //this.queryCmd.setCommad(query + " where " + lastUpdateFieldName + " > ? order by " + idFieldName);

        // assume it's well-formed query
        this.queryCmd.setCommad(query);
        this.idFieldName = idFieldName;
        this.lastUpdateFieldName = lastUpdateFieldName;
        return this;
    }


    public <T> BatchProcessTaskExt process (Connection conn, JdbcDAO<Long, T> dao, BatchProcess<T> batchProcess, MigrateAdapter<T> adapter) throws SQLException {
        ResultSet rs = null;

        logger.debug("running query : " + queryCmd.getCommand());

        List<T> insertBatchSet = new ArrayList<>(batchSize);
        List<T> updateBatchSet = new ArrayList<>(batchSize);

        long handledWithInserting = 0;
        long handledWithUpdating = 0;
        records_handled = 0;

        MigrationEntry migrationEntry = migrationDAO.getOrCreateEntry(this.stateEntryId);
//        long maxId = migrationEntry.getLastId();

        queryCmd.addParam(migrationEntry.getLastUpdate());

        try (PreparedStatement st = conn.prepareStatement(queryCmd.getCommand(), ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY)) {

            Tm_QueryExecutor.setupStatement(st, queryCmd);
            st.setFetchSize(fetchSize);
            rs = st.executeQuery();

            logger.debug("loop over result-set::begin");

            while (rs.next()) {
                records_handled++;

                if (rs.getLong(this.idFieldName) > migrationEntry.getLastId()) {
                    migrationEntry.setLastId(rs.getLong(this.idFieldName));

                    //insert
                    handledWithInserting++;
                    insertBatchSet.add(adapter.createEntity(Tm_SqlHelper.fetchRowAsMap(rs)));
                }else{
                    //update
                    handledWithUpdating++;
                    updateBatchSet.add(adapter.createEntity(Tm_SqlHelper.fetchRowAsMap(rs)));
                }


                if (insertBatchSet.size() >= batchSize) {
                    //processBatch(dao, insertBatchSet, true);
                    batchProcess.doInsert(dao, insertBatchSet);
                    migrationDAO.merge(migrationEntry);
                    logger.debug("Handled insert rows : " + handledWithInserting);
                }
                if (updateBatchSet.size() >= batchSize) {
                    batchProcess.doUpdate(dao, updateBatchSet);
                    migrationDAO.merge(migrationEntry);
                    logger.debug("Handled update rows : " + handledWithUpdating);
                }
            }

            // остатки
            if (!insertBatchSet.isEmpty()) {
                batchProcess.doInsert(dao, insertBatchSet);
                logger.debug("Handled inset rows : " + handledWithInserting);
            }
            if (!updateBatchSet.isEmpty()) {
                batchProcess.doUpdate(dao, updateBatchSet);
                logger.debug("Handled update rows : " + handledWithUpdating);
            }

            migrationDAO.merge(migrationEntry);

            logger.debug("loop over result-set::end");
        }
        finally {
            Tm_SqlHelper.safeCloseResultSet(rs);
        }

        return this;
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
