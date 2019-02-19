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
        operator().column().not( "!" ).condition( " = ?" ).attribute( attr );
        return condition;
    }

    public SqlConditionBuilder lt( Object attr ) {
        if (columnName == null || attr == null) return condition;
        operator().column().condition( " < ?" ).attribute( attr );
        return condition;
    }

    public SqlConditionBuilder gt( Object attr ) {
        if (columnName == null || attr == null) return condition;
        operator().column().condition( " > ?" ).attribute( attr );
        return condition;
    }

    public SqlConditionBuilder le( Object attr ) {
        if (columnName == null || attr == null) return condition;
        operator().column().condition( " <= ?" ).attribute( attr );
        return condition;
    }

    public SqlConditionBuilder ge( Object attr ) {
        if (columnName == null || attr == null) return condition;
        operator().column().condition( " >= ?" ).attribute( attr );
        return condition;
    }

    public SqlConditionBuilder like( String attr ) {
        if (columnName == null || attr == null) return condition;
        operator().column().not( " NOT" ).condition( " LIKE %" ).condition( attr ).condition( "%" );
        return condition;
    }

    public SqlConditionBuilder isNull( Object attr ) {
        if (columnName == null || attr == null) return condition;
        operator().column().condition( " IS" ).not( " NOT" ).condition( " NULL" );
        return condition;
    }

    public SqlConditionBuilder in( Collection attr ) {
        if (columnName == null || attr == null) return condition;
        operator().column().not( " NOT" ).condition( " IN (" + attr.stream()
                .filter( o -> o != null )
                .map( this::inString )
                .collect( Collectors.joining( "," ) ) + ")" );
        return condition;
    }


    private SqlOperator attribute( Object attribute ) {
        condition.attribute( attribute );
        return this;
    }

    private SqlOperator condition( String condition ) {
        this.condition.condition( condition );
        return this;
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
