package ru.protei.portal.core.model.query;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

public class SqlOperator {


    private String operator;
    private String columnName;
    private SqlConditionBuilder condition;
    private boolean not;

    public SqlOperator( String operator, String name, SqlConditionBuilder sqlCondition ) {
        this.operator = operator;
        columnName = name;

        this.condition = sqlCondition;
    }

    public SqlOperator not() {
        not = true;
        return this;
    }

    public SqlConditionBuilder equal( Object attr ) {
        if (columnName == null || attr == null) return condition;
        operator().column().attribute( attr ).not( "!" );
        return condition.condition( "=?" );
    }


    public SqlConditionBuilder lt( Object attr ) {
        if (columnName == null || attr == null) return condition;
        operator().column().attribute( attr );
        return condition.condition( "<?" );
    }

    public SqlConditionBuilder gt( Object attr ) {
        if (columnName == null || attr == null) return condition;
        operator().column().attribute( attr );
        return condition.condition( ">?" );
    }

    public SqlConditionBuilder le( Object attr ) {
        if (columnName == null || attr == null) return condition;
        operator().column().attribute( attr );
        return condition.condition( "<=?" );
    }

    public SqlConditionBuilder ge( Object attr ) {
        if (columnName == null || attr == null) return condition;
        operator().column().attribute( attr );
        return condition.condition( ">=?" );
    }


    private SqlOperator attribute( Object attr ) {
        condition.attribute( attr );
        return this;
    }

    public SqlConditionBuilder like( String attr ) {
        if (columnName == null || attr == null) return condition;
        operator().column().not( " NOT" );
        return condition.condition( " LIKE %" ).condition( attr ).condition( "%" );
    }


    public SqlConditionBuilder isNull( Object attr ) {
        if (columnName == null || attr != null) return condition;
        operator().column().not( " NOT" );
        return condition.condition( " NULL" );
    }

    public SqlConditionBuilder in( Collection attr ) {
        if (columnName == null || attr == null) return condition;
        operator().column().not( " NOT" );
        return condition.condition( " IN (" + attr.stream()
                .filter( o -> o != null )
                .map( this::inString )
                .collect( Collectors.joining( "," ) ) + ")" );
    }

    private SqlOperator not( String s ) {
        if (not) condition.condition( s );
        return this;
    }


    private Object inString( Object o ) {
        if (o instanceof String) return "'" + o.toString() + "'";
        return o.toString();
    }

    private SqlOperator column() {
        condition.condition( columnName );
        return this;
    }

    private SqlOperator operator() {
        if (condition.isEmpty()) return this;
        condition.condition( operator );
        return this;
    }


}
