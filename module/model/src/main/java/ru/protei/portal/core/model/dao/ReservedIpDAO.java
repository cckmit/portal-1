package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.query.ReservedIpQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public interface ReservedIpDAO extends PortalBaseDAO<ReservedIp> {

    ReservedIp getReservedIpByAddress(String address);

    @SqlConditionBuilder
    SqlCondition createReservedIpSqlCondition(ReservedIpQuery query);
}