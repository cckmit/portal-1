package ru.protei.portal.core.model.util.sqlcondition;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.List;

public interface Query {
    Query select( String selectExpressionWithoutSelect );

    Query select( String... columnStrings );

    Query from( String fromExpressionWithoutFrom );

    Query whereExpression( String whereSqlExpressionWithoutWhere );

    Query where( Condition condition );

    Operator where( String columnName );

    Query attributes( Object... attrs );

    Query offset( Integer offset );

    Query limit( Integer limit );

    Query sort(En_SortDir direction, String... sortFieldsNames);

    Query sort(SortField... sortFields);

    Query sort(List<SortField> sortFields);

    Query groupBy( String... groupBy );

    Query groupBy( List<String> groupBy );

    Query union (Query query);

    Query withJoins(String joins);

    JdbcSort getSort();

    Query forUpdate();

    Condition asCondition();

    boolean isEmpty();

    /**
     * Аргументы SQL выражениея для  подстановки в ?
     */
    Object[] args();

    /**
     * SQL выражение с ? вместо аргументов
     */
    String buildSql();

    JdbcQueryParameters build();
}
