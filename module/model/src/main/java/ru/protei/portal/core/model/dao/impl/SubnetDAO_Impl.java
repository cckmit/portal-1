package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.SubnetDAO;
import ru.protei.portal.core.model.ent.Subnet;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ReservedIpQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public class SubnetDAO_Impl extends PortalBaseJdbcDAO<Subnet> implements SubnetDAO {

    @Override
    public Subnet checkExistsByAddress(String address) {
        return getByCondition("address=?", address);
    }

    @SqlConditionBuilder
    public SqlCondition createSubnetSqlCondition(ReservedIpQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (HelperFunc.isLikeRequired(query.getSearchString())) {
                String arg = HelperFunc.makeLikeArg(query.getSearchString(), true);
                condition.append(" and (address like ? or comment like ?)");
                args.add(arg);
                args.add(arg);
            }
        });
    }
}
