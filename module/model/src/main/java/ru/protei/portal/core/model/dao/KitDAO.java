package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Kit;

import java.util.List;

/**
 * DAO для Комплектов
 */
public interface KitDAO extends PortalBaseDAO<Kit> {
    String getLastSerialNumber(boolean isArmyProject);

    boolean isAvailableSerialNumbers(List<String> serialNumbers);
}
