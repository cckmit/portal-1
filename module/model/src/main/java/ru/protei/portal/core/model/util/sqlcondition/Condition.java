package ru.protei.portal.core.model.util.sqlcondition;

import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

public interface Condition {
    Operator and( String name );

    Operator or( String name );

    Condition or( Condition inCondition );

    Condition and( Condition inCondition );

    Condition condition( String arbitrarySqlExpression );

    Condition attribute( Object attr );

    boolean isEmpty();

    String getSqlCondition();

    List<Object> getSqlParameters();

    SqlCondition build();

    Query asQuery();

}
