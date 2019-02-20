package ru.protei.portal.core.model.util.sqlcondition;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.List;

public interface Condition {
    Operator and( String name );

    Operator or( String name );

    Condition where( String condition );

    Condition attribute( Object attr );

    Condition or( Condition inCondition );

    Condition and( Condition inCondition );

    Condition offset( int offset );

    Condition limit( int limit );

    Condition sort( En_SortDir direction, String... sortFields );

    Condition groupBy( String... groupBy );

    JdbcSort getSort();

    boolean isEmpty();

    String getSqlCondition();

    List<Object> getSqlParameters();

    SqlCondition build();

    JdbcQueryParameters asJdbcQueryParameters();


}
