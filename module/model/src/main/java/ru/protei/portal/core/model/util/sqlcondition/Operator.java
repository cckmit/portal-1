package ru.protei.portal.core.model.util.sqlcondition;

import java.util.Collection;

public interface Operator {
    Operator not();

    Condition equal( Object attr );

    Condition lt( Object attr );

    Condition gt( Object attr );

    Condition le( Object attr );

    Condition ge( Object attr );

    Condition like( String attr );

    Condition isNull( Object attr );

    Condition in( Collection attr );

    Condition in( Condition condition );
}
