package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.query.EquipmentQuery;

import java.util.List;

/**
 * DAO оборудования
 */
public interface EquipmentDAO extends PortalBaseDAO<Equipment> {

    List<Equipment> getListByQuery( EquipmentQuery query );

    boolean checkIfExistPAMR_RegNum( String classifierCode, String registerNumber );

    boolean checkIfExistPDRA_RegNum( String classifierCode, String registerNumber );

    String getMaxPDRA_RegNum( String classifierCode );

    String getMaxPAMR_RegNum( String classifierCode );

    String getMaxPAMR_RegNumModification( String classifierCode, String registerNumber );

    String getMaxPDRA_RegNumModification( String classifierCode, String registerNumber );
}
