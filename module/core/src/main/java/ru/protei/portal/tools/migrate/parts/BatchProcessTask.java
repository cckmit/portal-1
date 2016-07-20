package ru.protei.portal.tools.migrate.parts;

import protei.sql.Tm_SqlHelper;
import ru.protei.portal.core.model.dao.PersonAbsenceDAO;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.tools.migrate.tools.EndOfBatch;
import ru.protei.portal.tools.migrate.tools.MigrateAdapter;
import ru.protei.portal.tools.migrate.tools.MigrateUtils;
import ru.protei.winter.jdbc.JdbcDAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 20.05.16.
 */
public class BatchProcessTask<T> {

    String query;

    int fetchSize = 1000;
    int batchSize = 1000;

    String idFieldName;

    Long lastIdValue;

    EndOfBatch _onBatchEnd;

    long lastUpdate;
    private long records_handled = 0L;

    public BatchProcessTask (String tableName, String idFieldName, Long lastUpdate) {
        this ("select * from " + tableName + " where " + idFieldName + " > " + "DATEADD(ss, "+ lastUpdate/1000 +",'1/1/1970') order by nID"); // withIdFieldName вызывается после конструктора, поэтому nID прописываем явно
    }

    public BatchProcessTask (String query) {
        this.query = query;
    }
    public BatchProcessTask<T> onBatchEnd (EndOfBatch cb) {
        this._onBatchEnd = cb;
        return this;
    }


    public BatchProcessTask<T> withIdFieldName (String idFieldName) {
        this.idFieldName = idFieldName;
        return this;
    }


    public BatchProcessTask setLastId(long lastId){
        this.lastIdValue = lastId;
        return this;
    }
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
            long maxId = lastIdValue;

            while (rs.next()) {
                records_handled++;

                if (this.idFieldName != null && rs.getLong(this.idFieldName) > lastIdValue) {
                    maxId = rs.getLong(this.idFieldName);

                    //insert
                    handledWithInserting++;
                    insertBatchSet.add(adapter.createEntity(Tm_SqlHelper.fetchRowAsMap(rs)));
                }else{
                    //update
                    handledWithUpdating++;
                    updateBatchSet.add(adapter.createEntity(Tm_SqlHelper.fetchRowAsMap(rs)));

                }


                if (insertBatchSet.size() >= batchSize) {
                    processBatch(dao, insertBatchSet, true);
                    System.out.println("Handled insert rows : " + handledWithInserting);
                }
                if (updateBatchSet.size() >= batchSize) {
                    processBatch(dao, updateBatchSet, false);
                    System.out.println("Handled update rows : " + handledWithUpdating);
                }
            }

            // остатки
            if (!insertBatchSet.isEmpty()) {
                processBatch(dao, insertBatchSet, true);
                System.out.println("Handled inset rows : " + handledWithInserting);
            }
            if (!updateBatchSet.isEmpty()) {
                processBatch(dao, updateBatchSet, false);
                System.out.println("Handled update rows : " + handledWithUpdating);
            }

            if (_onBatchEnd != null) {
                _onBatchEnd.onBatchEnd(maxId);
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
        else{
            if(dao instanceof PersonAbsenceDAO)
                for(T set: batchSet){
                    dao.mergeByCondition(set, "old_id=?", ((PersonAbsence)set).getOldId());
                }
            else
                dao.mergeBatch(batchSet);
        }

        batchSet.clear();
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
