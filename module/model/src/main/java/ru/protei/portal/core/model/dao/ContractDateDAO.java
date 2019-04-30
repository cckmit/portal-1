package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.ContractDate;

import java.util.Date;
import java.util.List;

public interface ContractDateDAO extends PortalBaseDAO<ContractDate> {

    List<ContractDate> getNotifyBetweenDates(Date from, Date to);
}
