package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.RFIDDeviceDAO;
import ru.protei.portal.core.model.ent.RFIDDevice;

public class RFIDDeviceDAO_Impl extends PortalBaseJdbcDAO<RFIDDevice> implements RFIDDeviceDAO {
    @Override
    public RFIDDevice getByReaderId(String readerId) {
        return getByCondition(RFIDDevice.Columns.READER_ID + " = ?", readerId);
    }
}
