package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.CrmConstants;

import java.util.ArrayList;
import java.util.List;

import static ru.protei.portal.core.model.dao.impl.CaseShortViewDAO_Impl.isSearchAtComments;
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
            } else if (CollectionUtils.isNotEmpty(query.getCaseTagsIds())) {
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

            if ( query.getCompanyIds() != null && !query.getCompanyIds().isEmpty() ) {
                condition.append(" and initiator_company in " + makeInArg(query.getCompanyIds(), false));
            }

            if ( query.getInitiatorIds() != null && !query.getInitiatorIds().isEmpty() ) {
                condition.append(" and initiator in " + makeInArg(query.getInitiatorIds(), false));
            }

            if (CollectionUtils.isNotEmpty(query.getProductIds())) {
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

            if ( query.getManagerIds() != null && !query.getManagerIds().isEmpty() ) {
                List<Long> managerIds = new ArrayList<>(query.getManagerIds());
                boolean isWithoutManager = managerIds.remove(CrmConstants.Employee.UNDEFINED);

                if (!isWithoutManager) {
                    condition.append(" and manager IN " + makeInArg(managerIds, false));
                } else if (managerIds.isEmpty()) {
                    condition.append(" and manager IS NULL");
                } else {
                    condition.append(" and (manager IN " + makeInArg(managerIds, false) + " OR manager IS NULL)");
                }
            }

            if ( query.getStateIds() != null && !query.getStateIds().isEmpty() ) {
                condition.append(" and state in " + makeInArg(query.getStateIds(), false));
            }

            if ( query.getImportanceIds() != null && !query.getImportanceIds().isEmpty() ) {
                condition.append(" and importance in " + makeInArg(query.getImportanceIds(), false));
            }

            if ( query.getCreatedFrom() != null ) {
                condition.append( " and case_object.created >= ?" );
                args.add( query.getCreatedFrom() );
            }

            if ( query.getCreatedTo() != null ) {
                condition.append( " and case_object.created < ?" );
                args.add( query.getCreatedTo() );
            }

            if ( query.getModifiedFrom() != null ) {
                condition.append( " and case_object.modified >= ?" );
                args.add( query.getModifiedFrom() );
            }

            if ( query.getModifiedTo() != null ) {
                condition.append( " and case_object.modified < ?" );
                args.add( query.getModifiedTo() );
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

                if ( query.getModifiedFrom() != null ) {
                    condition.append( " and case_comment.created >= ?" );
                    args.add(query.getModifiedFrom());
                }
                if ( query.getModifiedTo() != null ) {
                    condition.append( " and case_comment.created < ?" );
                    args.add(query.getModifiedTo());
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

            if (query.getProductDirectionId() != null) {
                condition
                        .append(" and product_id = ")
                        .append(query.getProductDirectionId());
            }

            if (CollectionUtils.isNotEmpty(query.getCreatorIds())) {
                condition
                        .append(" and case_object.CREATOR in ")
                        .append(makeInArg(query.getCreatorIds(), false));
            }
        });
    }
}
