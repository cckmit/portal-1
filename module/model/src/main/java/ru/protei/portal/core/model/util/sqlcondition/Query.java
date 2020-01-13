package ru.protei.portal.core.model.util.sqlcondition;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

public interface Query {
    Query select( String selectExpressionWithoutSelect );

    Query from( String fromExpressionWithoutFrom );

    Query whereExpression( String whereSqlExpressionWithoutWhere );

    Operator where( String columnName );

    Query attributes( Object... attrs );

    Query offset( int offset );

    Query limit( int limit );

    Query sort( En_SortDir direction, String... sortFields );

    Query groupBy( String... groupBy );

    JdbcSort getSort();

    JdbcQueryParameters asJdbcQueryParameters();

    Query forUpdate();

    Condition asCondition();
}
