package ru.protei.portal.core.model.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.dao.CaseCommentTimeElapsedSumDAO;
import ru.protei.portal.core.model.ent.CaseCommentTimeElapsedSum;
import ru.protei.portal.core.model.query.CommentTimeElapsedQuery;
import ru.protei.portal.core.model.query.SqlConditionBuilder;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

public class CaseCommentTimeElapsedSumDAO_Impl extends PortalBaseJdbcDAO<CaseCommentTimeElapsedSum> implements CaseCommentTimeElapsedSumDAO {

    @Override
    public List<CaseCommentTimeElapsedSum> getListByQuery( CommentTimeElapsedQuery caseCommentQuery ) {
        return getList( makeJdbcQueryParameters( caseCommentQuery ) );
    }

    private static final Logger log = LoggerFactory.getLogger( CaseCommentTimeElapsedSumDAO_Impl.class );
    private JdbcQueryParameters makeJdbcQueryParameters( CommentTimeElapsedQuery query ) {
        SqlConditionBuilder where = SqlConditionBuilder.init();

        where
                .and( "case_object.id" ).equal( query.getId() )
                .and( "case_object.caseno" ).in( query.getCaseNumbers() )
                .and( "case_object.initiator" ).in( query.getInitiatorIds() )
                .and( "case_object.product_id" ).in( query.getProductIds() )
                .and( "case_object.initiator_company" ).in( query.getCompanyIds() )
                .and( "case_object.manager" ).in( query.getManagerIds() )

                .and( "case_comment.created" ).ge( query.getFrom() )
                .and( "case_comment.created" ).lt( query.getTo() )
                .and( "case_comment.time_elapsed" ).not().isNull( query.isTimeElapsedNotNull() )
        ;
        log.info( "makeJdbcQueryParameters(): where: {}", where.toString()  );

        JdbcQueryParameters parameters = where.asJdbcQueryParameters();
        parameters.withOffset( query.getOffset() );
        parameters.withLimit( query.getLimit() );
        parameters.withSort( TypeConverters.createSort( query ) );
        parameters.withGroupBy( "author_id", "case_id" );
        return parameters;
    }
}
