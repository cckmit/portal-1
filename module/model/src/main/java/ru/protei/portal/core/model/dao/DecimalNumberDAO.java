package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.DecimalNumber;

/**
 * DAO децимальных номеров
 */
public interface DecimalNumberDAO extends PortalBaseDAO<DecimalNumber> {

    boolean checkIfExist( DecimalNumber number );

    Integer getMaxRegisterNumber( DecimalNumber number );

    Integer getMaxModification( DecimalNumber number );
}
