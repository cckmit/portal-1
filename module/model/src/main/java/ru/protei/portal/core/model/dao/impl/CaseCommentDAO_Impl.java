package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseCommentNightWork;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkCaseCommentTimeElapsedSum;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.sqlcondition.Query;
import ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder;
import ru.protei.portal.core.utils.TypeConverters;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.ent.CaseCommentNightWork.Columns.*;
import static ru.protei.portal.core.model.helper.DateRangeUtils.makeInterval;
import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;

/**
 * Created by michael on 20.05.16.
 */
public class CaseCommentDAO_Impl extends PortalBaseJdbcDAO<CaseComment> implements CaseCommentDAO {

    @Autowired
    CaseCommentSqlBuilder sqlBuilder;

    @Autowired
    CompanyDAO companyDAO;

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

    @Override
    public List<CaseCommentNightWork> getCaseCommentNightWork(CaseQuery query) {
        Interval interval = makeInterval(query.getCreatedRange());

        // Берем для уменьшения выборки по day в having - период создания комментарий пошире
        Date caseCommentCreatedFrom = new Date(interval.getFrom().getTime());
        caseCommentCreatedFrom.setDate(caseCommentCreatedFrom.getDate() - 1);

        Date caseCommentCreatedTo = new Date(interval.getTo().getTime());
        caseCommentCreatedTo.setDate(caseCommentCreatedTo.getDate() + 1);

        Query sqlQuery = SqlQueryBuilder.query()
                .where( "co.product_id" ).in( query.getProductIds() )
                .and( "co.initiator_company" ).in( query.getCompanyIds() )
                .and( "co.manager" ).in( query.getManagerIds() )
                .and( "cc.created" ).ge( caseCommentCreatedFrom )
                .and( "cc.created" ).lt( caseCommentCreatedTo )
                .and( "cc.author_id" ).in( query.getCommentAuthorIds() )
                .asQuery();

        return jdbcTemplate.query( "" +
                        "select (CASE WHEN HOUR(cc.CREATED) < " + CrmConstants.NightWork.START_NIGHT + " " +
                        "                THEN date(cc.CREATED) " +
                        "            ELSE ADDDATE(date(cc.CREATED), 1) " +
                        "    END) " + DAY + ", " +
                        "       sum(cc.time_elapsed) " + TIME_ELAPSED_SUM + ", " +
                        "       count(cc.time_elapsed) " + TIME_ELAPSED_COUNT + ", " +
                        "       p.displayname " + AUTHOR_DISPLAY_NAME + ", " +
                        "       co.CASENO " + CASE_NUMBER + ", " +
                        "       c.cname " + CASE_COMPANY_NAME + ", " +
                        "       cust.displayname " + INITIATOR_DISPLAY_NAME +", " +
                        "       co.product_id, du.UNIT_NAME " + PRODUCT_NAME + ", " +
                        "       max(cc.ID) " + LAST_COMMENT_ID + " " +
                        "FROM case_object co " +
                        "         join case_comment cc on cc.CASE_ID = co.ID " +
                        "         join person p on cc.AUTHOR_ID = p.id " +
                        "         left join person cust on co.INITIATOR = cust.id " +
                        "         join company c on co.initiator_company = c.id " +
                        "         left join dev_unit du on co.product_id = du.ID " +
                        "WHERE cc.time_elapsed_type = " + En_TimeElapsedType.NIGHT_WORK.getId() + " and cc.time_elapsed is not null " +
                        "AND " + sqlQuery.buildSql() + " " +
                        "GROUP BY day, co.id, author_id " +
                        "HAVING day >= '" + nightWorkFormat.format(interval.getFrom()) + "' AND day < '" + nightWorkFormat.format(interval.getTo()) + "' " +
                        "ORDER BY " + query.getSortField().getFieldName() + " " + query.getSortDir().name() + " " +
                        "LIMIT " + query.getLimit() + " " +
                        "OFFSET " + query.getOffset() + ";", sqlQuery.args(),
                (ResultSet rs, int rowNum) -> {
                    CaseCommentNightWork comment = new CaseCommentNightWork();
                    comment.setDay(new Date(rs.getTimestamp(DAY).getTime()));
                    comment.setTimeElapsedSum(rs.getLong(TIME_ELAPSED_SUM));
                    comment.setTimeElapsedCount(rs.getLong(TIME_ELAPSED_COUNT));
                    comment.setAuthorDisplayName(rs.getString(AUTHOR_DISPLAY_NAME));
                    comment.setCaseNumber(rs.getLong(CASE_NUMBER));
                    comment.setCaseCompanyName(rs.getString(CASE_COMPANY_NAME));
                    comment.setInitiatorDisplayName(rs.getString(INITIATOR_DISPLAY_NAME));
                    comment.setProductName(rs.getString(PRODUCT_NAME));
                    comment.setLastCommentId(rs.getLong(LAST_COMMENT_ID));
                    return comment;
                });
    }

    @Override
    public List<ReportYtWorkCaseCommentTimeElapsedSum> getCaseCommentReportYtWork(Interval interval) {
        String homeCompanyId = makeHomeCompanySet();

        return jdbcTemplate.query( "select cc.AUTHOR_ID cc_author_id, " +
                        "       sum(cc.time_elapsed) spentTime, " +
                        "       ( case " +
                        "           when co.initiator_company in (" + homeCompanyId + ") OR " +
                        "                    (co.platform_id is null) OR (plat.project_id is null) " +
                        "            then null " +
                        "            else plat.id END " +
                        "        ) sur_platform_id " +
                        "from case_comment cc join case_object co on cc.CASE_ID = co.ID " +
                        "         left outer join platform plat on co.platform_id = plat.id " +
                        "where cc.CREATED >= '" + reportYtWorkFormat.format(interval.from) +"' " +
                        "  and cc.CREATED < '" + reportYtWorkFormat.format(interval.to) +"' " +
                        "  and cc.time_elapsed is not null " +
                        "group by cc.AUTHOR_ID, sur_platform_id ",
                (ResultSet rs, int rowNum) -> {
                    ReportYtWorkCaseCommentTimeElapsedSum sum = new ReportYtWorkCaseCommentTimeElapsedSum();
                    sum.setPersonId(rs.getLong("cc_author_id"));
                    sum.setSpentTime(rs.getInt("spentTime"));
                    long sur_platform_id = rs.getLong("sur_platform_id");
                    sum.setSurrogatePlatformId(rs.wasNull()? null : sur_platform_id);
                    return sum;
                });
    }

    private String makeHomeCompanySet() {
        return companyDAO.getAllHomeCompanies().stream()
                .map(company -> String.valueOf(company.getId()))
                .collect(Collectors.joining(","));
    }

    private final static DateFormat nightWorkFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final static DateFormat reportYtWorkFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
}
