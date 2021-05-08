package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Kit;

/**
 * DAO для местоположений проекта
 */
public interface KitDAO extends PortalBaseDAO<Kit> {
    String getLastSerialNumber(boolean isArmyProject);
}
