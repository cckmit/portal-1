package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.DevUnitChildRefDAO;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnitChildRef;

public class DevUnitChildRefDAO_Impl extends PortalBaseJdbcDAO<DevUnitChildRef> implements DevUnitChildRefDAO {
    @Override
    public int removeParents(Long productId) {
        return removeByCondition("CHILD_ID = ? AND DUNIT_ID IN (SELECT ID FROM dev_unit WHERE UTYPE_ID != ?)", productId, En_DevUnitType.DIRECTION.getId());
    }

    @Override
    public int removeChildren(Long productId) {
        return removeByCondition("DUNIT_ID = ?", productId);
    }

    @Override
    public int removeProductDirection(Long productId) {
        return removeByCondition("CHILD_ID = ? AND DUNIT_ID IN (SELECT ID FROM dev_unit WHERE UTYPE_ID = ?)", productId, En_DevUnitType.DIRECTION.getId());
    }
}
