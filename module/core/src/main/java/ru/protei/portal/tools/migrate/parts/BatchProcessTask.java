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

    private long records_handled = 0L;

    public BatchProcessTask (String tableName, String idFieldName, Long startFromId) {
        this ("select * from " + tableName + " where " + idFieldName + " > " + MigrateUtils.nvl(startFromId,0L) + " order by " + idFieldName);
        withIdField(idFieldName);
    }

    public BatchProcessTask (String query) {
        this.query = query;
    }


    public BatchProcessTask<T> withIdField (String idFieldName) {
        this.idFieldName = idFieldName;
        return this;
    }

    public BatchProcessTask<T> onBatchEnd (EndOfBatch cb) {
        this._onBatchEnd = cb;
        return this;
    }

    public BatchProcessTask<T> process (Connection conn, JdbcDAO<Long, T> dao, MigrateAdapter<T> adapter) throws SQLException {
        ResultSet rs = null;

        System.out.println("running query : " + query);

        List<T> batchSet = new ArrayList<>(batchSize);

        long handled = 0;
        try (Statement st = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY)) {

            st.setFetchSize(fetchSize);

            rs = st.executeQuery(query);

            System.out.println("loop over result-set::begin");

            while (rs.next()) {
                handled++;
                records_handled++;

                if (this.idFieldName != null) {
                    lastIdValue = rs.getLong(this.idFieldName);
                }

                batchSet.add(adapter.createEntity(Tm_SqlHelper.fetchRowAsMap(rs)));

                if (batchSet.size() >= batchSize) {
                    processBatch(dao, batchSet);
                    System.out.println("Handled rows : " + handled);
                }
            }

            if (!batchSet.isEmpty()) {
                processBatch(dao, batchSet);
                System.out.println("Handled rows : " + handled);
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

    private void processBatch(JdbcDAO<Long, T> dao, List<T> batchSet) {
        dao.persistBatch(batchSet);
        batchSet.clear();
        if (_onBatchEnd != null) {
            _onBatchEnd.onBatchEnd(this.lastIdValue);
        }
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
