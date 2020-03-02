package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.core.model.query.ReservedIpQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public interface SubnetDAO extends PortalBaseDAO<Subnet> {

    Subnet checkExistsByAddress(String address);

    @SqlConditionBuilder
    SqlCondition createSubnetSqlCondition(ReservedIpQuery query);
}