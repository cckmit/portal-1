package ru.protei.portal.core.model.dao;

import ru.protei.winter.jdbc.JdbcDAO;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.List;

/**
 * Created by michael on 25.05.16.
 */
public interface PortalBaseDAO<T> extends JdbcDAO<Long,T> {

    Long getMaxId ();
    Long getMaxId (String cond, Object...args);

    boolean saveOrUpdate (T entity);

    <V> V getMaxValue (String field, Class<V> type, String cond, Object...args);
    Long getIdValue (T obj);
    List<T> sortByField (List<T> entries, String fieldName, JdbcSort.Direction dir);
}
