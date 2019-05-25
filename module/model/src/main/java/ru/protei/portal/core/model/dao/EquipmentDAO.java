package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Equipment;
import ru.protei.portal.core.model.query.EquipmentQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

/**
 * DAO оборудования
 */
public interface EquipmentDAO extends PortalBaseDAO<Equipment> {

    SearchResult<Equipment> getSearchResult(EquipmentQuery query);
}
