package ru.protei.portal.tools.migrate.tools;

import ru.protei.winter.jdbc.JdbcDAO;

import java.util.List;

/**
 * Created by michael on 05.07.16.
 */
public interface BatchProcess<T> {

   // public void onBatchEnd (Long lastIdValue);


    public void doInsert (JdbcDAO<Long, T> dao, List<T> entries);
    public void doUpdate (JdbcDAO<Long, T> dao, List<T> entries);
}
