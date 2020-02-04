package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.DevUnitChildRef;

public interface DevUnitChildRefDAO extends PortalBaseDAO<DevUnitChildRef> {
    boolean removeParents(Long productId);

    boolean removeChildren(Long productId);

    boolean removeProductDirection(Long productId);
}
