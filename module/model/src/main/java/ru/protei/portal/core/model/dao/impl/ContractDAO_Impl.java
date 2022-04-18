package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.lang3.StringUtils;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.ContractDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.helper.HelperFunc;
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
import static ru.protei.portal.core.model.helper.HelperFunc.makeLikeArg;
import static ru.protei.portal.core.model.util.ContractStateUtil.getOpenedContractStates;

public class ContractDAO_Impl extends PortalBaseJdbcDAO<Contract> implements ContractDAO {

    public static final String LEFT_OUTER_JOIN_PROJECT_TO_PRODUCT = " left outer join project_to_product ptp on ptp.project_id = contract.project_id";

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
        if (CollectionUtils.isNotEmpty(query.getDirectionIds())) {
            return getObjectsCount(where.condition, where.args, LEFT_OUTER_JOIN_PROJECT_TO_PRODUCT, true);
        }
        return getObjectsCount(where.condition, where.args);
    }

    @Override
    public List<Contract> getByProjectId(Long projectId) {
        return getListByCondition("contract.project_id = ?", projectId);
    }

    @Override
    public boolean mergeRefKey(Long contractId, String refKey) {
        Contract contract = new Contract();
        contract.setId(contractId);
        contract.setRefKey(refKey);
        return partialMerge(contract, "ref_key");
    }

    @Override
    public List<Contract> getByCustomerAndProject(String customerName) {
        JdbcQueryParameters parameters = new JdbcQueryParameters();
        parameters.withJoins("inner join case_object co_contract on contract.id = co_contract.id " +
                        "   inner join case_object co_project on co_project.ID = contract.project_id" +
                        "   inner join company on company.id = co_project.initiator_company")
                .withCondition("company.cname = ? and co_project.CASE_TYPE = " + En_CaseType.PROJECT.getId(), customerName)
                .withDistinct(true);

        return getList(parameters);
    }

    @Override
    public List<Contract> getByPlatformId(Long platformId) {
        JdbcQueryParameters parameters = new JdbcQueryParameters();
        parameters.withJoins("join platform on platform.project_id = contract.project_id")
                .withCondition("platform.id = ?", platformId);
        return getList(parameters);
    }

    private JdbcQueryParameters buildJdbcQueryParameters(ContractQuery query) {

        JdbcQueryParameters parameters = new JdbcQueryParameters();
        SqlCondition where = createSqlCondition(query);
        parameters.withCondition(where.condition, where.args)
                .withDistinct(true)
                .withSort(TypeConverters.createSort(query))
                .withOffset(query.getOffset());

        if (CollectionUtils.isNotEmpty(query.getDirectionIds())) {
            parameters.withDistinct(true);
            parameters.withJoins(LEFT_OUTER_JOIN_PROJECT_TO_PRODUCT);
        }

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

            if (StringUtils.isNotEmpty(query.getDeliveryNumber())) {
                condition.append(" and contract.delivery_number like ?");
                args.add(makeLikeArg(query.getDeliveryNumber(), true));
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

            if (CollectionUtils.isNotEmpty(query.getDirectionIds())) {
                condition.append(" and (ptp.product_id in ")
                        .append(makeInArg(query.getDirectionIds(), false))
                        .append(")");
            }


            if (CollectionUtils.isNotEmpty(query.getContractorIds())) {
                String inArg = HelperFunc.makeInArg(query.getContractorIds(), false);
                condition.append(" and contractor_id in ").append(inArg);
            }

            if (CollectionUtils.isNotEmpty(query.getCuratorIds())) {
                String inArg = HelperFunc.makeInArg(query.getCuratorIds(), false);
                condition.append(" and CO.initiator in ").append(inArg);
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

            if (isNotEmpty(query.getRefKeys())) {
                String inArg = makeInArg(query.getRefKeys(), s -> "'" + s + "'");
                condition.append(" AND contract.ref_key IN ").append(inArg);
            }

            if (query.getOpenStateDate() != null) {
                condition.append(" AND contract.id IN (");
                condition.append(" SELECT DISTINCT hww.case_object_id FROM (");
                condition.append(" SELECT");
                condition.append(" hw.*,");
                condition.append(" ROW_NUMBER() OVER (PARTITION BY hw.case_object_id ORDER BY hw.date DESC) AS rownumber");
                condition.append(" FROM history AS hw WHERE 1=1");
                condition.append(" AND hw.case_object_id IN (SELECT ch.id FROM contract AS ch)");
                condition.append(" AND hw.date <= ?");
                condition.append(" ) hww");
                condition.append(" WHERE 1=1");
                condition.append(" AND hww.rownumber = 1");
                condition.append(" AND hww.new_id IN ").append(makeInArg(getOpenedContractStates(), s -> String.valueOf(s.getId())));
                condition.append(")");
                args.add(query.getOpenStateDate());
            }

            if (query.getProjectId() != null) {
                condition.append(" and contract.project_id = ?");
                args.add(query.getProjectId());
            }
        }));
    }
}