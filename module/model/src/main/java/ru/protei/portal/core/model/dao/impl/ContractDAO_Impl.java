package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.lang3.StringUtils;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.ContractDAO;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.sqlcondition.Query;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class ContractDAO_Impl extends PortalBaseJdbcDAO<Contract> implements ContractDAO {

    @Override
    public SearchResult<Contract> getSearchResult(ContractQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return getSearchResult(parameters);
    }

    @Override
    public Contract getByIdAndManagerId(Long id, Long managerId) {
        return getByCondition("contract.id = ? AND CO.MANAGER = ?", id, managerId);
    }

    @Override
    public int countByQuery(ContractQuery query) {
        SqlCondition where = createSqlCondition(query);
        return getObjectsCount(where.condition, where.args);
    }

    @Override
    public List<Contract> getByProjectId(Long projectId) {
        return getListByCondition("contract.project_id = ?", projectId);
    }

    @Override
    public List<Contract> getByRefKeys(List<String> refKeys) {
        Query query = query().asCondition()
                .and("contract.ref_key").in(refKeys)
                .asQuery();
        return getListByCondition(query.buildSql(), query.args());
    }

    private JdbcQueryParameters buildJdbcQueryParameters(ContractQuery query) {

        JdbcQueryParameters parameters = new JdbcQueryParameters();
        SqlCondition where = createSqlCondition(query);
        parameters.withCondition(where.condition, where.args)
                .withDistinct(true)
                .withSort(TypeConverters.createSort(query, "CO"))
                .withOffset(query.getOffset());
        if (query.limit > 0) {
            parameters = parameters.withLimit(query.getLimit());
        }

        return parameters;
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

            if (CollectionUtils.isNotEmpty(query.getStates())) {
                String inArg = HelperFunc.makeInArg(query.getStates(), state -> String.valueOf(state.getId()));
                condition.append(" and CO.state in ").append(inArg);
            } else {
                condition.append(" and CO.state != ?");
                args.add(En_ContractState.CANCELLED.getId());
            }

            if (CollectionUtils.isNotEmpty(query.getTypes())) {
                // Filter by comma-separated value
                condition.append(" and (");
                condition.append(stream(query.getTypes())
                        .map(type -> "contract.contract_types REGEXP '(^|,)" + type.getId() + "(,|$)'")
                        .collect(Collectors.joining(" or ")));
                condition.append(")");
            }

            if (query.getDirectionId() != null) {
                condition.append(" and (CO.product_id = ? or P.product_id = ?)");
                args.add(query.getDirectionId());
                args.add(query.getDirectionId());
            }

            if (CollectionUtils.isNotEmpty(query.getContractorIds())) {
                String inArg = HelperFunc.makeInArg(query.getContractorIds(), false);
                condition.append(" and contractor_id in ").append(inArg);
            }

            if (CollectionUtils.isNotEmpty(query.getOrganizationIds())) {
                condition.append(" and contract.organization_id in ")
                        .append(HelperFunc.makeInArg(query.getOrganizationIds(), false));
            }

            if (CollectionUtils.isNotEmpty(query.getManagerIds())) {
                String inArg = HelperFunc.makeInArg(query.getManagerIds(), false);
                condition.append(" and (CO.MANAGER in ").append(inArg)
                        .append("or P.MANAGER in ").append(inArg)
                        .append(")");
            }
        }));
    }
}