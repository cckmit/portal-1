package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.PortalBaseDAO;
import ru.protei.winter.jdbc.JdbcBaseDAO;
import ru.protei.winter.jdbc.JdbcSort;
import ru.protei.winter.jdbc.column.JdbcObjectColumn;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by michael on 25.05.16.
 */
public abstract class PortalBaseJdbcDAO<T> extends JdbcBaseDAO<Long,T> implements PortalBaseDAO<T> {

    public static final Object[] EMPTY_ARG_SET = new Object[]{};

//    @Autowired
//    List<JdbcObjectColumn<T>> pojoColumns;
    protected String[] pojoColumns;

    public PortalBaseJdbcDAO () {
        super();
    }

    public Long getMaxId () {
        return getMaxValue(getIdColumnName(), Long.class, null, (Object[])null);
    }

    public Long getMaxId (String cond, Object... args) {
        return getMaxValue(getIdColumnName(), Long.class, cond, args);
    }

    @Override
    public boolean saveOrUpdate(T entity) {
        Long id = getIdValue(entity);
        if (id == null || id <= 0L) {
            return persist(entity) != null;
        }
        else {
            return merge(entity);
        }
    }

    public <V> V getMaxValue (String field, Class<V> type, String cond, Object...args) {

        String query = "select max(" + field + ") from " + getTableName();
        if (cond != null) {
            query += " where " + cond;
        }

        return jdbcTemplate.queryForObject(query, type, args == null ? EMPTY_ARG_SET : args);
    }


    public Long getIdValue (T obj) {
        return this.getObjectMapper().getIdValue(obj);
    }


    public List <T> sortByField (List<T> entries, String fieldName, JdbcSort.Direction dir) {

        final JdbcObjectColumn<T> column = getObjectMapper().findColumn(fieldName);
        final int dirModifier = dir == JdbcSort.Direction.ASC ? 1 : -1;
        final int dirModifierRev = dir == JdbcSort.Direction.ASC ? -1 : 1;

        Collections.sort(entries, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {

                Object v1 = column.get(o1);
                Object v2 = column.get(o2);

                if (v1 == null && v2 == null) {
                    return 0;
                }

                if (v1 == null)
                    return -1;

                if (v2 == null)
                    return 1;

                if (v1 instanceof Comparable)
                    return ((Comparable) v1).compareTo(v2) * dirModifier;

                if (v2 instanceof Comparable)
                    return ((Comparable) v2).compareTo(v1) * dirModifierRev;

                //
                return 0;
            }
        });

        return entries;
    }


    @Override
    public T plainGet(Long id) {
        return partialGet(id,getPojoColumns());
    }

    @Override
    public List<T> plainListByCondition(String query, JdbcSort sort, List<Object> args) {
        return partialGetListByCondition(query, sort,args, getPojoColumns());
    }

    @Override
    public List<T> plainListByCondition(String query, JdbcSort sort, List<Object> args, int offset, int limit) {
        return partialGetListByCondition(query, args, offset, limit, sort, getPojoColumns()).getResults();
    }

    protected String[] getPojoColumns() {
        if (pojoColumns == null) {
            /**
             * we are we are hack you!
             */
            try {
                Field f = this.getObjectMapper().getClass().getDeclaredField("allColumns");
                f.setAccessible(true);

                pojoColumns = ((List<JdbcObjectColumn<T>>) f.get(this.getObjectMapper()))
                        .stream()
                        .filter(c -> c.getPath() == null || c.getPath().isEmpty())
                        .map(c -> c.getName())
                        .collect(Collectors.toList())
                        .toArray(new String[]{});

                f.setAccessible(false);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return pojoColumns;
    }

}
