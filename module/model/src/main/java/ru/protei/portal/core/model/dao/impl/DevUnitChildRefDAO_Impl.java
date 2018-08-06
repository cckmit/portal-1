package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.DevUnitChildRefDAO;
import ru.protei.portal.core.model.ent.DevUnitChildRef;

public class DevUnitChildRefDAO_Impl extends PortalBaseJdbcDAO<DevUnitChildRef> implements DevUnitChildRefDAO {

    @Override
    public boolean removeByParentId(Long parentId) {
        return removeByCondition("DUNIT_ID = ?", parentId) > 0;
    }

    @Override
    public boolean removeByChildId(Long childId) {
        return removeByCondition("CHILD_ID = ?", childId) > 0;
    }
}
