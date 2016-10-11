package ru.protei.portal.core.model.dao;

import ru.protei.winter.jdbc.JdbcDAO;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.List;

/**
 * Created by michael on 25.05.16.
 */
public interface PortalBaseDAO<T> extends JdbcDAO<Long,T> {

    public Long getMaxId ();
    public Long getMaxId (String cond, Object...args);
    public <V> V getMaxValue (String field, Class<V> type, String cond, Object...args);
    public Long getIdValue (T obj);
    public List<T> sortByField (List<T> entries, String fieldName, JdbcSort.Direction dir);
}
