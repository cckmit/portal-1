package ru.protei.portal.tools.migrate.parts;

import ru.protei.portal.tools.migrate.utils.BatchProcess;
import ru.protei.winter.jdbc.JdbcDAO;

import java.util.List;

/**
 * Created by michael on 26.07.16.
 */
public class BaseBatchProcess<T> implements BatchProcess<T> {



    public void afterInsert(List<T> insertedEntries) {
    }


    public void afterUpdate(List<T> updatedEntries) {
    }


    protected void processInsert (JdbcDAO<Long, T> dao, List<T> entries) {
        dao.persistBatch(entries);
    }

    protected void processUpdate (JdbcDAO<Long, T> dao, List<T> entries) {
        dao.mergeBatch(entries);
    }

    @Override
    public final void doInsert(JdbcDAO<Long, T> dao, List<T> entries) {
        if (entries != null && !entries.isEmpty()) {
            processInsert(dao, entries);
            afterInsert(entries);
        }
    }

    @Override
    public final void doUpdate(JdbcDAO<Long, T> dao, List<T> entries) {
        if (entries != null && !entries.isEmpty()) {
            processUpdate(dao, entries);
            afterUpdate(entries);
        }
    }
}
