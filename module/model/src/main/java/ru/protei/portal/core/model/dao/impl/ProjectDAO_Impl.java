package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.ProjectDAO;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.dict.En_PersonRoleType;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcHelper;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.DateRangeUtils.makeInterval;
import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;

public class ProjectDAO_Impl extends PortalBaseJdbcDAO<Project> implements ProjectDAO {

    public static final String LEFT_OUTER_JOIN_PROJECT_TO_PRODUCT = " left outer join project_to_product ptp on ptp.project_id = CO.id";
    public static final String RIGHT_JOIN_PROJECT_TO_CONTRACT = " right join contract c on c.project_id = CO.id";
    public static final String LEFT_JOIN_CASE_COMMENT = " LEFT JOIN case_comment cc ON CO.id = cc.CASE_ID";

    @Override
    public Collection<Project> selectScheduledPauseTime( long greaterThanTime ) {
        ProjectQuery query = new ProjectQuery();

        query.setPauseDateGreaterThan(greaterThanTime);
        query.setDeleted(CaseObject.NOT_DELETED);

        return getProjects(query);
    }

    @Override
    public SearchResult<Project> getSearchResult(ProjectQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return getSearchResult(parameters);
    }

    @Override
    public int countByQuery(ProjectQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return JdbcHelper.getObjectsCount(this.getObjectMapper(), this.jdbcTemplate, parameters);
    }

    @Override
    public List<Project> getProjects(ProjectQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return getList(parameters);
    }

    private JdbcQueryParameters buildJdbcQueryParameters(ProjectQuery query) {

        JdbcQueryParameters parameters = new JdbcQueryParameters();
        SqlCondition where = createSqlCondition(query);
        parameters.withCondition(where.condition, where.args)
                .withDistinct(true)
                .withSort(TypeConverters.createSort(query))
                .withOffset(query.getOffset());

        String joins = "";
        if (isNotEmpty(query.getDirectionIds())) {
            joins += LEFT_OUTER_JOIN_PROJECT_TO_PRODUCT;
        }
        if (query.getCommentCreationRange() != null) {
            joins += LEFT_JOIN_CASE_COMMENT;
        }
        if (Objects.equals(query.getHasContract(), true)) {
            joins += RIGHT_JOIN_PROJECT_TO_CONTRACT;
        }
        if (StringUtils.isNotEmpty(joins)) {
            parameters.withDistinct(true);
            parameters.withJoins(joins);
        }

        if (query.limit > 0) {
            parameters = parameters.withLimit(query.getLimit());
        }

        return parameters;
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(ProjectQuery query) {
        return new SqlCondition().build(((condition, args) -> {
            condition.append("CO.deleted = 0");

            if (query.getIdSearch() != null) {
                condition.append(" and project.id LIKE ?" );
                args.add(HelperFunc.makeLikeArg(query.getIdSearch().toString(), true));
            }
            if (query.getSearchString() != null && !query.getSearchString().trim().isEmpty()) {
                Condition searchCondition = SqlQueryBuilder.condition()
                        .or( "CO.case_name" ).like( query.getSearchString() )
                        .or( "CO.case_name" ).like( query.getAlternativeSearchString() )
                        .or( "CO.info" ).like( query.getSearchString() )
                        .or( "CO.info" ).like( query.getAlternativeSearchString() );
                condition.append( " and (" )
                        .append( searchCondition.getSqlCondition() )
                        .append( ")" );
                args.addAll( searchCondition.getSqlParameters() );
            }

            if (isNotEmpty(query.getCaseIds())) {
                condition
                        .append(" and project.id in ")
                        .append(makeInArg(query.getCaseIds(), false));
            }

            if ( isNotEmpty(query.getStateIds()) ) {
                condition.append(" and CO.state in ").append(HelperFunc.makeInArg(query.getStateIds(), false));
            }

            if ( isNotEmpty(query.getRegionIds()) ) {
                Set<Long> regionIds = new HashSet<>(query.getRegionIds());

                if (regionIds.remove(CrmConstants.Region.UNDEFINED)) {
                    condition.append(" and (CO.id not in (SELECT CASE_ID FROM case_location)");
                    if (!regionIds.isEmpty()) {
                        condition.append(" or CO.id in (SELECT CASE_ID FROM case_location WHERE LOCATION_ID IN ")
                                .append(makeInArg(regionIds, false))
                                .append(")");
                    }
                    condition.append(")");
                } else {
                    condition.append(" and CO.id in (SELECT CASE_ID FROM case_location WHERE LOCATION_ID IN ")
                            .append(makeInArg(regionIds, false))
                            .append(")");
                }
            }

            if ( isNotEmpty(query.getHeadManagerIds()) ) {
                Set<Long> headManagerIds = new HashSet<>(query.getHeadManagerIds());

                if (headManagerIds.remove(null)) {
                    condition.append(" and (CO.id not in (SELECT CASE_ID FROM case_member WHERE MEMBER_ROLE_ID = ?)");
                    args.add(En_PersonRoleType.HEAD_MANAGER.getId());
                    if (!headManagerIds.isEmpty()) {
                        condition.append(" or CO.id in (SELECT CASE_ID FROM case_member WHERE MEMBER_ROLE_ID = ? and MEMBER_ID IN ")
                                .append(makeInArg(headManagerIds, false))
                                .append(")");
                        args.add(En_PersonRoleType.HEAD_MANAGER.getId());
                    }
                    condition.append(")");
                } else {
                    condition.append(" and CO.id in (SELECT CASE_ID FROM case_member WHERE MEMBER_ROLE_ID = ? and MEMBER_ID IN ")
                            .append(makeInArg(headManagerIds, false))
                            .append(")");
                    args.add(En_PersonRoleType.HEAD_MANAGER.getId());
                }
            }

            if ( isNotEmpty(query.getCaseMemberIds()) ) {
                Set<Long> caseMemberIds = new HashSet<>(query.getCaseMemberIds());

                if (caseMemberIds.remove(null)) {
                    condition.append(" and (CO.id not in (SELECT CASE_ID FROM case_member)");
                    if (!caseMemberIds.isEmpty()) {
                        condition.append(" or CO.id in (SELECT CASE_ID FROM case_member WHERE MEMBER_ID IN ")
                                .append(makeInArg(caseMemberIds, false))
                                .append(")");
                    }
                    condition.append(")");
                } else {
                    condition.append(" and CO.id in (SELECT CASE_ID FROM case_member WHERE MEMBER_ID IN ")
                            .append(makeInArg(caseMemberIds, false))
                            .append(")");
                }
            }

            if ( isNotEmpty(query.getDirectionIds()) ) {
                Set<Long> productDirectionIds = new HashSet<>(query.getDirectionIds());

                if (productDirectionIds.remove(CrmConstants.Product.UNDEFINED)) {
                    condition.append(" and (ptp.product_id is null");
                    if (!productDirectionIds.isEmpty()) {
                        condition.append(" or ptp.product_id in ")
                                .append(makeInArg(productDirectionIds, false));
                    }
                    condition.append(")");
                } else {
                    condition.append(" and ptp.product_id in ")
                            .append(makeInArg(productDirectionIds, false));
                }
            }

            if ( isNotEmpty(query.getDistrictIds()) ) {
                condition.append(" and CO.id in (SELECT case_location.case_id FROM case_location " +
                        "WHERE case_location.location_id in (SELECT location.id FROM location WHERE location.parent_id in " + makeInArg(query.getDistrictIds(), false) + "))");
            }

            if (query.getMemberId() != null) {
                condition.append(" and CO.id in (select case_id from case_member where member_id = ?)");
                args.add(query.getMemberId());
            }

            if (isNotEmpty(query.getProductIds())) {
                Set<Long> productIds = new HashSet<>(query.getProductIds());
                if (!productIds.remove(CrmConstants.Product.UNDEFINED) || !query.getProductIds().isEmpty()) {
                    condition.append(" and project.id in")
                            .append(" (select project_id from project_to_product where product_id in " + makeInArg(query.getProductIds(), false) + ")");
                }
            }

            if (query.getCustomerType() != null) {
                condition.append( " and customer_type = ?" );
                args.add(query.getCustomerType().getId());
            }

            Interval created = makeInterval(new DateRange(En_DateIntervalType.FIXED, query.getCreatedFrom(), query.getCreatedTo()));

            if ( created != null ) {
                if (created.from != null) {
                    condition.append( " and CO.created >= ?" );
                    args.add( created.from );
                }
                if (created.to != null) {
                    condition.append( " and CO.created < ?" );
                    args.add( created.to );
                }
            }

            if (isNotEmpty(query.getInitiatorCompanyIds())) {
                condition.append(" and initiator_company in ").append(makeInArg(query.getInitiatorCompanyIds(), false));
            }

            if (query.getPauseDateGreaterThan() != null) {
                condition.append(" and CO.pause_date > ").append(query.getPauseDateGreaterThan());
            }

            if (query.getDeleted() != null) {
                condition.append(" and CO.deleted = ").append(query.getDeleted());
            }

            if (isNotEmpty(query.getSubcontractorIds())) {
                condition.append(" and project.id in (select project_to_company.project_id from project_to_company where project_to_company.company_id in")
                        .append(makeInArg(query.getSubcontractorIds(), false)).append(")");
            }
            if (!isEmpty(query.getTechnicalSupportExpiresInDays())) {
                condition.append(
                        query.getTechnicalSupportExpiresInDays().stream().map(interval -> {
                            args.add(interval.getFrom());
                            args.add(interval.getTo());
                            return "(project.technical_support_validity >= ? and project.technical_support_validity < ?)";
                        }).collect(Collectors.joining(" or ", " and( ", " ) ")));
            }
            if (query.getActive()) {
                condition.append(" and (project.technical_support_validity >= ? or project.work_completion_date >= ?)");
                args.add(new Date());
                args.add(new Date());
            }

            if (query.getCommentCreationRange() != null) {
                Interval interval = makeInterval(query.getCommentCreationRange());
                if (interval.from != null) {
                    condition.append( " and cc.created >= ?" );
                    args.add(interval.from );
                }
                if (interval.to != null) {
                    condition.append( " and cc.created < ?" );
                    args.add( interval.to );
                }
            }
        }));
    }
}
