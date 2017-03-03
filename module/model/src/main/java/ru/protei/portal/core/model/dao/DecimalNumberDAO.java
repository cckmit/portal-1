package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.DecimalNumber;

import java.util.List;
import java.util.Set;

/**
 * DAO децимальных номеров
 */
public interface DecimalNumberDAO extends PortalBaseDAO<DecimalNumber> {

    boolean checkIfExist( DecimalNumber number );

    Integer getMaxRegisterNumber( DecimalNumber number );

    Integer getMaxModification( DecimalNumber number );

    Set<Long> getEquipmentsIds( List<DecimalNumber> numbers );
}
