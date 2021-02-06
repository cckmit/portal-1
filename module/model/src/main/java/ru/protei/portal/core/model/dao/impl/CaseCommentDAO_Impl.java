package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;

import java.sql.ResultSet;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;

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
    public List<CaseComment> listByRemoteIds(List<String> remoteIds) {
        String sql = "case_comment.remote_id in (" + remoteIds.stream().map(id -> "?").collect(Collectors.joining(", ")) + ")";
        return getListByCondition(sql, remoteIds);
    }

    @Override
    public boolean checkExistsByRemoteIdAndRemoteLinkId( String remoteId, Long remoteLinkId ) {
        return checkExistsByCondition( " case_comment.remote_id=? and case_comment.remote_link_id=?", remoteId, remoteLinkId );
    }

    @Override
    public List<CaseComment> getLastNotNullTextPartialCommentsForReport(List<Long> caseId) {
        return jdbcTemplate.query( "SELECT ID, CASE_ID, CREATED, COMMENT_TEXT " +
                        "FROM case_comment " +
                        "WHERE id in " +
                            "(SELECT max(cs.id) id " + // "created" field is not unique, select last comment by id
                                "FROM case_comment cs " +
                                    "inner join (SELECT CASE_ID, max(CREATED) created " +     // select last created date
                                                "FROM case_comment " +
                                                "WHERE CASE_ID in " + makeInArg(caseId) +
                                                    " AND COMMENT_TEXT is not NULL " +
                                                "group by CASE_ID) last_created_table " +
                                            "on cs.CASE_ID = last_created_table.CASE_ID and cs.CREATED = last_created_table.created " +
                            "WHERE cs.COMMENT_TEXT is not NUll " +
                            "group by cs.CASE_ID);",
                (ResultSet rs, int rowNum) -> {
                    CaseComment caseComment = new CaseComment();
                    caseComment.setId(rs.getLong("ID"));
                    caseComment.setCaseId(rs.getLong("CASE_ID"));
                    caseComment.setCreated(new Date( rs.getTimestamp( "CREATED" ).getTime()));
                    caseComment.setText(rs.getString("COMMENT_TEXT"));
                    return caseComment;
                });
    }

    @Override
    public List<CaseComment> getPartialCommentsForReport(CaseCommentQuery query) {
        SqlCondition where = createSqlCondition( query );
        return partialGetListByCondition(where.condition,  TypeConverters.createSort(query), where.args, "id", "case_id", "created", "comment_text");
    }
}
