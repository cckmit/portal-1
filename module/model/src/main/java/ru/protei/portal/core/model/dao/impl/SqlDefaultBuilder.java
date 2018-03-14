package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.stream.Collectors;

public class SqlDefaultBuilder {

    public SqlCondition caseCommonQuery (CaseQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

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

            if ( query.getCompanyId() != null ) {
                condition.append( " and initiator_company=?" );
                args.add( query.getCompanyId() );
            }

            if ( query.getProductId() != null ) {
                condition.append( " and product_id=?" );
                args.add( query.getProductId() );
            }

            if ( query.getManagerId() != null) {
                if(query.getManagerId() > 0) {
                    condition.append(" and manager=?");
                    args.add(query.getManagerId());
                }else{
                    condition.append( " and manager is null" );
                }
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
                condition.append( " and ( case_name like ? or case_object.info like ?)" );
                String likeArg = HelperFunc.makeLikeArg(query.getSearchString(), true);
                args.add(likeArg);
                args.add(likeArg);
            }
        });
    }
}
