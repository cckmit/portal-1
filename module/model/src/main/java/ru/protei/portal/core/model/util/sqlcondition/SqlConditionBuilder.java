package ru.protei.portal.core.model.util.sqlcondition;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * Обертка над StringBuilder с утилитарными метададми формирования sql
 */
public class SqlConditionBuilder implements Operator, Condition, Query {

    private SqlConditionBuilder() {
    }

    public static Query query() {
        return new SqlConditionBuilder();
    }

    public static Condition condition() {
        return new SqlConditionBuilder();
    }

    @Override
    public Query select( String selectExpression ) {
        this.selectExpression = selectExpression.replace( "SELECT ", "" ).replace( "select ", "" ).trim();
        return this;
    }

    @Override
    public Query from( String fromExpression ) {
        this.fromExpression = fromExpression.replace( "FROM", "" ).replace( "from", "" ).replace( " WHERE", "" ).replace( " where", "" ).trim();
        return this;
    }

    @Override
    public Query whereExpression( String whereSqlExpression ) {
        if (whereSqlExpression == null) return this;
        this.where.append( whereSqlExpression );
        return this;
    }

    @Override
    public Operator where( String column ) {
        return sqlOperator( " AND ", column );
    }

    @Override
    public Condition condition( String where ) {
        if (where == null) return this;
        this.where.append( where );
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
    public Condition or( Condition inCondition ) {
        if (inCondition == null || inCondition.isEmpty()) return this;
        or().sqlCondition( inCondition );
        return this;
    }

    @Override
    public Operator and( String column ) {
        return sqlOperator( " AND ", column );
    }

    @Override
    public Operator or( String name ) {
        return sqlOperator( " OR ", name );
    }



    @Override
    public SqlCondition build() {
        if (isEmpty()) {
            return new SqlCondition();
        }
        return new SqlCondition( getSqlCondition(), getSqlParameters() );
    }

    @Override
    public JdbcQueryParameters asJdbcQueryParameters() {
        JdbcQueryParameters jdbcQueryParameters = new JdbcQueryParameters();
        if (!isEmpty()) {
            jdbcQueryParameters.withCondition( getSqlCondition(), getSqlParameters() );
        }
        if (offset != null) jdbcQueryParameters.withOffset( offset );
        if (limit != null) jdbcQueryParameters.withLimit( limit );
        if (groupBy != null) jdbcQueryParameters.withGroupBy( groupBy );
        if (jdbcSort != null) jdbcQueryParameters.withSort( jdbcSort );
        return jdbcQueryParameters;
    }

    @Override
    public Query offset( int offset ) {
        this.offset = offset;
        return this;
    }

    @Override
    public Query limit( int limit ) {
        this.limit = limit;
        return this;
    }

    @Override
    public Query sort( En_SortDir direction, String... sortFields ) {
        if (sortFields == null) return this;
        jdbcSort = new JdbcSort( TypeConverters.toWinter( direction, JdbcSort.Direction.ASC ),
                sortFields
        );
        return this;
    }

    @Override
    public Query groupBy( String... groupBy ) {
        this.groupBy = asList( groupBy );
        return this;
    }

    @Override
    public JdbcSort getSort() {
        return jdbcSort;
    }

    @Override
    public boolean isEmpty() {
        return (where == null || where.length() < 1) && isEmpty( selectExpression ) && isEmpty( fromExpression );
    }

    @Override
    public String getSqlCondition() {
        StringBuilder result = new StringBuilder();
        if (selectExpression != null) result.append( "SELECT " ).append( selectExpression );
        if (fromExpression != null) result.append( " FROM " ).append( fromExpression ).append( " WHERE " );
        result.append( where );
        if (isEmpty( where )) result.append( "TRUE" );
        return result.toString();
    }

    @Override
    public List<Object> getSqlParameters() {
        return args;
    }

    @Override
    public Query asQuery(){
        return this;
    }

    private SqlConditionBuilder sqlCondition( Condition condition ) {
        append( "(" )
                .append( condition.getSqlCondition() )
                .append( ")" );
        this.args.addAll( condition.getSqlParameters() );
        return this;
    }

    private SqlConditionBuilder and() {
        if (!isEmpty()) where.append( " AND " );
        return this;
    }

    private SqlConditionBuilder or() {
        if (!isEmpty()) where.append( " OR " );
        return this;
    }

    private SqlConditionBuilder append( String s ) {
        where.append( s );
        return this;
    }

    private boolean isEmpty( CharSequence s ) {
        return s == null || s.length() < 1;
    }

    @Override
    public String toString() {
        return "SqlConditionBuilder{" +
                "where=" + where +
                ", args=" + args +
                ", offset=" + offset +
                ", limit=" + limit +
                ", groupBy=" + groupBy +
                ", jdbcSort=" + (jdbcSort == null ? "null" : jdbcSort.getParams()) +
                '}';
    }


    private StringBuilder where = new StringBuilder();
    private List<Object> args = new ArrayList<>();
    private Integer offset;
    private Integer limit;
    private List<String> groupBy;
    private JdbcSort jdbcSort = null;
    private String selectExpression;
    private String fromExpression;

    /**
     * Operator
     * Вписан внуть билдера чтобы не создавать лишние объекты операторов
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
    public SqlConditionBuilder equal( Object attr ) {
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
    public Condition isNull( Object attr ) {
        if (columnName == null || attr == null) return done();
        operator().column().append( " IS" ).not( " NOT" ).append( " NULL" );
        return done();
    }

    @Override
    public Condition in( Collection attr ) {
        if (columnName == null || attr == null) return done();
        operator().column().not( " NOT" ).append( " IN (" + attr.stream()
                .filter( o -> o != null )
                .map( this::inString )
                .collect( Collectors.joining( "," ) ) + ")" );
        return done();
    }

    @Override
    public Condition in( Condition inCondition ) {
        if (inCondition == null || inCondition.isEmpty()) return done();
        operator().column().not( " NOT" ).append( " IN " ).sqlCondition( inCondition ).done();
        return done();
    }

    private SqlConditionBuilder not( String s ) {
        if (not) append( s );
        return this;
    }

    private Object inString( Object o ) {
        if (o instanceof String) return "'" + o.toString() + "'";
        return o.toString();
    }

    private SqlConditionBuilder column() {
        append( columnName );
        return this;
    }

    private SqlConditionBuilder operator() {
        if (isEmpty( where )) return this;
        append( operator );
        return this;
    }

    private SqlConditionBuilder done() {
        columnName = null;
        operator = null;
        not = false;
        return this;
    }

    private String operator;
    private String columnName;
    private boolean not;


    // Operator done
}

