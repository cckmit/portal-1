package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 *
 */
public class SqlConditionBuilder {

    public static SqlConditionBuilder init() {
        return new SqlConditionBuilder();
    }

    public SqlOperator and( String name ) {
        return new SqlOperator( " AND ", name, this );
    }

    public SqlOperator or( String name ) {
        return new SqlOperator( " OR ", name, this );
    }

    public SqlConditionBuilder condition( String condition ) {
        where.append( condition );
        return this;
    }

    public SqlConditionBuilder attribute( Object attr ) {
        args.add( attr );
        return this;
    }

    public SqlConditionBuilder or( SqlConditionBuilder inCondition ) {
        if (inCondition == null || inCondition.isEmpty()) return this;
        or().sqlCondition( inCondition );
        return this;
    }

    public SqlConditionBuilder and( SqlConditionBuilder inCondition ) {
        if (inCondition == null || inCondition.isEmpty()) return this;
        and().sqlCondition( inCondition );
        return this;
    }

    public SqlCondition build() {
        if (isEmpty()) {
            return new SqlCondition();
        }
        return new SqlCondition( getSqlCondition(), getSqlParameters() );
    }

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

    public SqlConditionBuilder offset( int offset ) {
        this.offset = offset;
        return this;
    }

    public SqlConditionBuilder limit( int limit ) {
        this.limit = limit;
        return this;
    }

    public SqlConditionBuilder sort( En_SortDir direction, String... sortFields ) {
        if (sortFields == null) return this;
        jdbcSort = new JdbcSort( TypeConverters.toWinter( direction, JdbcSort.Direction.ASC ),
                sortFields
        );
        return this;
    }

    public SqlConditionBuilder groupBy( String... groupBy ) {
        this.groupBy = asList( groupBy );
        return this;
    }

    public JdbcSort getSort() {
        return jdbcSort;
    }

    public boolean isEmpty() {
        return where == null || where.length() < 1;
    }

    public String getSqlCondition() {
        if (isEmpty()) where.append( "TRUE" );
        return where.toString();
    }

    public List<Object> getSqlParameters() {
        return args;
    }

    private void sqlCondition( SqlConditionBuilder condition ) {
        condition( "(" )
                .condition( condition.getSqlCondition() )
                .condition( ")" );
        this.args.addAll( condition.getSqlParameters() );
    }

    private SqlConditionBuilder and() {
        if (!isEmpty()) where.append( " AND " );
        return this;
    }

    private SqlConditionBuilder or() {
        if (!isEmpty()) where.append( " OR " );
        return this;
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
}

