package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.DevUnitChildRefDAO;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnitChildRef;

public class DevUnitChildRefDAO_Impl extends PortalBaseJdbcDAO<DevUnitChildRef> implements DevUnitChildRefDAO {
    @Override
    public boolean removeParents(Long productId) {
        return removeByCondition("CHILD_ID = ? AND DUNIT_ID IN (SELECT ID FROM dev_unit WHERE UTYPE_ID != ?)", productId, En_DevUnitType.DIRECTION.getId()) > 0;
    }

    @Override
    public boolean removeChildren(Long productId) {
        return removeByCondition("DUNIT_ID = ?", productId) > 0;
    }

    @Override
    public boolean removeProductDirection(Long productId) {
        return removeByCondition("CHILD_ID = ? AND DUNIT_ID IN (SELECT ID FROM dev_unit WHERE UTYPE_ID = ?)", productId, En_DevUnitType.DIRECTION.getId()) > 0;
    }
}
