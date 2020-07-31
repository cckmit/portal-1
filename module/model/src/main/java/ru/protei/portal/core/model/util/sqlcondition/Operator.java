package ru.protei.portal.core.model.util.sqlcondition;

import java.util.Collection;

public interface Operator {
    Operator not();

    /**
     * Expected Any not null object or true
     */
    Operator not(Object isNotIfTrue);

    Condition equal( Object attr );

    Condition lt( Object attr );

    Condition gt( Object attr );

    Condition le( Object attr );

    Condition ge( Object attr );

    Condition like( String attr );

    Condition regexp( String condition );

    /**
     * Expected Any not null object or true
     */
    Condition isNull(Object notNullOrTrue);

    Condition in( Collection attr );

    Condition in( Query query );

}
