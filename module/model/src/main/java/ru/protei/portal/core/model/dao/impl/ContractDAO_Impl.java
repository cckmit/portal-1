package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.lang3.StringUtils;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.ContractDAO;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.model.util.sqlcondition.Query;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.helper.DateRangeUtils.makeInterval;
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

    @Override
    public boolean mergeRefKey(Long contractId, String refKey) {
        Contract contract = new Contract();
        contract.setId(contractId);
        contract.setRefKey(refKey);
        return partialMerge(contract, "ref_key");
    }

    private JdbcQueryParameters buildJdbcQueryParameters(ContractQuery query) {

        JdbcQueryParameters parameters = new JdbcQueryParameters();
        SqlCondition where = createSqlCondition(query);
        parameters.withCondition(where.condition, where.args)
                .withDistinct(true)
                .withSort(TypeConverters.createSort(query))
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
                String inArg = HelperFunc.makeInArg(query.getTypes(), type -> String.valueOf(type.getId()));
                condition.append(" and contract.contract_type in ").append(inArg);
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

            if (CollectionUtils.isNotEmpty(query.getParentContractIds())) {
                String inArg = HelperFunc.makeInArg(query.getParentContractIds(), false);
                condition.append(" and contract.parent_contract_id in ").append(inArg);
            }

            if (CollectionUtils.isNotEmpty(query.getManagerIds())) {
                String inArg = HelperFunc.makeInArg(query.getManagerIds(), false);
                condition.append(" and (CO.MANAGER in ").append(inArg)
                        .append("or P.MANAGER in ").append(inArg)
                        .append(")");
            }

            if (query.getKind() != null) {
                String kindCondition = null;
                switch (query.getKind()) {
                    case RECEIPT: kindCondition = "IS NULL"; break;
                    case EXPENDITURE: kindCondition = "IS NOT NULL"; break;
                }
                condition.append(" and contract.parent_contract_id ").append(kindCondition);
            }

            if (query.getDateSigningRange() != null) {
                Interval interval = makeInterval(query.getDateSigningRange());
                if (interval != null) {
                    if (interval.from != null) {
                        condition.append(" and contract.date_signing >= ?");
                        args.add(interval.from);
                    }
                    if (interval.to != null) {
                        condition.append(" and contract.date_signing <= ?");
                        args.add(interval.to);
                    }
                }
            }

            if (query.getDateValidRange() != null) {
                Interval interval = makeInterval(query.getDateValidRange());
                if (interval != null) {
                    if (interval.from != null) {
                        condition.append(" and contract.date_valid >= ?");
                        args.add(interval.from);
                    }
                    if (interval.to != null) {
                        condition.append(" and contract.date_valid <= ?");
                        args.add(interval.to);
                    }
                }
            }
        }));
    }
}