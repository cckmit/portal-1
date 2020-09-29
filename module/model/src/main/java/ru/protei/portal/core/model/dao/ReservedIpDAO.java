package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.query.ReservedIpQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;
import java.util.Map;

public interface ReservedIpDAO extends PortalBaseDAO<ReservedIp> {

    List<ReservedIp> getReservedIpsByAddress(String address);

    List<ReservedIp> getReservedIpsBySubnetId(Long subnetId);

    Map<Long, Long> countBySubnetIds(List<Long> subnetIds);

    @SqlConditionBuilder
    SqlCondition createReservedIpSqlCondition(ReservedIpQuery query);
}
