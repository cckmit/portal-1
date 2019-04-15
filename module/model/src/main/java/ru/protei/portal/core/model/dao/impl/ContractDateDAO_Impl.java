package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.ContractDateDAO;
import ru.protei.portal.core.model.ent.ContractDate;

import java.util.Date;
import java.util.List;

public class ContractDateDAO_Impl extends PortalBaseJdbcDAO<ContractDate> implements ContractDateDAO {

    @Override
    public List<ContractDate> getNotifyBetweenDates(Date from, Date to) {
        return getListByCondition("notify is true and date between ? and ?", from, to);
    }
}
