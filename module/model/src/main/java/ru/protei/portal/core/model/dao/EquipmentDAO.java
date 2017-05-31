package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.query.EquipmentQuery;

import java.util.List;

/**
 * DAO оборудования
 */
public interface EquipmentDAO extends PortalBaseDAO<Equipment> {

    List<Equipment> getListByQuery( EquipmentQuery query );

    Long countByQuery( EquipmentQuery query );
}
