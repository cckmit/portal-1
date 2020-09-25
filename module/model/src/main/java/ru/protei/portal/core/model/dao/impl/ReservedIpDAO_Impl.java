package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.ReservedIpDAO;
import ru.protei.portal.core.model.ent.ReservedIp;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ReservedIpQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservedIpDAO_Impl extends PortalBaseJdbcDAO<ReservedIp> implements ReservedIpDAO {

    @Override
    public List<ReservedIp> getReservedIpsByAddress(String address) {
        return getListByCondition("ip_address=?", address);
    }

    @Override
    public List<ReservedIp> getReservedIpsBySubnetId(Long subnetId) { return getListByCondition("subnet_id=?", subnetId); }

    @Override
    public Map<Long, Long> countBySubnetIds(List<Long> subnetIds) {
        StringBuilder sb = new StringBuilder()
                .append("SELECT subnet_id, COUNT(*) AS cnt FROM ")
                .append(getTableName());

        if (CollectionUtils.isNotEmpty(subnetIds)) {
            sb.append(" WHERE subnet_id IN ").append(HelperFunc.makeInArg(subnetIds, String::valueOf));
        }

        sb.append(" GROUP BY subnet_id");
        Map<Long, Long> result = new HashMap<>();
        jdbcTemplate.query(sb.toString(), (rs, rowNum) -> {
            long id = rs.getLong("subnet_id");
            long count = rs.getLong("cnt");
            result.put(id, count);
            return null;
        });
        return result;
    }

    @SqlConditionBuilder
    public SqlCondition createReservedIpSqlCondition(ReservedIpQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (HelperFunc.isLikeRequired(query.getSearchString())) {
                String arg = HelperFunc.makeLikeArg(query.getSearchString(), true);
                condition.append(" and (ip_address like ? or mac_address like ? or ")
                         .append(getTableName())
                         .append(".comment like ? )");
                args.add(arg);
                args.add(arg);
                args.add(arg);
            }

            if (query.getReservedFrom() != null) {
                condition.append(" and reserve_date >= ?");
                args.add(query.getReservedFrom());
            }

            if (query.getReservedTo() != null) {
                condition.append(" and reserve_date <= ?");
                args.add(query.getReservedTo());
            }

            if (query.getReleasedFrom() != null) {
                condition.append(" and release_date >= ?");
                args.add(query.getReleasedFrom());
            }

            if (query.getReleasedTo() != null) {
                condition.append(" and release_date <= ?");
                args.add(query.getReleasedTo());
            }

            if (query.getLastActiveFrom() != null) {
                condition.append(" and last_check_date >= ?");
                args.add(query.getLastActiveFrom());
                /*
                   @todo условие успешной проверки по полю lastCheckInfo
                 */
            }

            if (query.getLastActiveTo() != null) {
                condition.append(" and last_check_date <= ?");
                args.add(query.getLastActiveTo());
                /*
                   @todo условие успешной проверки по полю lastCheckInfo
                 */
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
