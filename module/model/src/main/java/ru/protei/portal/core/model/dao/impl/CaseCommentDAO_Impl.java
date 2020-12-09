package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dict.En_HistoryAction;
import ru.protei.portal.core.model.dict.En_HistoryType;
import ru.protei.portal.core.model.dto.CaseResolutionTimeReportDto;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.CrmConstants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
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
    public List<CaseComment> getLastNotNullTextCommentsForReport(List<Long> caseId) {
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
    public List<CaseResolutionTimeReportDto> reportCaseResolutionTime(Date from, Date to, List<Long> terminatedStates,
                                                                      List<Long> companiesIds, Set<Long> productIds, List<Long> managersIds, List<Integer> importanceIds,
                                                                      List<Long> tagsIds) {
        String fromTime = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format( from );
        String toTime = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format( to );
        String acceptableStates = makeInArg( terminatedStates, false);

        String products = "";
        if ( productIds != null && !productIds.isEmpty() ) {
            if (productIds.remove(CrmConstants.Product.UNDEFINED)) {
                products += " and (ob.product_id is null";
                if (!productIds.isEmpty()) {
                    products += " or ob.product_id in " + makeInArg(productIds, false);
                }
                products += ")";
            } else {
                products += " and ob.product_id in " + makeInArg(productIds, false);
            }
        }

        String companies = makeAndPartFromListIds(companiesIds, "ob.initiator_company");
        String managers = makeAndPartFromListIds(managersIds, "ob.manager");
        String importance = makeAndPartFromListIds(importanceIds, "ob.importance");
        String tags = tagsIds == null ? "" : " and ob.ID in (SELECT cot.case_id FROM case_object_tag cot WHERE cot.tag_id in " + makeInArg(tagsIds, false) + ")";

        // Активные задачи на момент начала интервала запроса
        String activeCasesAtIntervalStart =
                "SELECT case_object_id, h.date, new_id" +
                        " FROM history h" +
                        "        LEFT OUTER JOIN case_object ob on ob.id = h.case_object_id" +
                        " WHERE h.date = (" +
                        "   SELECT max(date) last" +
                        "   FROM history" +
                        "   WHERE case_object_id = h.case_object_id" +
                        "     and date < '" + fromTime + "'" +  // # левая граница
                        "     and value_type = " + En_HistoryType.CASE_STATE.getId() +
                        "     and action_type in " +
                                    makeInArg(Arrays.asList(En_HistoryAction.ADD.getId(), En_HistoryAction.CHANGE.getId()), false) +
                        "     and new_id is not null" +
                        " )" +
                        "   and new_id in " + acceptableStates
                        + products
                        + companies
                        + managers
                        + importance
                        + tags
                ;

        // Задачи переходящие в активное состояние в интервале запроса
        String activeCasesInInterval =
                "SELECT case_object_id, h.date, new_id" +
                        " FROM history h" +
                        "        LEFT OUTER JOIN case_object ob on ob.id = h.case_object_id" +
                        " WHERE h.date > '" + fromTime + "'" +  // # левая граница
                        "   and h.date < '" + toTime + "' " +  //# правая граница
                        "   and h.value_type = " + En_HistoryType.CASE_STATE.getId() +
                        "   and h.action_type in " +
                                makeInArg(Arrays.asList(En_HistoryAction.ADD.getId(), En_HistoryAction.CHANGE.getId()), false) +
                        "   and new_id in " + acceptableStates
                        + products
                        + companies
                        + managers
                        + importance
                        + tags
                ;

        String query =
                "SELECT case_object_id, outerH.date as historyCreated, new_id, ob.CASENO as caseObjectNumber" +
                        " FROM history outerH LEFT JOIN case_object ob on ob.id = outerH.case_object_id" +
                        " WHERE outerH.case_object_id in (" +
                        "   SELECT DISTINCT case_object_id" +
                        "   from (" +
                        activeCasesAtIntervalStart +
                        " union " +
                        activeCasesInInterval +
                        "        ) as beforeAndInInterval " +
                        " )" +
                        " and outerH.date < '" + toTime + "' " + //# правая граница
                        "  ORDER BY outerH.date ASC;";

        try {
            return jdbcTemplate.query( query, rm );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    RowMapper<CaseResolutionTimeReportDto> rm = new RowMapper<CaseResolutionTimeReportDto>() {
        @Override
        public CaseResolutionTimeReportDto mapRow( ResultSet r, int i ) throws SQLException {
            CaseResolutionTimeReportDto comment = new CaseResolutionTimeReportDto();

            comment.setCaseId( r.getLong( "case_object_id" ) );
            comment.setCaseNumber(r.getLong( "caseObjectNumber" ));
            Long cstateId = r.getLong( "new_id" );
            comment.setCaseStateId( r.wasNull() ? null : cstateId );
            comment.setCreated( new Date( r.getTimestamp( "historyCreated" ).getTime() ) );

            return comment;
        }
    };

    private String makeAndPartFromListIds(final List<?> list, final String field){
        return list == null ? "" : " and " + field + " in " + makeInArg(list, false);
    }
}
