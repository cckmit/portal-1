package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
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

    CoreResponse<Long> count( EquipmentQuery query );

    CoreResponse<List<Equipment >> equipmentList( EquipmentQuery query );

    CoreResponse<Equipment> getEquipment( long id );

    CoreResponse<Equipment> saveEquipment( Equipment equipment );

    CoreResponse<Boolean> checkIfExistPDRA_Number( String classifierCode, String registerNumber );

    CoreResponse<Boolean> checkIfExistPAMR_Number( String classifierCode, String registerNumber );
}
