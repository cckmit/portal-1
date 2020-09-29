package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.lang3.StringUtils;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.ContractDAO;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ContractApiQuery;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.DateRangeUtils.makeInterval;
import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;
import static ru.protei.portal.core.model.util.ContractStateUtil.getClosedContractStates;
import static ru.protei.portal.core.model.util.ContractStateUtil.getOpenedContractStates;

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
    public List<Contract> getByApiQuery(ContractApiQuery apiQuery) {

        SqlCondition where = new SqlCondition().build(((condition, args) -> {
            condition.append("1=1");

            if (isNotEmpty(apiQuery.getRefKeys())) {
                String inArg = makeInArg(apiQuery.getRefKeys(), s -> "'" + s + "'");
                condition.append(" AND contract.ref_key IN ").append(inArg);
            }

            if (apiQuery.getOpenStateDate() != null) {
                condition.append(" AND contract.id IN (");
                  condition.append(" SELECT history.case_object_id FROM history WHERE 1=1");
                  condition.append(" AND history.case_object_id IN (SELECT contract_h.id FROM contract AS contract_h)");
                  condition.append(" AND ? BETWEEN");
                    condition.append(" (SELECT MIN(hd1.date) AS date FROM history AS hd1 WHERE hd1.new_id IN ").append(makeInArg(getOpenedContractStates(), s -> String.valueOf(s.getId()))).append(")");
                    condition.append(" AND");
                    condition.append(" (SELECT MAX(hd2.date) AS date FROM history AS hd2 WHERE hd2.new_id IN ").append(makeInArg(getClosedContractStates(), s -> String.valueOf(s.getId()))).append(")");
                condition.append(")");
                args.add(apiQuery.getOpenStateDate());
            }
        }));

        JdbcQueryParameters parameters = new JdbcQueryParameters()
            .withCondition(where.condition, where.args)
            .withDistinct(true);

        return getList(parameters);
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

            if (CollectionUtils.isNotEmpty(query.getCaseTagsIds())) {
                if (query.getCaseTagsIds().remove(CrmConstants.CaseTag.NOT_SPECIFIED)) {
                    condition.append(" and ( ");
                    condition.append("CO.id not in (select case_id from case_object_tag) ");
                    if (CollectionUtils.isNotEmpty(query.getCaseTagsIds())) {
                        condition.append("or CO.id in ");
                        condition.append("(select case_id from case_object_tag where tag_id in ").append(makeInArg(query.getCaseTagsIds(), false)).append(") ");
                    }
                    condition.append(") ");
                } else {
                    condition.append(" and CO.id in ");
                    condition.append("(select case_id from case_object_tag where tag_id in ").append(makeInArg(query.getCaseTagsIds(), false)).append(") ");
                }
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