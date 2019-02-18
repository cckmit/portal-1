package ru.protei.portal.core.model.query;

import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class SqlConditionBuilder {

    public static SqlConditionBuilder init() {
       return new SqlConditionBuilder();
    }

    StringBuilder where = new StringBuilder();
    List<Object> args = new ArrayList<>();
    
    public List<Object> getSqlParameters() {
        return args;
    }

    public SqlOperator and( String name ) {
        return new SqlOperator( " AND ", name, this );
    }

    public SqlOperator or( String name ) {
        return new SqlOperator( " OR ", name, this );
    }

    public SqlConditionBuilder condition( String s ) {
        where.append( s );
        return this;
    }

    public SqlConditionBuilder or(SqlConditionBuilder inCondition) {
        if (inCondition == null || inCondition.isEmpty()) return this;
        or().sqlCondition( inCondition );
        return this;
    }

    public SqlConditionBuilder and(SqlConditionBuilder inCondition) {
        if (inCondition == null || inCondition.isEmpty()) return this;
        and().sqlCondition( inCondition );
        return this;
    }

    public JdbcQueryParameters asJdbcQueryParameters() {
        JdbcQueryParameters jdbcQueryParameters = new JdbcQueryParameters();
        if(!isEmpty()){
            jdbcQueryParameters.withCondition( getSqlCondition(), getSqlParameters() );
        }
        return jdbcQueryParameters;
    }

    private void sqlCondition( SqlConditionBuilder condition ) {
        condition( "(" )
                .condition( condition.getSqlCondition() )
                .condition( ")" );
        this.args.addAll( condition.getSqlParameters() );
    }

    private SqlConditionBuilder and() {
        if (!isEmpty( )) where.append( " AND " );
        return this;
    }

    private SqlConditionBuilder or() {
        if (!isEmpty( )) where.append( " OR " );
        return this;
    }

    @Override
    public String toString() {
        return "SqlCondition{" +
                "where=" + where +
                ", args=" + args +
//                ", sortOrder=" + sortOrder +
                '}';
    }

    public SqlConditionBuilder attribute( Object attr ) {
        args.add( attr );
        return this;
    }

    public boolean isEmpty() {
        return where == null || where.length() < 1;
    }

    public String getSqlCondition() {
        if (isEmpty( )) where.append( "TRUE" );
        return where.toString();
    }
}

