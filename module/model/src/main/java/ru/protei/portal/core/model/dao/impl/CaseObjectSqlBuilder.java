package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.CrmConstants;

import java.util.stream.Collectors;

import static ru.protei.portal.core.model.dao.impl.CaseShortViewDAO_Impl.isSearchAtComments;

public class CaseObjectSqlBuilder {

    public SqlCondition caseCommonQuery (CaseQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1 and deleted = 0");

            // TODO merge ids to use queries simultaneously
            if ( query.getId() != null ) {
                condition.append( " and case_object.id=?" );
                args.add( query.getId() );
            } else if (CollectionUtils.isNotEmpty(query.getMemberIds())) {
                condition.append(" and case_object.id in (select case_id from case_member where member_id in (")
                        .append(StringUtils.join(query.getMemberIds(), ","))
                        .append("))");
            } else if (CollectionUtils.isNotEmpty(query.getCaseTagsIds())) {
                condition.append(" and case_object.id in (select case_id from case_object_tag where tag_id")
                        .append(query.getCaseTagsIds().remove(CrmConstants.CaseTag.NOT_SPECIFIED) ? " is null or tag_id not in (" : " in (" )
                        .append(StringUtils.join(query.getCaseTagsIds(), ","))
                        .append("))");
            }

            if ( !query.isAllowViewPrivate() ) {
                condition.append( " and private_flag=?" );
                args.add( 0 );
            } else if (query.isViewPrivate() != null) {
                condition.append( " and private_flag=?" );
                args.add( query.isViewPrivate() ? 1 : 0 );
            }

            if ( query.getType() != null ) {
                condition.append( " and case_type=?" );
                args.add( query.getType().getId() );
            }

            if ( query.getCaseNumbers() != null && !query.getCaseNumbers().isEmpty() ) {
                condition.append(" and caseno in (")
                        .append(query.getCaseNumbers().stream()
                                .map(Object::toString)
                                .collect(Collectors.joining(","))
                        )
                        .append(")");
            }

            if ( query.getCompanyIds() != null && !query.getCompanyIds().isEmpty() ) {
                condition.append(" and initiator_company in (" + query.getCompanyIds().stream().map(Object::toString).collect( Collectors.joining(",")) + ")");
            }

            if ( query.getInitiatorIds() != null && !query.getInitiatorIds().isEmpty() ) {
                condition.append(" and initiator in (")
                        .append(query.getInitiatorIds().stream().map(Object::toString).collect(Collectors.joining(",")))
                        .append(")");
            }

            if ( query.getProductIds() != null && !query.getProductIds().isEmpty() ) {
                if (query.getProductIds().remove(CrmConstants.Product.UNDEFINED)) {
                    condition.append(" and (product_id is null");
                    if (!query.getProductIds().isEmpty()) {
                        condition.append(" or product_id in (" + query.getProductIds().stream().map(Object::toString).collect( Collectors.joining(",")) + ")");
                    }
                    condition.append(")");
                } else {
                    condition.append(" and product_id in (" + query.getProductIds().stream().map(Object::toString).collect( Collectors.joining(",")) + ")");
                }
            }

            if ( query.getManagerIds() != null && !query.getManagerIds().isEmpty() ) {
                condition.append(" and manager in (" + query.getManagerIds().stream().map(Object::toString).collect( Collectors.joining(",")) + ")");

                if ( query.isOrWithoutManager() ) {
                    condition.append(" or manager is null" );
                }
            }

            if ( query.getStateIds() != null && !query.getStateIds().isEmpty() ) {
                condition.append(" and state in (" + query.getStateIds().stream().map(Object::toString).collect( Collectors.joining(",")) + ")");
            }

            if ( query.getImportanceIds() != null && !query.getImportanceIds().isEmpty() ) {
                condition.append(" and importance in (" + query.getImportanceIds().stream().map(Object::toString).collect( Collectors.joining(",")) + ")");
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
                    condition.append( " and case_comment.cstate_id in " + HelperFunc.makeInArg(query.getStateIds()));
                }
                if ( query.getManagerIds() != null ) {
                    condition.append( " and manager in " +  HelperFunc.makeInArg(query.getManagerIds()));
                }

                condition.append(")");
            }
        });
    }
}
