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
import java.util.Date;
import java.util.List;

/**
 * Created by michael on 20.05.16.
 */
public class CaseCommentDAO_Impl extends PortalBaseJdbcDAO<CaseComment> implements CaseCommentDAO {

    @Autowired
    CaseCommentSqlBuilder sqlBuilder;

    @Override
    public List<CaseComment> getCaseComments(CaseCommentQuery query) {
        return listByQuery(query);
    }

    @Override
    public List<Long> getCaseCommentsCaseIds(CaseCommentQuery query) {
        SqlCondition where = createSqlCondition(query);
        return listColumnValue("case_id", Long.class, where.condition, where.args.toArray());
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(CaseCommentQuery query) {
        return sqlBuilder.createSqlCondition(query);
    }

    @Override
    public CaseComment getByRemoteId(String remoteId) {
        return getByCondition(" case_comment.remote_id=? ", remoteId);
    }

    @Override
    public boolean checkExistsByRemoteIdAndRemoteLinkId(String remoteId, Long remoteLinkId) {
        return checkExistsByCondition(" case_comment.remote_id=? and case_comment.remote_link_id=?", remoteId, remoteLinkId);
    }

    @Override
    public List<CaseComment> reportCaseCompletionTime( Long productId ) {
        try {
            return jdbcTemplate.query( reportCaseCompletionTimeQuery,
                    rm, productId);
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
            comment.setCreated( new Date(r.getTimestamp( "ccCreated" ).getTime() ));

            return comment;
        }
    };

    private static final String reportCaseCompletionTimeQuery = "SELECT product.UNIT_NAME," +
            "       ob.product_id," +
            "       ob.ID      caseID," +
            "       ob.CASE_NAME," +
            "       cc.CSTATE_ID   csId," +
            "       state.state," +
            "       cc.id      case_comment_id," +
            "       ob.CREATED obCreated," +
            "       cc.CREATED ccCreated," +
            "       COMMENT_TEXT" +
            " FROM case_comment cc" +
            "       LEFT OUTER JOIN case_object ob on ob.id = cc.CASE_ID" +
            "       JOIN dev_unit product on ob.product_id = product.id" +
            "       JOIN case_state state on cc.CSTATE_ID = state.ID" +
            " WHERE product.id = ?" +
            "  and ob.id not in (" +
            "  SELECT DISTINCT cc.CASE_ID" +
            "  FROM case_comment cc" +
            "  WHERE (cc.CSTATE_ID in (3, 5, 7, 8, 9, 10, 17, 32, 33) and cc.CREATED < '2017-05-24 10:00:00') " +
            "     or (cc.CSTATE_ID not in (3, 5, 7, 8, 9, 10, 17, 32, 33) and cc.CREATED > '2017-05-24 18:00:00')" +
            ")" +
            "ORDER BY ccCreated ASC;";

}
