package ru.protei.portal.core.model.util.sqlcondition;

public interface Query extends Condition {
    Query select( String selectExpressionWithoutSelect );

    Condition from( String fromExpressionWithoutFromWithoutWhere );
}
