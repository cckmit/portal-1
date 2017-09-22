package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.struct.DecimalNumberFilter;

import java.util.List;

/**
 * DAO децимальных номеров
 */
public interface DecimalNumberDAO extends PortalBaseDAO<DecimalNumber> {

    boolean checkIfExist( DecimalNumber number );

    Integer getMaxRegisterNumber( DecimalNumber number );

    Integer getMaxModification( DecimalNumber number );

    List<Long> getDecimalNumbersByEquipmentId( Long id );

    Integer getNextAvailableRegNumber(DecimalNumber number);

    Integer getNextAvailableRegNumberNotContainsInList(DecimalNumberFilter filter);

    Integer getNextAvailableModification(DecimalNumber number);

    Integer getNextAvailableRegisterNumberModificationNotContainsInList(DecimalNumberFilter filter);
}
