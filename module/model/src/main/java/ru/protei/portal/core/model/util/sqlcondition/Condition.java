package ru.protei.portal.core.model.util.sqlcondition;

import java.util.List;

public interface Condition {
    Operator and( String name );

    Operator or( String name );

    Condition and( Query inQuery );

    Condition or( Query inQuery );

    Condition and( Condition inCondition );

    Condition or( Condition inCondition );

    Condition condition( CharSequence arbitrarySqlCondition );

    Condition attribute( Object attr );

    boolean isEmpty();

    String getSqlCondition();

    List<Object> getSqlParameters();

    Query asQuery();

}
