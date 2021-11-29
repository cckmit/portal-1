package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.RFIDLabel;

public interface RFIDLabelDAO extends PortalBaseDAO<RFIDLabel> {
    RFIDLabel getByEPC(String epc);
}