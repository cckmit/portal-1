package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.DeliveryDetailDAO;
import ru.protei.portal.core.model.ent.DeliveryDetail;

public class DeliveryDetailDAO_Impl extends PortalBaseJdbcDAO<DeliveryDetail> implements DeliveryDetailDAO {
    @Override
    public DeliveryDetail getByName(String value) {
        return getByCondition("lower(" + DeliveryDetail.Columns.NAME + ") = ?", value.trim().toLowerCase());
    }
}
