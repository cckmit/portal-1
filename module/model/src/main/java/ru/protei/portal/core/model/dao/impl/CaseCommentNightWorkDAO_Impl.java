package ru.protei.portal.core.model.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.dao.CaseCommentNightWorkDAO;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.ent.CaseCommentNightWork;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.model.util.sqlcondition.Query;
import ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

import static ru.protei.portal.core.model.helper.DateRangeUtils.makeInterval;

public class CaseCommentNightWorkDAO_Impl extends PortalBaseJdbcDAO<CaseCommentNightWork> implements CaseCommentNightWorkDAO {

    @Override
    public List<CaseCommentNightWork> getListByQuery( CaseQuery caseCommentQuery ) {
        return getList( makeJdbcQueryParameters( caseCommentQuery ) );
    }

    private static final Logger log = LoggerFactory.getLogger( CaseCommentNightWorkDAO_Impl.class );

    private JdbcQueryParameters makeJdbcQueryParameters( CaseQuery query ) {
        Interval interval = makeInterval(query.getCreatedRange());

        Query sqlQuery = SqlQueryBuilder.query()
                .where( "co.product_id" ).in( query.getProductIds() )
                .and( "co.initiator_company" ).in( query.getCompanyIds() )
                .and( "co.manager" ).in( query.getManagerIds() )
                .and( "cc.created" ).ge( interval.getFrom() )
                .and( "cc.created" ).lt( interval.getTo() )
                .and( "cc.time_elapsed" ).not().isNull( true )
                .and( "cc.time_elapsed_type" ).equal(En_TimeElapsedType.NIGHT_WORK.getId())
                .and( "cc.author_id" ).in( query.getCommentAuthorIds() )
                .asQuery()
                .offset( query.getOffset() )
                .limit( query.getLimit() )
                .sort( query.getSortDir(), query.getSortField().getFieldName() )
                .groupBy( "day", "co.id", "author_display_name" );

        log.info( "makeJdbcQueryParameters(): where: {}", sqlQuery.toString() );

        return sqlQuery.build();
    }
}
