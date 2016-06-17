package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.PortalBaseDAO;
import ru.protei.winter.jdbc.JdbcBaseDAO;

/**
 * Created by michael on 25.05.16.
 */
public abstract class PortalBaseJdbcDAO<T> extends JdbcBaseDAO<Long,T> implements PortalBaseDAO<T> {

    public static final Object[] EMPTY_ARG_SET = new Object[]{};

    public Long getMaxId () {
        return getMaxValue(getIdColumnName(), Long.class, null, (Object[])null);
    }

    public Long getMaxId (String cond, Object... args) {
        return getMaxValue(getIdColumnName(), Long.class, cond, args);
    }

    public <V> V getMaxValue (String field, Class<V> type, String cond, Object...args) {

        String query = "select max(" + field + ") from " + getTableName();
        if (cond != null) {
            query += " where " + cond;
        }

        return jdbcTemplate.queryForObject(query, type, args == null ? EMPTY_ARG_SET : args);
    }
}
