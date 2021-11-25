package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.RFIDDevice;

public interface RFIDDeviceDAO extends PortalBaseDAO<RFIDDevice> {
    RFIDDevice getByReaderId(String readerId);
}