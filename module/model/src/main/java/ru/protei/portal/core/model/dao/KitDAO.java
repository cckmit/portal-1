package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Kit;

import java.util.List;

/**
 * DAO для Комплектов
 */
public interface KitDAO extends PortalBaseDAO<Kit> {
    List<Kit> listByDeliveryId(Long deliveryId);
    String getLastSerialNumber(boolean isArmyProject);
    String getLastSerialNumber(Long deliveryId);
    boolean isExistAnySerialNumbers(List<String> serialNumbers);
    List<Kit> getModulesGroupedByKit(Long deliveryId);
}
