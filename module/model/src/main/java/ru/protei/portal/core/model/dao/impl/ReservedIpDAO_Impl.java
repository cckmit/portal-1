package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.ReservedIpDAO;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ReservedIpQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcHelper;

public class ReservedIpDAO_Impl extends PortalBaseJdbcDAO<ReservedIp> implements ReservedIpDAO {

    @Override
    public ReservedIp getReservedIpByAddress(String address) { return getByCondition("ip_address=?", address); }

    @SqlConditionBuilder
    public SqlCondition createReservedIpSqlCondition(ReservedIpQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (HelperFunc.isLikeRequired(query.getSearchString())) {
                String arg = HelperFunc.makeLikeArg(query.getSearchString(), true);
                condition.append(" and (ip_address like ? or mac_address like ?)");
                args.add(arg);
                args.add(arg);
            }

            if (query.getReservedFrom() != null) {
                condition.append(" and reserve_date >= ?");
                args.add(query.getReservedFrom());
            }

            if (query.getReservedTo() != null) {
                condition.append(" and reserve_date >= ?");
                args.add(query.getReservedTo());
            }

            if (query.getReleasedFrom() != null) {
                condition.append(" and release_date >= ?");
                args.add(query.getReleasedFrom());
            }

            if (query.getReleasedTo() != null) {
                condition.append(" and release_date >= ?");
                args.add(query.getReleasedTo());
            }

            if (CollectionUtils.isNotEmpty(query.getOwnerIds())) {
                condition.append(" and owner_id in ")
                        .append(JdbcHelper.makeSqlStringCollection(query.getOwnerIds(), args, null));
            }

            if (CollectionUtils.isNotEmpty(query.getSubnetIds())) {
                condition.append(" and subnet_id in ")
                        .append(JdbcHelper.makeSqlStringCollection(query.getSubnetIds(), args, null));
            }
        });
    }
}
