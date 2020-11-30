package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.ProjectDAO;
import ru.protei.portal.core.model.dict.En_DateIntervalType;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.helper.HelperFunc;
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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.DateRangeUtils.makeInterval;
import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;

public class ProjectDAO_Impl extends PortalBaseJdbcDAO<Project> implements ProjectDAO {


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
        if (query.limit > 0) {
            parameters = parameters.withLimit(query.getLimit());
        }

        return parameters;
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(ProjectQuery query) {
        return new SqlCondition().build(((condition, args) -> {
            condition.append("CO.deleted = 0");

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

            if ( isNotEmpty(query.getStates()) ) {
                String inArg = HelperFunc.makeInArg(query.getStates(), state -> String.valueOf(state.getId()));
                condition.append(" and CO.state in ").append(inArg);
            }

            if ( isNotEmpty(query.getRegions()) ) {
                List<Long> regionIds = query.getRegions().stream()
                        .map(region -> region == null ? null : region.getId())
                        .collect(toList());

                if (regionIds.remove(null)) {
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

            if ( isNotEmpty(query.getHeadManagers()) ) {
                List<Long> headManagerIds = query.getHeadManagers().stream()
                        .map(headManager -> headManager == null ? null : headManager.getId())
                        .collect(toList());

                if (headManagerIds.remove(null)) {
                    condition.append(" and (CO.id not in (SELECT CASE_ID FROM case_member WHERE MEMBER_ROLE_ID = ?)");
                    args.add(En_DevUnitPersonRoleType.HEAD_MANAGER.getId());
                    if (!headManagerIds.isEmpty()) {
                        condition.append(" or CO.id in (SELECT CASE_ID FROM case_member WHERE MEMBER_ROLE_ID = ? and MEMBER_ID IN ")
                                .append(makeInArg(headManagerIds, false))
                                .append(")");
                        args.add(En_DevUnitPersonRoleType.HEAD_MANAGER.getId());
                    }
                    condition.append(")");
                } else {
                    condition.append(" and CO.id in (SELECT CASE_ID FROM case_member WHERE MEMBER_ROLE_ID = ? and MEMBER_ID IN ")
                            .append(makeInArg(headManagerIds, false))
                            .append(")");
                    args.add(En_DevUnitPersonRoleType.HEAD_MANAGER.getId());
                }
            }

            if ( isNotEmpty(query.getCaseMembers()) ) {
                List<Long> caseMemberIds = query.getCaseMembers().stream()
                        .map(member -> member == null ? null : member.getId())
                        .collect(toList());

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

            if ( isNotEmpty(query.getDirections()) ) {
                List<Long> productDirectionIds = query.getDirections().stream()
                        .map(directionInfo -> directionInfo == null ? null : directionInfo.id)
                        .collect(toList());

                if (productDirectionIds.remove(null)) {
                    condition.append(" and (product_id is null");
                    if (!productDirectionIds.isEmpty()) {
                        condition.append(" or product_id in ")
                                .append(makeInArg(productDirectionIds, false));
                    }
                    condition.append(")");
                } else {
                    condition.append(" and product_id in ")
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
                if (!query.getProductIds().remove(CrmConstants.Product.UNDEFINED) || !query.getProductIds().isEmpty()) {
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

            if (query.getPlatformIndependentProject() != null && query.getPlatformIndependentProject()) {
                condition.append(" and project.id NOT IN (SELECT platform.project_id FROM platform WHERE platform.project_id IS NOT NULL)");
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
        }));
    }
}
