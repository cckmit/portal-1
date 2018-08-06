package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.DevUnitChildRef;

public interface DevUnitChildRefDAO extends PortalBaseDAO<DevUnitChildRef> {

    boolean removeByParentId(Long parentId);

    boolean removeByChildId(Long childId);
}
