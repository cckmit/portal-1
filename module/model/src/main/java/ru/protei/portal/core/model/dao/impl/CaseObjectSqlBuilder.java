package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.CrmConstants;

import java.util.stream.Collectors;

import static ru.protei.portal.core.model.dao.impl.CaseShortViewDAO_Impl.isSearchAtComments;

public class CaseObjectSqlBuilder {

    public SqlCondition caseCommonQuery (CaseQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1 and deleted = 0");

            if ( query.getId() != null ) {
                condition.append( " and case_object.id=?" );
                args.add( query.getId() );
            }

            if ( !query.isAllowViewPrivate() ) {
                condition.append( " and private_flag=?" );
                args.add( 0 );
            }

            if ( query.getType() != null ) {
                condition.append( " and case_type=?" );
                args.add( query.getType().getId() );
            }

            if ( query.getCaseNo() != null ) {
                condition.append( " and caseno=?" );
                args.add( query.getCaseNo() );
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

            if ( query.isWithoutManager() ) {
                condition.append(" and manager is null" );
            } else if ( query.getManagerIds() != null && !query.getManagerIds().isEmpty() ) {
                condition.append(" and manager in (" + query.getManagerIds().stream().map(Object::toString).collect( Collectors.joining(",")) + ")");
            }

            if ( query.getStateIds() != null && !query.getStateIds().isEmpty() ) {
                condition.append(" and state in (" + query.getStateIds().stream().map(Object::toString).collect( Collectors.joining(",")) + ")");
            }

            if ( query.getImportanceIds() != null && !query.getImportanceIds().isEmpty() ) {
                condition.append(" and importance in (" + query.getImportanceIds().stream().map(Object::toString).collect( Collectors.joining(",")) + ")");
            }

            if ( query.getFrom() != null ) {
                condition.append( " and case_object.created >= ?" );
                args.add( query.getFrom() );
            }

            if ( query.getTo() != null ) {
                condition.append( " and case_object.created < ?" );
                args.add( query.getTo() );
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

            if (query.getIncludeIds() != null && !query.getIncludeIds().isEmpty()) {
                condition.append(" or case_object.id in (");
                condition.append(query.getIncludeIds().stream().map(Object::toString).collect(Collectors.joining(",")));
                condition.append(")");
            }

            if (query.getSearchCasenoString() != null && !query.getSearchCasenoString().isEmpty()) {
                condition.append(" and caseno like ?");
                args.add(HelperFunc.makeLikeArg(query.getSearchCasenoString(), true));
            }
        });
    }
}
