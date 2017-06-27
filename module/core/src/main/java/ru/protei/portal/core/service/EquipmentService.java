package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.EquipmentQuery;

import java.util.List;
import java.util.Set;

/**
 * Сервис управления оборудованием
 */
public interface EquipmentService {

    CoreResponse< Long > count( EquipmentQuery query );

    CoreResponse< List< Equipment > > equipmentList( EquipmentQuery query, Set< UserRole > roles );

    CoreResponse< Equipment > getEquipment( long id, Set< UserRole > roles );

    CoreResponse< Equipment > saveEquipment( Equipment equipment, Set< UserRole > roles );

    CoreResponse< DecimalNumber > getNextAvailableDecimalNumber( DecimalNumber number );

    CoreResponse< DecimalNumber > getNextAvailableDecimalNumberModification( DecimalNumber number );

    CoreResponse< Boolean > checkIfExistDecimalNumber( DecimalNumber number );

    CoreResponse<Long> copyEquipment( Long equipmentId, String newName, Long authorId, Set< UserRole > roles );

    CoreResponse<Boolean> removeEquipment( Long equipmentId, Set< UserRole > roles );
}
