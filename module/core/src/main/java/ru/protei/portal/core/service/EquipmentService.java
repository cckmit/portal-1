package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.DecimalNumber;
import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.List;

/**
 * Сервис управления оборудованием
 */
public interface EquipmentService {

    CoreResponse< Long > count( EquipmentQuery query );

    CoreResponse< List< Equipment > > equipmentList( EquipmentQuery query );

    CoreResponse< Equipment > getEquipment( long id );

    CoreResponse< Equipment > saveEquipment( Equipment equipment );

    CoreResponse< DecimalNumber > getNextAvailableDecimalNumber( DecimalNumber number );

    CoreResponse< DecimalNumber > getNextAvailableDecimalNumberModification( DecimalNumber number );

    CoreResponse< Boolean > checkIfExistDecimalNumber( DecimalNumber number );

    CoreResponse<Long> copyEquipment( Long equipmentId, String newName, Long authorId );

    CoreResponse<Boolean> removeEquipment( Long equipmentId );
}
