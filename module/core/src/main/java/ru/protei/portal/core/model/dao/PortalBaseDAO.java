package ru.protei.portal.core.model.dao;

import ru.protei.winter.jdbc.JdbcDAO;

/**
 * Created by michael on 25.05.16.
 */
public interface PortalBaseDAO<T> extends JdbcDAO<Long,T> {

    public Long getMaxId ();
    public Long getMaxId (String cond, Object...args);
    public <V> V getMaxValue (String field, Class<V> type, String cond, Object...args);
}
