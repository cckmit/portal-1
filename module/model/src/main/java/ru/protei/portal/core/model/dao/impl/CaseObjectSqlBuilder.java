package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder;

import java.util.*;

import static ru.protei.portal.core.model.dao.impl.CaseShortViewDAO_Impl.isFilterByTagNames;
import static ru.protei.portal.core.model.dao.impl.CaseShortViewDAO_Impl.isSearchAtComments;
import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.DateRangeUtils.makeInterval;
import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.condition;

public class CaseObjectSqlBuilder {

    public SqlCondition caseCommonQuery (CaseQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("deleted = 0");

            // TODO merge ids to use queries simultaneously
            if ( query.getId() != null ) {
                condition.append( " and case_object.id=?" );
                args.add( query.getId() );
            } else if (isNotEmpty(query.getCaseTagsIds())) {
                if (query.getCaseTagsIds().remove(CrmConstants.CaseTag.NOT_SPECIFIED)) {
                    if (query.isCustomerSearch()) {
                        condition.append(" and (case_object.id not in (select case_id from case_object_tag where case_object_tag.tag_id in")
                                .append(" (select case_tag.id from case_tag where case_tag.company_id in " + makeInArg(query.getCompanyIds(), false) + "))");
                    } else {
                        condition.append(" and (case_object.id not in (select case_id from case_object_tag)");
                    }
                    if (!query.getCaseTagsIds().isEmpty()) {
                        condition.append(" or case_object.id in")
                                .append(" (select case_id from case_object_tag where tag_id in " + makeInArg(query.getCaseTagsIds(), false) + ")");
                    }
                    condition.append(")");
                } else {
                    condition.append(" and case_object.id in")
                            .append(" (select case_id from case_object_tag where tag_id in " + makeInArg(query.getCaseTagsIds(), false) + ")");
                }
            }

            if (isFilterByTagNames(query)) {
                if (query.getPlanId() == null) {
                    condition.append(" and case_tag.name in" + makeInArg(query.getCaseTagsNames(), true));
                } else {
                    condition.append(" and case_object.id in (")
                            .append("select case_id from case_object_tag join case_tag on case_tag.id = case_object_tag.tag_id where case_tag.name in ")
                            .append(makeInArg(query.getCaseTagsNames(), true)).append(")");
                }
            }

            if ( !query.isAllowViewPrivate() ) {
                condition.append( " and case_object.private_flag=?" );
                args.add( 0 );
            } else if (query.isViewPrivate() != null) {
                condition.append( " and case_object.private_flag=?" );
                args.add( query.isViewPrivate() ? 1 : 0 );
            }

            if ( query.getType() != null ) {
                condition.append( " and case_object.case_type=?" );
                args.add( query.getType().getId() );
            }

            if ( query.getCaseNumbers() != null && !query.getCaseNumbers().isEmpty() ) {
                condition.append(" and caseno in " + makeInArg(query.getCaseNumbers(), false));
            }

            if (isNotEmpty(query.getCompanyIds())) {
                condition.append(" and initiator_company in " + makeInArg(query.getCompanyIds(), false));
            }

            if (isNotEmpty(query.getInitiatorIds())) {
                condition.append(" and initiator in " + makeInArg(query.getInitiatorIds(), false));
            }

            if (isNotEmpty(query.getPlatformIds())) {
                condition.append(" and platform_id in " + makeInArg(query.getPlatformIds(), false));
            }

            if (isNotEmpty(query.getManagerCompanyIds())) {
                condition.append(" and manager_company_id in " + makeInArg(query.getManagerCompanyIds(), false));
            }

            if (isNotEmpty(query.getManagerIds())) {
                List<Long> managerIds = new ArrayList<>(query.getManagerIds());
                boolean isWithoutManager = managerIds.remove(CrmConstants.Employee.UNDEFINED);
                boolean isGroupManager = managerIds.remove(CrmConstants.Employee.GROUP_MANAGER);

                if (!isWithoutManager && !managerIds.isEmpty()) {
                    condition.append(" and manager IN ")
                             .append(makeInArg(managerIds, false));
                } else if (managerIds.isEmpty()) {
                    if (isGroupManager) {
                        condition.append(" and (manager IS NULL or (SELECT person.sex FROM person WHERE person.id = manager) = ?)");
                        args.add(En_Gender.UNDEFINED.getCode());
                    } else {
                        condition.append(" and manager IS NULL");
                    }
                } else {
                    condition.append(" and (manager IN ")
                             .append(makeInArg(managerIds, false))
                             .append(" or manager IS NULL)");
                }
            }

            if (isNotEmpty(query.getProductIds())) {
                if (query.getProductIds().remove(CrmConstants.Product.UNDEFINED)) {
                    condition.append(" and (product_id is null");
                    if (!query.getProductIds().isEmpty()) {
                        condition.append(" or product_id in " + makeInArg(query.getProductIds(), false));
                    }
                    condition.append(")");
                } else {
                    condition.append(" and product_id in " + makeInArg(query.getProductIds(), false));
                }
            }

            if ( isNotEmpty(query.getLocationIds()) ) {
                condition.append(" and case_object.id in (SELECT case_location.case_id FROM case_location " +
                        "WHERE case_location.location_id in " + makeInArg(query.getLocationIds(), false) + ")");
            }
            else if ( isNotEmpty(query.getDistrictIds()) ) {
                condition.append(" and case_object.id in (SELECT case_location.case_id FROM case_location " +
                        "WHERE case_location.location_id in (SELECT location.id FROM location WHERE location.parent_id in " + makeInArg(query.getDistrictIds(), false) + "))");
            }

            if ( isNotEmpty(query.getStateIds()) ) {
                condition.append(" and case_object.state in " + makeInArg(query.getStateIds(), false));
            }

            if ( isNotEmpty(query.getImportanceIds()) ) {
                String importantces = makeInArg( query.getImportanceIds(), false );
                if (query.isCheckImportanceHistory() == null || !query.isCheckImportanceHistory()) {
                    condition.append( " and importance in " ).append( importantces );
                } else {
                    if (query.getPlanId() == null) {
                        condition.append( " and (importance in " ).append( importantces )
                                .append(" or (history.new_id in " ).append( importantces )
                                    .append(" and history.value_type = ").append(En_HistoryType.CASE_IMPORTANCE.getId())
                                    .append(" and history.action_type in ")
                                    .append(makeInArg(Arrays.asList(En_HistoryAction.ADD.getId(), En_HistoryAction.CHANGE.getId()), false))
                                    .append(")")
                                .append( ")" );
                    } else {
                        condition.append( " and (importance in " ).append( importantces )
                                .append(" or case_object.id in (").append("select case_object_id from history " +
                                    "where new_id in ").append(makeInArg(query.getImportanceIds(), false))
                                    .append(" and value_type = ").append(En_HistoryType.CASE_IMPORTANCE.getId())
                                    .append(" and action_type in ").append(makeInArg(Arrays.asList(En_HistoryAction.ADD.getId(), En_HistoryAction.CHANGE.getId()), false))
                                    .append(")")
                                .append( ")" );;
                    }
                }
            }

            Interval created = makeInterval(query.getCreatedRange());

            if ( created != null ) {
                if (created.from != null) {
                    condition.append( " and case_object.created >= ?" );
                    args.add( created.from );
                }
                if (created.to != null) {
                    condition.append( " and case_object.created < ?" );
                    args.add( created.to );
                }
            }

            Interval modified = makeInterval(query.getModifiedRange());

            if ( modified != null ) {
                if (modified.from != null) {
                    condition.append( " and case_object.modified >= ?" );
                    args.add( modified.from );
                }
                if (modified.to != null) {
                    condition.append( " and case_object.modified < ?" );
                    args.add( modified.to );
                }
            }

            if (query.getSearchString() != null && !query.getSearchString().trim().isEmpty()) {
                Condition searchCondition = condition()
                        .or( "case_name" ).like( query.getSearchString() )
                        .or( "case_name" ).like( query.getAlternativeSearchString() )
                        .or( "case_object.info" ).like( query.getSearchString() )
                        .or( "case_object.info" ).like( query.getAlternativeSearchString() );
                if (isSearchAtComments( query )) {
                    if (query.getPlanId() == null) {
                        searchCondition
                                .or( "case_comment.comment_text" ).like( query.getSearchString() )
                                .or( "case_comment.comment_text" ).like( query.getAlternativeSearchString() );
                    } else {
                        searchCondition
                                .or("case_object.id").in(SqlQueryBuilder.query().select("CASE_ID").from("case_comment")
                                    .where("case_comment.comment_text").like(query.getSearchString())
                                        .or("case_comment.comment_text").like(query.getAlternativeSearchString()).asQuery());
                    }
                }
                condition.append( " and (" )
                        .append( searchCondition.getSqlCondition() )
                        .append( ")" );
                args.addAll( searchCondition.getSqlParameters() );
            }

            if (query.getSearchCasenoString() != null && !query.getSearchCasenoString().isEmpty()) {
                condition.append(" and caseno like ?");
                args.add(HelperFunc.makeLikeArg(query.getSearchCasenoString(), true));
            }

            if (isNotEmpty(query.getCreatorIds())) {
                condition
                        .append(" and case_object.CREATOR in ")
                        .append(makeInArg(query.getCreatorIds(), false));
            }

            if (isNotEmpty(query.getCaseIds())) {
                condition
                        .append(" and case_object.id in ")
                        .append(makeInArg(query.getCaseIds(), false));
            }

            if (query.getPlanId() != null) {
                condition.append(" and plan_id = ?");
                args.add(query.getPlanId());
            }

            if (query.getPersonIdToIsFavorite() != null &&
                    query.getPersonIdToIsFavorite().getA() != null &&
                    query.getPersonIdToIsFavorite().getB() != null) {

                String inCondition = query.getPersonIdToIsFavorite().getB() ? "IN" : "NOT IN";

                condition
                        .append(" and case_object.id ")
                        .append(inCondition)
                        .append(" (SELECT case_object_id FROM person_favorite_issues WHERE person_id = ?)");

                args.add(query.getPersonIdToIsFavorite().getA());
            }

            if (CollectionUtils.isNotEmpty(query.getTimeElapsedTypeIds())) {
                List<String> orConditions = new ArrayList<>();

                condition.append(" and (");

                if (query.getTimeElapsedTypeIds().contains(En_TimeElapsedType.NONE.getId())) {
                    orConditions.add("case_comment.time_elapsed_type IS NULL and case_comment.time_elapsed IS NOT NULL");
                }

                if (isNotEmpty(query.getTimeElapsedTypeIds())) {
                    orConditions.add("case_comment.time_elapsed_type IN " + HelperFunc.makeInArg(query.getTimeElapsedTypeIds(), false));
                }

                condition
                        .append(String.join(" or ", orConditions))
                        .append(")");
            }

            if (CollectionUtils.isNotEmpty(query.getWorkTriggersIds())) {
                List<String> orConditions = new ArrayList<>();

                condition.append(" and (");

                if (query.getWorkTriggersIds().contains(En_WorkTrigger.NONE.getId())) {
                    orConditions.add("case_object.work_trigger IS NULL");
                }

                if (isNotEmpty(query.getWorkTriggersIds())) {
                    orConditions.add("case_object.work_trigger IN " + HelperFunc.makeInArg(query.getWorkTriggersIds(), false));
                }

                condition
                        .append(String.join(" or ", orConditions))
                        .append(")");
            }

            if (query.getOverdueDeadlines() != null) {
                condition.append(" and case_object.deadline " + (query.getOverdueDeadlines()? "<=" : ">") +" ?");
                args.add(new Date().getTime());
            }

            if (query.getAutoClose() != null) {
                condition.append(" and case_object.auto_close = ?");
                args.add(query.getAutoClose());
            }
        });
    }
}
