package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.RFIDLabelDAO;
import ru.protei.portal.core.model.ent.RFIDLabel;

public class RFIDLabelDAO_Impl extends PortalBaseJdbcDAO<RFIDLabel> implements RFIDLabelDAO {

    @Override
    public RFIDLabel getByEPC(String epc) {
        return getByCondition(RFIDLabel.Columns.EPC + " = ?", epc);
    }
}
