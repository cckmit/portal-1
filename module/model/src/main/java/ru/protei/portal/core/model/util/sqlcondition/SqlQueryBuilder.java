package ru.protei.portal.core.model.util.sqlcondition;

import static ru.protei.portal.core.model.dict.En_SortDir.*;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.Pair;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * Обертка над StringBuilder с утилитарными метададми формирования sql
 */
public class SqlQueryBuilder implements Operator, Condition, Query {

    private SqlQueryBuilder() {
    }

    public static Query query() {
        return new SqlQueryBuilder();
    }

    public static Condition condition() {
        return new SqlQueryBuilder();
    }

    /**
     * Заменить/Установить выражение SELECT
     */
    @Override
    public Query select( String selectExpression ) {
        this.selectExpression = new StringBuilder( selectExpression.replace( "SELECT ", "" ).replace( "select ", "" ).trim() );
        return this;
    }

    /**
     * Заменить/Установить выражение SELECT
     * MAX(age), name, city
     */
    @Override
    public Query select( String... columnStrings ) {
        this.selectExpression = new StringBuilder();
        for (String s : columnStrings) {
            if (s == null) continue;
            if (selectExpression.length() > 0) selectExpression.append( ", " );
            selectExpression.append( s );
        }
        return this;
    }

    /**
     * Заменить/Установить выражение FROM
     */
    @Override
    public Query from( String fromExpression ) {
        this.fromExpression = fromExpression.replace( "FROM", "" ).replace( "from", "" ).replace( " WHERE", "" ).replace( " where", "" ).trim();
        return this;
    }

    /**
     * Заменить/Установить выражение WHERE в том числе обнулить "аргументы"
     */
    @Override
    public Query whereExpression( String whereSqlExpression ) {
        this.where = new StringBuilder();
        this.args = new ArrayList<Object>();
        asCondition().condition( whereSqlExpression );
        return this;
    }

    /**
     * Заменить/Установить выражение WHERE в том числе обнулить "аргументы"
     */
    @Override
    public Query where( Condition condition ) {
        this.where = new StringBuilder();
        this.args = new ArrayList<Object>();
        if (condition == null || condition.isEmpty()) return this;
        this.where.append( condition.getSqlCondition() );
        this.args.addAll( condition.getSqlParameters() );
        return this;
    }

    /**
     * Заменить/Установить значения аттрибутов
     */
    @Override
    public Query attributes( Object... attrs ) {
        if (attrs != null) this.args = toListWithNulls( attrs );
        return this;
    }

    @Override
    public Operator where( String column ) {
        return sqlOperator( " AND ", column );
    }

    /**
     * SQL без winter
     */
    @Override
    public String buildSql() {
        StringBuilder result = new StringBuilder();
        if (union != null && (limit != null || offset != null)) result.append( "(" );

        if (selectExpression != null) result.append( "SELECT " ).append( selectExpression );
        if (fromExpression != null) result.append( " FROM " ).append( fromExpression );
        if (fromExpression != null && !isEmpty( where )) result.append( " WHERE " );
        if (!isEmpty( where )) result.append( getSqlCondition() );
        result.append( makeGroupByString( groupBy ) );
        result.append( makeSortString( sortFields ) );
        result.append( makeOffsetLimit( offset, limit ) );
        if (isForUpdate) result.append( " FOR UPDATE" );

        if (union != null && (limit != null || offset != null)) result.append( ")" );
        if (union != null) {
            result.append( " UNION " ).append( union.buildSql() );
        }
        return result.toString();
    }

    @Override
    public Object[] args() {
        if (null == union) {
            return args.toArray();
        }

        List<Object> unionArgs = new ArrayList<Object>( args );
        unionArgs.addAll( union.asCondition().getSqlParameters() );
        return unionArgs.toArray();
    }

    @Override
    public JdbcQueryParameters build() {
        JdbcQueryParameters jdbcQueryParameters = new JdbcQueryParameters();
        if (!isEmpty()) {
            jdbcQueryParameters.withCondition( getSqlCondition(), getSqlParameters() );
        }
        if (offset != null) jdbcQueryParameters.withOffset( offset );
        if (limit != null) jdbcQueryParameters.withLimit( limit );
        if (groupBy != null) jdbcQueryParameters.withGroupBy( groupBy );
        if (sortFields != null) jdbcQueryParameters.withSort( makeJdbcSort( sortFields ) );
        if (joins != null) jdbcQueryParameters.withJoins( joins );
        return jdbcQueryParameters;
    }

    @Override
    public Query forUpdate() {
        isForUpdate = true;
        return this;
    }

    @Override
    public Condition asCondition() {
        return this;
    }

    @Override
    public Query offset( Integer offset ) {
        this.offset = offset;
        return this;
    }

    @Override
    public Query limit( Integer limit ) {
        this.limit = limit;
        return this;
    }

    /**
     * Добавляет все опции сортировки с указанным порядком к существующим
     */
    @Override
    public Query sort( En_SortDir direction, String... sortFields ) {
        if (sortFields == null) return this;
        if (this.sortFields == null) this.sortFields = new ArrayList<SortField>();
        for (String sortField : sortFields) {
            this.sortFields.add( new SortField( sortField, direction ) );
        }
        return this;
    }

    /**
     * Заменяет все опции сортировки
     */
    @Override
    public Query sort( SortField... sortFields ) {
        return sort( toListOmitNulls( sortFields ) );
    }

    /**
     * Заменяет все опции сортировки
     */
    @Override
    public Query sort( List<SortField> sortFields ) {
        if (sortFields == null) return this;
        this.sortFields = sortFields;
        return this;
    }

    /**
     * Заменяет все опции группировки
     */
    @Override
    public Query groupBy( String... groupBy ) {
        return groupBy( toListOmitNulls( groupBy ) );
    }

    /**
     * Заменяет все опции группировки
     */
    @Override
    public Query groupBy( List<String> groupBy ) {
        if (groupBy == null) return this;
        this.groupBy = groupBy;
        return this;
    }

    @Override
    public Query union( Query query ) {
        union = query;
        return this;
    }

    private static <T> List<T> toListWithNulls( T[] source ) {
        return toList( source, false );
    }

    private static <T> List<T> toListOmitNulls( T[] source ) {
        return toList( source, true );
    }

    private static <T> List<T> toList( T[] source, boolean omitNulls ) {
        ArrayList<T> list = new ArrayList<T>();
        if (source == null) return list;
        for (T field : source) {
            if (omitNulls && field == null) {
                continue;
            }
            list.add( field );
        }
        return list;
    }

    private Query union;
    // Query end

    // Condition begin
    @Override
    public Condition condition( CharSequence arbitrarySqlCondition ) {
        if (arbitrarySqlCondition == null) return this;
        this.where.append( arbitrarySqlCondition );
        return this;
    }

    @Override
    public Condition attribute( Object attr ) {
        args.add( attr );
        return this;
    }

    @Override
    public Condition and( Condition inCondition ) {
        if (inCondition == null || inCondition.isEmpty()) return this;
        and().sqlCondition( inCondition );
        return this;
    }

    @Override
    public Condition and( Query inQuery ) {
        if (inQuery == null || inQuery.isEmpty()) return this;
        and().sqlQuery( inQuery );
        return this;
    }

    @Override
    public Condition or( Condition inCondition ) {
        if (inCondition == null || inCondition.isEmpty()) return this;
        or().sqlCondition( inCondition );
        return this;
    }

    @Override
    public Condition or( Query inQuery ) {
        if (inQuery == null || inQuery.isEmpty()) return this;
        or().sqlQuery( inQuery );
        return this;
    }

    @Override
    public Operator and( String column ) {
        return sqlOperator( " AND ", column );
    }

    @Override
    public Operator or( String column ) {
        return sqlOperator( " OR ", column );
    }

    @Override
    public Query withJoins( String joins ) {
        this.joins = joins;
        return this;
    }

    /**
     * SQL для winter.
     */
    @Override
    public JdbcSort getSort() {
        return makeJdbcSort( sortFields );
    }

    @Override
    public boolean isEmpty() {
        return isEmpty( where ) && isEmpty( selectExpression ) && isEmpty( fromExpression );
    }

    /**
     * Condition
     * функционал интерфейса Condition
     */


    /**
     * Содержимое только WHERE.
     * SQL для winter.
     * При пустом условии WHERE возвращает null - winter не добавит ключевое слово "WHERE"
     */
    @Override
    public String getSqlCondition() {
        if (isEmpty( where )) return null;
        return where.toString();
    }

    @Override
    public List<Object> getSqlParameters() {
        return args;
    }

    @Override
    public Query asQuery() {
        return this;
    }

    private CharSequence makeSortString( List<SortField> sortFields ) {
        if (sortFields == null || sortFields.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (SortField sortField : sortFields) {
            if (sb.length() > 0) {
                sb.append( "," );
            } else {
                sb.append( " ORDER BY" );
            }
            sb.append( " " ).append( sortField.fieldName ).append( " " ).append( sortField.sortDirection );
        }
        return sb;
    }

    private CharSequence makeGroupByString( List<String> groupBy ) {
        if (groupBy == null || groupBy.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String group : groupBy) {
            if (sb.length() > 0) {
                sb.append( "," );
            } else {
                sb.append( " GROUP BY" );
            }
            sb.append( " " ).append( group );
        }
        return sb;
    }

    private CharSequence makeOffsetLimit( Integer offset, Integer limit ) {
        if (offset == null && limit == null) return "";
        StringBuilder sb = new StringBuilder();

        sb.append( " LIMIT " );
        if (offset != null) {
            sb.append( offset ).append( "," );
        }

        if (limit != null && limit >= 0) sb.append( limit );
        return sb;
    }

    private SqlQueryBuilder sqlCondition( Condition condition ) {
        append( "(" )
                .append( condition.getSqlCondition() )
                .append( ")" );
        this.args.addAll( condition.getSqlParameters() );
        return this;
    }

    private SqlQueryBuilder sqlQuery( Query sqlQuery ) {
        append( "(" )
                .append( sqlQuery.buildSql() )
                .append( ")" );
        this.args.addAll( sqlQuery.asCondition().getSqlParameters() );
        return this;
    }

    private SqlQueryBuilder and() {
        if (!isEmpty( where )) append( " AND " );
        return this;
    }

    private SqlQueryBuilder or() {
        if (!isEmpty( where )) append( " OR " );
        return this;
    }

    private SqlQueryBuilder append( String s ) {
        where.append( s );
        return this;
    }

    private boolean isEmpty( CharSequence s ) {
        return s == null || s.length() < 1;
    }

    private JdbcSort.Direction toWinter( En_SortDir direction ) {
        return direction == ASC ? JdbcSort.Direction.ASC : JdbcSort.Direction.DESC;
    }

    private JdbcSort makeJdbcSort( List<SortField> sortFields ) {
        List<Pair<String, JdbcSort.Direction>> collect = sortFields.stream()
                .map( sf -> new Pair<String, JdbcSort.Direction>( sf.fieldName, toWinter( sf.sortDirection ) ) ).collect( Collectors.toList() );
        return new JdbcSort( collect );
    }

    @Override
    public String toString() {
        return "SqlQueryBuilder{" +
                "selectExpression=" + selectExpression +
                ", fromExpression=" + fromExpression +
                ", where=" + where +
                ", args=" + args +
                ", offset=" + offset +
                ", limit=" + limit +
                ", groupBy=" + groupBy +
                ", sortFields=" + sortFields +
                '}';
    }


    private StringBuilder where = new StringBuilder();
    private List<Object> args = new ArrayList<Object>();
    private Integer offset;
    private Integer limit;
    private List<String> groupBy;
    private List<SortField> sortFields;
    private StringBuilder selectExpression;
    private String fromExpression;
    private boolean isForUpdate;

    // Condition end

    // Operator begin

    /**
     * Operator
     * функционал интерфейса Operator
     * каждый Operator заканчивается вызовом метода done() для  очистки полей оператора.
     */
    private Operator sqlOperator( String operator, String columnName ) {
        this.operator = operator;
        this.columnName = columnName;

        return this;
    }

    @Override
    public Operator not() {
        not = true;
        return this;
    }

    @Override
    public Operator not(Object addNotIfTrue) {
        if (addNotIfTrue == null) return this;
        if ((addNotIfTrue instanceof Boolean) && !((Boolean) addNotIfTrue)) return this;
        return not();
    }

    @Override
    public SqlQueryBuilder equal( Object attr ) {
        if (columnName == null || attr == null) return done();
        operator().column().append( " " ).not( "!" ).append( "= ?" ).attribute( attr );
        return done();
    }

    @Override
    public Condition lt( Object attr ) {
        if (columnName == null || attr == null) return done();
        operator().column().append( " < ?" ).attribute( attr );
        return done();
    }

    @Override
    public Condition gt( Object attr ) {
        if (columnName == null || attr == null) return done();
        operator().column().append( " > ?" ).attribute( attr );
        return done();
    }

    @Override
    public Condition le( Object attr ) {
        if (columnName == null || attr == null) return done();
        operator().column().append( " <= ?" ).attribute( attr );
        return done();
    }

    @Override
    public Condition ge( Object attr ) {
        if (columnName == null || attr == null) return done();
        operator().column().append( " >= ?" ).attribute( attr );
        return done();
    }

    @Override
    public Condition like( String attr ) {
        if (columnName == null || attr == null) return done();
        operator().column().not( " NOT" ).append( " LIKE ?" ).attribute( "%" + attr + "%" );
        return done();
    }

    @Override
    public Condition regexp( String attr ) {
        if (columnName == null || attr == null) return done();
        operator().column().not( " NOT" ).append( " REGEXP ?" ).attribute( attr );
        return done();
    }

    @Override
    public Condition isNull( Object notNullOrTrue ) {
        if (columnName == null || notNullOrTrue == null) return done();
        if ((notNullOrTrue instanceof Boolean) && !((Boolean) notNullOrTrue)) return done();
        operator().column().append( " IS" ).not( " NOT" ).append( " NULL" );
        return done();
    }

    @Override
    public Condition in( Collection attr ) {
        if (columnName == null || attr == null) return done();
        StringBuilder sb = new StringBuilder();
        if (attr.isEmpty()) {
            sb.append(" NULL");
        } else {
            for (Object o : attr) {
                if (o == null) continue;
                if (sb.length() > 0) sb.append(",");
                sb.append(inString(o));
            }
        }

        operator().column().not( " NOT" ).append( " IN (" + sb + ")" );
        return done();
    }

    @Override
    public Condition in( Query inQuery ) {
        if (inQuery == null || inQuery.isEmpty()) return done();
        operator().column().not( " NOT" ).append( " IN " ).sqlQuery( inQuery ).done();
        return done();
    }

    private SqlQueryBuilder not( String s ) {
        if (not) append( s );
        return this;
    }

    private Object inString( Object o ) {
        if (o instanceof String) return "'" + o.toString() + "'";
        return o.toString();
    }

    private SqlQueryBuilder column() {
        append( columnName );
        return this;
    }

    private SqlQueryBuilder operator() {
        if (isEmpty( where )) return this;
        append( operator );
        return this;
    }

    private SqlQueryBuilder done() {
        columnName = null;
        operator = null;
        not = false;
        return this;
    }

    private String operator;
    private String columnName;
    private boolean not;
    private String joins;

    // Operator end
}

