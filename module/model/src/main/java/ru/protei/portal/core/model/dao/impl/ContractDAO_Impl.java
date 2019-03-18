package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.lang3.StringUtils;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.ContractDAO;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcBaseDAO;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

public class ContractDAO_Impl extends JdbcBaseDAO<Long, Contract> implements ContractDAO {

    @Override
    public List<Contract> getListByQuery(ContractQuery query) {
        SqlCondition where = createSqlCondition(query);
        JdbcQueryParameters queryParameters = new JdbcQueryParameters();

        queryParameters.withCondition(where.condition, where.args)
                .withDistinct(true)
                .withSort(TypeConverters.createSort(query, "CO"))
                .withOffset(query.getOffset());
        if (query.limit > 0) {
            queryParameters = queryParameters.withLimit(query.getLimit());
        }
        return getList(queryParameters);
    }

    @Override
    public int countByQuery(ContractQuery query) {
        SqlCondition where = createSqlCondition(query);
        return getObjectsCount(where.condition, where.args);
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(ContractQuery query) {
        return new SqlCondition().build(((condition, args) -> {
            condition.append("1=1");

            if (StringUtils.isNotEmpty(query.getSearchString())) {
                condition.append(" and (CO.CASE_NAME like ? or CO.INFO like ?)");
                String likeArg = HelperFunc.makeLikeArg(query.getSearchString(), true);
                args.add(likeArg);
                args.add(likeArg);
            }

            if (query.getState() != null) {
                condition.append(" and CO.state = ?");
                args.add(query.getState().getId());
            }

            if (query.getType() != null) {
                condition.append(" and contract.contract_type = ?");
                args.add(query.getType().ordinal());
            }

            if (query.getDirectionId() != null) {
                condition.append(" and CO.product_id = ?");
                args.add(query.getDirectionId());
            }

            if (CollectionUtils.isNotEmpty(query.getContragentIds())) {
                condition.append(" and CO.initiator_company in ")
                        .append(HelperFunc.makeInArg(query.getContragentIds(), false));
            }

            if (CollectionUtils.isNotEmpty(query.getOrganizationIds())) {
                condition.append(" and contract.organization_id in ")
                        .append(HelperFunc.makeInArg(query.getOrganizationIds(), false));
            }

            if (CollectionUtils.isNotEmpty(query.getManagerIds())) {
                condition.append(" and CO.MANAGER in ")
                        .append(HelperFunc.makeInArg(query.getManagerIds(), false));
            }
        }));
    }
}