package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by michael on 20.05.16.
 */
public class CaseCommentDAO_Impl extends PortalBaseJdbcDAO<CaseComment> implements CaseCommentDAO {

    @Autowired
    CaseCommentSqlBuilder sqlBuilder;

    @Override
    public List<CaseComment> getCaseComments( CaseCommentQuery query ) {
        return listByQuery( query );
    }

    @Override
    public List<Long> getCaseCommentsCaseIds( CaseCommentQuery query ) {
        SqlCondition where = createSqlCondition( query );
        return listColumnValue( "case_id", Long.class, where.condition, where.args.toArray() );
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition( CaseCommentQuery query ) {
        return sqlBuilder.createSqlCondition( query );
    }

    @Override
    public CaseComment getByRemoteId( String remoteId ) {
        return getByCondition( " case_comment.remote_id=? ", remoteId );
    }

    @Override
    public boolean checkExistsByRemoteIdAndRemoteLinkId( String remoteId, Long remoteLinkId ) {
        return checkExistsByCondition( " case_comment.remote_id=? and case_comment.remote_link_id=?", remoteId, remoteLinkId );
    }

    @Override
    public List<CaseComment> reportCaseCompletionTime( Long productId, Date from, Date to, List<Integer> terminatedStates ) {
        String fromTime = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format( from );
        String toTime = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format( to );
        String terminates = terminatedStates.stream().map( String::valueOf ).collect( Collectors.joining( "," ) );

        String query = "SELECT ob.ID   caseID," +
                "       cc.CSTATE_ID   csId," +
                "       cc.CREATED ccCreated" +
                " FROM case_comment cc" +
                "       LEFT OUTER JOIN case_object ob on ob.id = cc.CASE_ID" +
                " WHERE ob.product_id = ?" +
                "  and ob.id not in (" +
                "  SELECT DISTINCT cc.CASE_ID" +
                "  FROM case_comment cc" +
                "  WHERE " +
                "(cc.CSTATE_ID in (" + terminates + ") and cc.CREATED < '" + fromTime + "') " +
                "     or (cc.CSTATE_ID not in (" + terminates + ") and cc.CREATED > '" + toTime + "')" +
                ")" +
                " ORDER BY ccCreated ASC;";
        int stop = 0;
        try {
            return jdbcTemplate.query( query, rm, productId );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    RowMapper<CaseComment> rm = new RowMapper<CaseComment>() {
        @Override
        public CaseComment mapRow( ResultSet r, int i ) throws SQLException {
            CaseComment comment = new CaseComment();

            comment.setCaseId( r.getLong( "caseID" ) );
            comment.setCaseStateId( r.getLong( "csId" ) );
            comment.setCreated( new Date( r.getTimestamp( "ccCreated" ).getTime() ) );

            return comment;
        }
    };


}
