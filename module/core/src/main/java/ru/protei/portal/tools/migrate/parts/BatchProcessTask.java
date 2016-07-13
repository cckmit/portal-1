package ru.protei.portal.tools.migrate.parts;

import protei.sql.Tm_SqlHelper;
import ru.protei.portal.tools.migrate.tools.EndOfBatch;
import ru.protei.portal.tools.migrate.tools.MigrateAdapter;
import ru.protei.portal.tools.migrate.tools.MigrateUtils;
import ru.protei.winter.jdbc.JdbcDAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by michael on 20.05.16.
 */
public class BatchProcessTask<T> {

    String query;

    int fetchSize = 1000;
    int batchSize = 1000;

    //String idFieldName;

    //Long lastIdValue;

    //EndOfBatch _onBatchEnd;

    long lastUpdate;
    private long records_handled = 0L;

    public BatchProcessTask (String tableName, String idFieldName, Long lastUpdate) {
        this ("select * from " + tableName + " where " + idFieldName + " > " + "DATEADD(ss, "+ lastUpdate/1000 +",'1/1/1970')");
    }

    public BatchProcessTask (String query) {
        this.query = query;
    }


//    public BatchProcessTask<T> withIdField (String idFieldName) {
//        this.idFieldName = idFieldName;
//        return this;
//    }

//    public BatchProcessTask<T> onBatchEnd (EndOfBatch cb) {
//        this._onBatchEnd = cb;
//        return this;
//    }


    public BatchProcessTask setLastUpdate(long lastUpdate){
        this.lastUpdate = lastUpdate;
        return this;
    }

    public BatchProcessTask<T> process (Connection conn, JdbcDAO<Long, T> dao, MigrateAdapter<T> adapter) throws SQLException {
        ResultSet rs = null;

        System.out.println("running query : " + query);

        List<T> insertBatchSet = new ArrayList<>(batchSize);
        List<T> updateBatchSet = new ArrayList<>(batchSize);

        long handledWithInserting = 0;
        long handledWithUpdating = 0;
        try (Statement st = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY)) {

            st.setFetchSize(fetchSize);

            rs = st.executeQuery(query);

            System.out.println("loop over result-set::begin");

            while (rs.next()) {
                records_handled++;

                //if (this.idFieldName != null) {
                //    lastIdValue = rs.getLong(this.idFieldName);
                //}

//                if(rs.getDate("dtCreation").getTime() > lastUpdate){
//                    handledWithInserting++;
//                    insertBatchSet.add(adapter.createEntity(Tm_SqlHelper.fetchRowAsMap(rs)));
//                }else{
                    handledWithUpdating++;
                    updateBatchSet.add(adapter.createEntity(Tm_SqlHelper.fetchRowAsMap(rs)));
//                }

                if (insertBatchSet.size() >= batchSize) {
                    processBatch(dao, insertBatchSet, true);
                    System.out.println("Handled insert rows : " + handledWithInserting);
                }
                if (updateBatchSet.size() >= batchSize) {
                    processBatch(dao, updateBatchSet, false);
                    System.out.println("Handled update rows : " + handledWithUpdating);
                }
            }

            if (!insertBatchSet.isEmpty()) {
                processBatch(dao, insertBatchSet, true);
                System.out.println("Handled inset rows : " + handledWithInserting);
            }

            if (!updateBatchSet.isEmpty()) {
                processBatch(dao, updateBatchSet, false);
                System.out.println("Handled update rows : " + handledWithUpdating);
            }

            System.out.println("loop over result-set::end");
        }
        finally {
            Tm_SqlHelper.safeCloseResultSet(rs);
        }

        return this;
    }

    public void dumpStats (String item) {
        System.out.println(String.format(item + ", imported: %d", records_handled));
    }

    private void processBatch(JdbcDAO<Long, T> dao, List<T> batchSet, boolean isNew) {
        if(isNew)
            dao.persistBatch(batchSet);
        else
            dao.mergeBatch(batchSet);

        batchSet.clear();

//        if (_onBatchEnd != null) {
//            _onBatchEnd.onBatchEnd(this.lastIdValue);
//        }
    }

    public String getQuery() {
        return query;
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
