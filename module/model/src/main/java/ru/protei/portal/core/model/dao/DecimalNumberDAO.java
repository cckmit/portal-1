package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.struct.DecimalNumberQuery;

import java.util.List;

/**
 * DAO децимальных номеров
 */
public interface DecimalNumberDAO extends PortalBaseDAO<DecimalNumber> {

    boolean checkExists(DecimalNumber number );

    List<Long> getDecimalNumbersByEquipmentId( Long id );

    Integer getNextAvailableRegNumber(DecimalNumberQuery filter);

    Integer getNextAvailableModification(DecimalNumberQuery filter);

    DecimalNumber find(DecimalNumber decimalNumber);
}
