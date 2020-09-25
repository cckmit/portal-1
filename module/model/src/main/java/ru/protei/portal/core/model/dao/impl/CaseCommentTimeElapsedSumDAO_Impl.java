package ru.protei.portal.core.model.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.dao.CaseCommentTimeElapsedSumDAO;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.ent.CaseCommentTimeElapsedSum;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.model.util.sqlcondition.Query;
import ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.Collection;
import java.util.List;

import static ru.protei.portal.core.model.helper.DateRangeUtils.makeInterval;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.condition;

public class CaseCommentTimeElapsedSumDAO_Impl extends PortalBaseJdbcDAO<CaseCommentTimeElapsedSum> implements CaseCommentTimeElapsedSumDAO {

    @Override
    public List<CaseCommentTimeElapsedSum> getListByQuery( CaseQuery caseCommentQuery ) {
        return getList( makeJdbcQueryParameters( caseCommentQuery ) );
    }

    private static final Logger log = LoggerFactory.getLogger( CaseCommentTimeElapsedSumDAO_Impl.class );

    private JdbcQueryParameters makeJdbcQueryParameters( CaseQuery query ) {
        Interval interval = makeInterval(query.getCreatedRange());

        Query sqlQuery = SqlQueryBuilder.query()
                .where( "case_object.id" ).equal( query.getId() )
                .and( "case_object.caseno" ).in( query.getCaseNumbers() )
                .and( "case_object.product_id" ).in( query.getProductIds() )
                .and( "case_object.initiator_company" ).in( query.getCompanyIds() )
                .and( "case_object.manager" ).in( query.getManagerIds() )

                .and( "case_comment.created" ).ge( interval.getFrom() )
                .and( "case_comment.created" ).lt( interval.getTo() )
                .and( "case_comment.time_elapsed" ).not().isNull( true )
                .and( getConditionByTimeElapsedTypeIds(query.getTimeElapsedTypeIds()) )
                .and( "case_comment.author_id" ).in( query.getCommentAuthorIds() )

                .asQuery()
                .offset( query.getOffset() )
                .limit( query.getLimit() )
                .sort( query.sortDir, query.getSortField().getFieldName() )
                .groupBy( "author_id", "case_id" );

        log.info( "makeJdbcQueryParameters(): where: {}", sqlQuery.toString() );

        return sqlQuery.build();
    }

    private Condition getConditionByTimeElapsedTypeIds(Collection<Integer> timeElapsedTypeIds) {
        if (!timeElapsedTypeIds.contains(En_TimeElapsedType.NONE.getId())) {
            return condition().and("case_comment.time_elapsed_type").in(timeElapsedTypeIds);
        }

        return condition()
                .or("case_comment.time_elapsed_type").in(timeElapsedTypeIds)
                .or("case_comment.time_elapsed_type").isNull(true)
                .and("case_comment.time_elapsed").not().isNull(true);
    }
}
