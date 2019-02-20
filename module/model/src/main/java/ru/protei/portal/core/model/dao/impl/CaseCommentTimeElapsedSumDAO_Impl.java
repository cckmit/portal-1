package ru.protei.portal.core.model.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.dao.CaseCommentTimeElapsedSumDAO;
import ru.protei.portal.core.model.ent.CaseCommentTimeElapsedSum;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.CommentTimeElapsedQuery;
import ru.protei.portal.core.model.util.sqlcondition.Query;
import ru.protei.portal.core.model.util.sqlcondition.SqlConditionBuilder;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

public class CaseCommentTimeElapsedSumDAO_Impl extends PortalBaseJdbcDAO<CaseCommentTimeElapsedSum> implements CaseCommentTimeElapsedSumDAO {

    @Override
    public List<CaseCommentTimeElapsedSum> getListByQuery( CommentTimeElapsedQuery caseCommentQuery ) {
        return getList( makeJdbcQueryParameters( caseCommentQuery ) );
    }

    private static final Logger log = LoggerFactory.getLogger( CaseCommentTimeElapsedSumDAO_Impl.class );

    private JdbcQueryParameters makeJdbcQueryParameters( CommentTimeElapsedQuery commentQuery ) {
        CaseQuery query = commentQuery.getCaseQuery();

        Query sqlQuery = SqlConditionBuilder.query()
                .where( "case_object.id" ).equal( query.getId() )
                .and( "case_object.caseno" ).in( query.getCaseNumbers() )
                .and( "case_object.product_id" ).in( query.getProductIds() )
                .and( "case_object.initiator_company" ).in( query.getCompanyIds() )
                .and( "case_object.manager" ).in( query.getManagerIds() )

                .and( "case_comment.created" ).ge( query.getFrom() )
                .and( "case_comment.created" ).lt( query.getTo() )
                .and( "case_comment.time_elapsed" ).not().isNull( commentQuery.isTimeElapsedNotNull() )
                .and( "case_comment.author_id" ).in( query.getCommentAuthorIds() )

                .asQuery()
                .offset( commentQuery.getOffset() )
                .limit( commentQuery.getLimit() )
                .sort( commentQuery.sortDir, commentQuery.getSortField().getFieldName() )
                .groupBy( "author_id", "case_id" );

        log.info( "makeJdbcQueryParameters(): where: {}", sqlQuery.toString() );

        return sqlQuery.asJdbcQueryParameters();
    }
}
