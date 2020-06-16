package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.core.model.util.CrmConstants;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ru.protei.portal.core.model.dao.impl.CaseShortViewDAO_Impl.isSearchAtComments;
import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;

public class CaseObjectSqlBuilder {

    public SqlCondition caseCommonQuery (CaseQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("deleted = 0");

            // TODO merge ids to use queries simultaneously
            if ( query.getId() != null ) {
                condition.append( " and case_object.id=?" );
                args.add( query.getId() );
            } else if (query.getMemberId() != null) {
                condition.append(" and (case_object.id in (select case_id from case_member where member_id = ").append(query.getMemberId()).append(")");
                condition.append(" or case_object.creator = ").append(query.getMemberId()).append(")");
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

            if ( !query.isAllowViewPrivate() ) {
                condition.append( " and case_object.private_flag=?" );
                args.add( 0 );
            } else if (query.isViewPrivate() != null) {
                condition.append( " and case_object.private_flag=?" );
                args.add( query.isViewPrivate() ? 1 : 0 );
            }

            if ( query.getType() != null ) {
                condition.append( " and case_type=?" );
                args.add( query.getType().getId() );
            }

            if ( query.getCaseNumbers() != null && !query.getCaseNumbers().isEmpty() ) {
                condition.append(" and caseno in " + makeInArg(query.getCaseNumbers(), false));
            }

            boolean isInitiatorCompaniesNotEmpty = isNotEmpty(query.getCompanyIds());

            if (isInitiatorCompaniesNotEmpty) {
                condition.append(" and (initiator_company in " + makeInArg(query.getCompanyIds(), false));

                if (isNotEmpty(query.getInitiatorIds())) {
                    condition.append(" and initiator in " + makeInArg(query.getInitiatorIds(), false));
                }
            }

            if (isNotEmpty(query.getManagerCompanyIds())) {
                String logicOperator = isInitiatorCompaniesNotEmpty && Boolean.TRUE.equals(query.getManagerOrInitiatorCondition()) ? " or " : " and ";

                condition.append(logicOperator + "manager_company_id in " + makeInArg(query.getManagerCompanyIds(), false));

                if (isNotEmpty(query.getManagerIds())) {
                    List<Long> managerIds = new ArrayList<>(query.getManagerIds());
                    boolean isWithoutManager = managerIds.remove(CrmConstants.Employee.UNDEFINED);

                    if (!isWithoutManager) {
                        condition
                                .append(" and manager IN ")
                                .append(makeInArg(managerIds, false));
                    } else if (managerIds.isEmpty()) {
                        condition.append(" and (manager IS NULL or (SELECT person.sex FROM person WHERE person.id = manager) = ?)");
                        args.add(En_Gender.UNDEFINED.getCode());
                    } else {
                        condition
                                .append(" and (manager IN ")
                                .append(makeInArg(managerIds, false))
                                .append(" or manager IS NULL or (SELECT person.sex FROM person WHERE person.id = manager) = ?)");
                        args.add(En_Gender.UNDEFINED.getCode());
                    }
                }
            }

            condition.append(isInitiatorCompaniesNotEmpty ? ")" : "");

            if (isNotEmpty(query.getProductIds())) {
                if (query.getType() != null && query.getType().equals(En_CaseType.PROJECT)) {
                    if (!query.getProductIds().remove(CrmConstants.Product.UNDEFINED) || !query.getProductIds().isEmpty()) {
                        condition.append(" and case_object.id in")
                                .append(" (select project_id from project_to_product where product_id in " + makeInArg(query.getProductIds(), false) + ")");
                    }
                } else {
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
            }

            if ( query.getLocationIds() != null && !query.getLocationIds().isEmpty() ) {
                condition.append(" and case_object.id in (SELECT case_location.case_id FROM case_location " +
                        "WHERE case_location.location_id in " + makeInArg(query.getLocationIds(), false) + ")");
            }
            else if ( query.getDistrictIds() != null && !query.getDistrictIds().isEmpty() ) {
                condition.append(" and case_object.id in (SELECT case_location.case_id FROM case_location " +
                        "WHERE case_location.location_id in (SELECT location.id FROM location WHERE location.parent_id in " + makeInArg(query.getDistrictIds(), false) + "))");
            }

            if ( query.getStateIds() != null && !query.getStateIds().isEmpty() ) {
                condition.append(" and case_object.state in " + makeInArg(query.getStateIds(), false));
            }

            if ( query.getImportanceIds() != null && !query.getImportanceIds().isEmpty() ) {
                String importantces = makeInArg( query.getImportanceIds(), false );
                if (query.isCheckImportanceHistory() == null || !query.isCheckImportanceHistory()) {
                    condition.append( " and importance in " ).append( importantces );
                } else {
                    condition.append( " and (importance in " ).append( importantces )
                            .append( " or case_comment.cimp_level in " ).append( importantces )
                            .append( ")" );
                }
            }

            Interval created = createInterval(query.getCreatedRange());

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

            Interval modified = createInterval(query.getModifiedRange());

            if ( modified != null ) {
                if (modified.from != null) {
                    condition.append( " and case_object.modified >= ?" );
                    args.add( modified.from );
                }
                if (modified.to != null) {
                    condition.append( " and case_object.modified < ?" );
                    args.add( created.to );
                }
            }

            if (query.getSearchString() != null && !query.getSearchString().trim().isEmpty()) {
                condition.append( " and ( case_name like ? or case_object.info like ?");
                if (isSearchAtComments(query)) {
                    condition.append(" or case_comment.comment_text like ?");
                    args.add(HelperFunc.makeLikeArg(query.getSearchString(), true));
                }
                condition.append( ")" );
                String likeArg = HelperFunc.makeLikeArg(query.getSearchString(), true);
                args.add(likeArg);
                args.add(likeArg);
            }

            if (query.getSearchCasenoString() != null && !query.getSearchCasenoString().isEmpty()) {
                condition.append(" and caseno like ?");
                args.add(HelperFunc.makeLikeArg(query.getSearchCasenoString(), true));
            }

            if (query.isFindRecordByCaseComments()) {
                condition.append(" and case_object.id in (SELECT case_comment.case_id FROM case_comment " +
                        "WHERE 2=2");

                if ( modified != null ) {
                    if (modified.from != null) {
                        condition.append( " and case_comment.created >= ?" );
                        args.add( modified.from );
                    }
                    if (modified.to != null) {
                        condition.append( " and case_comment.created < ?" );
                        args.add( created.to );
                    }
                }

                if ( query.getStateIds() != null ) {
                    condition.append( " and case_comment.cstate_id in " + makeInArg(query.getStateIds()));
                }
                if ( query.getManagerIds() != null ) {
                    condition.append( " and manager in " +  makeInArg(query.getManagerIds()));
                }

                condition.append(")");
            }

            if (query.getLocal() != null) {
                condition.append( " and case_object.islocal = ?" );
                args.add(query.getLocal());
            }

            if (query.getPlatformIndependentProject() != null && query.getPlatformIndependentProject()) {
                condition.append(" and case_object.id NOT IN (SELECT platform.project_id FROM platform WHERE platform.project_id IS NOT NULL)");
            }

            if ( isNotEmpty(query.getProductDirectionIds()) ) {
                if (query.getProductDirectionIds().remove(null)) {
                    condition.append(" and (product_id is null");
                    if (!query.getProductDirectionIds().isEmpty()) {
                        condition.append(" or product_id in ")
                                .append(makeInArg(query.getProductDirectionIds(), false));
                    }
                    condition.append(")");
                } else {
                    condition.append(" and product_id in ")
                            .append(makeInArg(query.getProductDirectionIds(), false));
                }
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

            if ( isNotEmpty(query.getRegionIds()) ) {
                if (query.getRegionIds().remove(null)) {
                    condition.append(" and (case_object.id not in (SELECT CASE_ID FROM case_location)");
                    if (!query.getRegionIds().isEmpty()) {
                        condition.append(" or case_object.id in (SELECT CASE_ID FROM case_location WHERE LOCATION_ID IN ")
                                .append(makeInArg(query.getRegionIds(), false))
                                .append(")");
                    }
                    condition.append(")");
                } else {
                    condition.append(" and case_object.id in (SELECT CASE_ID FROM case_location WHERE LOCATION_ID IN ")
                            .append(makeInArg(query.getRegionIds(), false))
                            .append(")");
                }
            }

            if ( isNotEmpty(query.getHeadManagerIds()) ) {
                if (query.getHeadManagerIds().remove(null)) {
                    condition.append(" and (case_object.id not in (SELECT CASE_ID FROM case_member WHERE MEMBER_ROLE_ID = ?)");
                    args.add(En_DevUnitPersonRoleType.HEAD_MANAGER.getId());
                    if (!query.getHeadManagerIds().isEmpty()) {
                        condition.append(" or case_object.id in (SELECT CASE_ID FROM case_member WHERE MEMBER_ROLE_ID = ? and MEMBER_ID IN ")
                                .append(makeInArg(query.getHeadManagerIds(), false))
                                .append(")");
                        args.add(En_DevUnitPersonRoleType.HEAD_MANAGER.getId());
                    }
                    condition.append(")");
                } else {
                    condition.append(" and case_object.id in (SELECT CASE_ID FROM case_member WHERE MEMBER_ROLE_ID = ? and MEMBER_ID IN ")
                            .append(makeInArg(query.getHeadManagerIds(), false))
                            .append(")");
                    args.add(En_DevUnitPersonRoleType.HEAD_MANAGER.getId());
                }
            }

            if ( isNotEmpty(query.getCaseMemberIds()) ) {
                if (query.getCaseMemberIds().remove(null)) {
                    condition.append(" and (case_object.id not in (SELECT CASE_ID FROM case_member)");
                    if (!query.getCaseMemberIds().isEmpty()) {
                        condition.append(" or case_object.id in (SELECT CASE_ID FROM case_member WHERE MEMBER_ID IN ")
                                .append(makeInArg(query.getCaseMemberIds(), false))
                                .append(")");
                    }
                    condition.append(")");
                } else {
                    condition.append(" and case_object.id in (SELECT CASE_ID FROM case_member WHERE MEMBER_ID IN ")
                            .append(makeInArg(query.getCaseMemberIds(), false))
                            .append(")");
                }
            }

            if (query.getPlanId() != null) {
                condition.append(" and case_object.id IN (SELECT case_object_id FROM plan_to_case_object WHERE plan_id = ?)");
                args.add(query.getPlanId());
            }
        });
    }

    private Interval makeToday() {
        Interval interval = new Interval();
        LocalDate local = LocalDate.now();
        interval.from = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        local = local.plusDays(1);
        interval.to = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return interval;
    }

    private Interval makeYesterday() {
        Interval interval = new Interval();
        LocalDate local = LocalDate.now();
        interval.to = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        local = local.minusDays(1);
        interval.from = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return interval;
    }

    private Interval makeThisWeek() {
        Interval interval = new Interval();
        LocalDate local = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue());
        interval.from = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        local = local.plusWeeks(1);
        interval.to = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return interval;
    }

    private Interval makeLastWeek() {
        Interval interval = new Interval();
        LocalDate local = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue());
        interval.to = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        local = local.minusWeeks(1);
        interval.from = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return interval;
    }

    private Interval makeThisMonth() {
        Interval interval = new Interval();
        LocalDate local = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);
        interval.from = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        local = local.plusMonths(1);
        interval.to = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return interval;
    }

    private Interval makeLastMonth() {
        Interval interval = new Interval();
        LocalDate local = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()-1);
        interval.to = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        local.minusMonths(1);
        interval.from = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return interval;
    }

    private Interval makeThisYear() {
        Interval interval = new Interval();
        LocalDate local = LocalDate.now().minusDays(LocalDate.now().getDayOfYear()-1);
        interval.from = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        local = local.plusYears(1);
        interval.to = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return interval;
    }

    private Interval makeLastYear() {
        Interval interval = new Interval();
        LocalDate local = LocalDate.now().minusDays(LocalDate.now().getDayOfYear()-1);
        interval.to = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        local.minusYears(1);
        interval.from = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return interval;
    }

    private Interval createInterval( DateRange dateRange ) {
        if ( dateRange == null )
            return null;

        switch (dateRange.getIntervalType()) {
            case FIXED      : return new Interval(dateRange.getFrom(), dateRange.getTo());
            case TODAY      : return makeToday();
            case YESTERDAY  : return makeYesterday();
            case THIS_WEEK  : return makeThisWeek();
            case LAST_WEEK  : return makeLastWeek();
            case THIS_MONTH : return makeThisMonth();
            case LAST_MONTH : return makeLastMonth();
            case THIS_YEAR  : return makeThisYear();
            case LAST_YEAR  : return makeLastYear();
        }

        return null;
    }

    private class Interval {
        public Date from;
        public Date to;
        Interval ()  {}
        Interval (Date from, Date to) {
            this.from = from;
            this.to = to;
        }
    }
}
