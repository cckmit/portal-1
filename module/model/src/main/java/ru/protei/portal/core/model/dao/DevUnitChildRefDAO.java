package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.DevUnitChildRef;

public interface DevUnitChildRefDAO extends PortalBaseDAO<DevUnitChildRef> {
    int removeParents(Long productId);

    int removeChildren(Long productId);

    int removeProductDirection(Long productId);
}
