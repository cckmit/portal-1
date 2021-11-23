package ru.protei.portal.core.model.dao.impl;

import org.slf4j.Logger;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.PortalBaseDAO;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.DataQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.core.utils.enums.HasId;
import ru.protei.winter.jdbc.JdbcBaseDAO;
import ru.protei.winter.jdbc.JdbcHelper;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;
import ru.protei.winter.jdbc.column.JdbcObjectColumn;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by michael on 25.05.16.
 */
public abstract class PortalBaseJdbcDAO<T> extends JdbcBaseDAO<Long,T> implements PortalBaseDAO<T> {

    public static final Object[] EMPTY_ARG_SET = new Object[]{};


    /**
     * реализация логики QueryConditionBuilder через вызов метода
     */
    class QueryConditionBuilderMethod implements QueryConditionBuilder {
        private Method method;

        public QueryConditionBuilderMethod(Method method) {
            this.method = method;
        }

        @Override
        public SqlCondition buildCondition(DataQuery query) {
            try {
                return (SqlCondition) method.invoke(PortalBaseJdbcDAO.this, query);
            }
            catch (Throwable e) {
                throw new RuntimeException("sql condition builder, method invocation error", e);
            }
        }
    }

    protected String[] pojoColumns;

    /**
     * Соответствие между классом Query и логикой формирования условия запроса
     */
    protected Map<Class<? extends DataQuery>, QueryConditionBuilder> queryBuilders;


    public PortalBaseJdbcDAO () {
        super();
        queryBuilders = new HashMap<>();
        registerConditionBuilderMethods();
    }

    private void registerConditionBuilderMethods () {
        Arrays.stream(this.getClass().getMethods())
                .filter(
                        method -> method.isAnnotationPresent(SqlConditionBuilder.class)
                        && SqlCondition.class.isAssignableFrom(method.getReturnType())
                        && method.getParameterCount() == 1
                        && DataQuery.class.isAssignableFrom(method.getParameterTypes()[0])
                )
                .forEach(method -> registerConditionBuilder((Class<DataQuery>)method.getParameterTypes()[0], new QueryConditionBuilderMethod(method)));
    }

    @Override
    public <Q extends DataQuery> void registerConditionBuilder (Class<Q> queryType, QueryConditionBuilder<Q> builder) {
        this.queryBuilders.put(queryType, builder);
    }

    @Override
    public SqlCondition createSqlCondition(DataQuery query) {
        QueryConditionBuilder<DataQuery> builder = queryBuilders.get(query.getClass());
        if (builder == null)
            throw new NoConditionBuilderDefinedException(this, query.getClass());

        return builder.buildCondition(query);
    }

    @Override
    public Long count(DataQuery query) {
        StringBuilder sql = new StringBuilder("select count(*) from ").append(getTableName());

        SqlCondition whereCondition = createSqlCondition(query);

        if (!whereCondition.condition.isEmpty()) {
            sql.append(" where ").append(whereCondition.condition);
        }

        return jdbcTemplate.queryForObject(sql.toString(), Long.class, whereCondition.args.toArray());
    }

    @Override
    public Long countByExpression(String expression, Object...args) {
        StringBuilder sql = new StringBuilder("select count(*) from ").append(getTableName());

        if (expression != null) {
            sql.append(" where ").append(expression);
        }

        return jdbcTemplate.queryForObject(sql.toString(), Long.class, args);
    }

    @Override
    public List<T> listByQuery(DataQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return getList(parameters);
    }

    @Override
    public SearchResult<T> getSearchResultByQuery(DataQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return getSearchResult(parameters);
    }

    @Override
    public SearchResult<T> getSearchResult( JdbcQueryParameters parameters ) {
        SearchResult<T> searchResult = new SearchResult<>( getList( parameters ) );
        if( searchResult.getTotalCount() < parameters.getLimit() ){
            return searchResult;
        }
        if (parameters.getOffset() <= 0 && parameters.getLimit() > 0) {
            searchResult.setTotalCount( getObjectsCount( parameters.getSqlCondition(), parameters.getParamValues(), parameters.getJoins(), parameters.isDistinct() ) );
        }
        return searchResult;
    }

    private JdbcQueryParameters buildJdbcQueryParameters(DataQuery query) {

        SqlCondition where = createSqlCondition(query);

        JdbcQueryParameters parameters = new JdbcQueryParameters();
        if (where.isConditionDefined())
            parameters.withCondition(where.condition, where.args);

        parameters.withOffset(query.getOffset());
        parameters.withLimit(query.getLimit());
        parameters.withSort(TypeConverters.createSort(query));

        return parameters;
    }

    public Long getMaxId () {
        return getMaxValue(getIdColumnName(), Long.class, null, null, (Object[])null);
    }

    public Long getMaxId (String cond, Object... args) {
        return getMaxValue(getIdColumnName(), Long.class, null, cond, args);
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

    @Override
    public Long saveOrUpdateBatch(Collection<T> entities) {
        if (entities == null) {
            return 0L;
        }
        long handled = 0;
        for (T entity : entities) {
            if (saveOrUpdate(entity)) {
                handled++;
            }
        }
        return handled;
    }

    public <V> V getMaxValue (String field, Class<V> type, String cond, Object...args) {
        return getMaxValue(field, type, null, cond, args);
    }

    public <V> V getMaxValue (String field, Class<V> type, String join, String cond, Object...args) {

        String query = "select max(" + field + ") from " + getTableName();

        if (join != null) {
            query += " " + join;
        }

        if (cond != null) {
            query += " where " + cond;
        }

        return jdbcTemplate.queryForObject(query, type, args == null ? EMPTY_ARG_SET : args);
    }

    @Override
    public List<Long> keys() {
        String query = "select " + getIdColumnName() + " from " + getTableName();
        return jdbcTemplate.queryForList(query, Long.class);
    }

    @Override
    public <K> List<K> listColumnValue(String column, Class<K> type) {
        String query = "select " + column + " from " + getTableName();
        return jdbcTemplate.queryForList(query, type);
    }

    @Override
    public <K> List<K> listColumnValue(String column, Class<K> type, String condition, Object... args) {

        if (HelperFunc.isNotEmpty(condition)) {
            String query = "select " + column + " from " + getTableName();
            query += " where " + condition;
            return jdbcTemplate.queryForList(query, args, type);
        }
        else
            return listColumnValue (column, type);
    }

    public Long getIdValue (T obj) {
        return this.getObjectMapper().getIdValue(obj);
    }


    public List <T> sortByField (List<T> entries, String fieldName, JdbcSort.Direction dir) {

        final JdbcObjectColumn<T> column = getObjectMapper().findColumn(fieldName);
        final int dirModifier = dir == JdbcSort.Direction.ASC ? 1 : -1;
        final int dirModifierRev = dir == JdbcSort.Direction.ASC ? -1 : 1;

        Collections.sort(entries, ( o1, o2 ) -> {
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

            return 0;
        } );

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


    @Override
    public <K> List<T> listByColumnIn(String columnName, Collection<K> values) {
        List<Object> args = new ArrayList<Object>();
        String sqlCondition = getObjectMapper().getSelectTableName() + "." + columnName + " in " + JdbcHelper.makeSqlStringCollection(values, args, null);
        return JdbcHelper.getObjects(getObjectMapper(), jdbcTemplate, sqlCondition, args, -1, -1, null);
    }



    protected Integer booleanAsNumber( Boolean isExpression ) {
        if (isExpression == null) return null;
        return isExpression ? 1 : 0;
    }

    protected Collection<Integer> collectIds( Collection<? extends HasId> hasIds ) {
        if (hasIds == null) return null;
        ArrayList<Integer> ids = new ArrayList<>();
        for (HasId hasId : hasIds) {
            ids.add( hasId.getId() );
        }

        return ids;
    }


    private static final Logger log = getLogger( PortalBaseJdbcDAO.class );
}
