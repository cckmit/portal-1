package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.CrmConstants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;
import static ru.protei.portal.core.model.util.sqlcondition.SqlConditionBuilder.condition;
import static ru.protei.portal.core.model.util.sqlcondition.SqlConditionBuilder.query;

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
    public List<CaseComment> listByRemoteIds(List<String> remoteIds) {
        String sql = "case_comment.remote_id in (" + remoteIds.stream().map(id -> "?").collect(Collectors.joining(", ")) + ")";
        return getListByCondition(sql, remoteIds);
    }

    @Override
    public boolean checkExistsByRemoteIdAndRemoteLinkId( String remoteId, Long remoteLinkId ) {
        return checkExistsByCondition( " case_comment.remote_id=? and case_comment.remote_link_id=?", remoteId, remoteLinkId );
    }

//    public static void main(String[] args){
////        List<Long> productIds = CollectionUtils.listOf( CrmConstants.Product.UNDEFINED, 0L, 1L );
//        List<Long> productIds = CollectionUtils.listOf(  0L, 1L );
//        List<Long> productIds2 = new ArrayList<>( productIds );
//
//        String products = "fake";
//        if ( productIds != null && !productIds.isEmpty() ) {
//            if (productIds.remove(CrmConstants.Product.UNDEFINED)) {
//                products += " and (ob.product_id is null";
//                if (!productIds.isEmpty()) {
//                    products += " or ob.product_id in " + makeInArg(productIds, false);
//                }
//                products += ")";
//            } else {
//                products += " and ob.product_id in " + makeInArg(productIds, false);
//            }
//        }
//
//        String products2 = condition().condition( "fake" )
//                .and( condition().and( "ob.product_id" ).isNull( productIds2.remove( CrmConstants.Product.UNDEFINED ) ? true : null )
//                        .or( "ob.product_id" ).in( productIds2 )
//                )
//                .getSqlCondition();
//
//        if(products.equalsIgnoreCase( products2 )){
//            int equal = 0;
//        }
//        int  end = 1;
//    }

    @Override
    public List<CaseComment> reportCaseResolutionTime( Date from, Date to, List<Integer> terminatedStates,
                                                       List<Long> companiesIds, List<Long> productIds, List<Long> managersIds, List<Integer> importanceIds,
                                                       List<Long> tagsIds) {
        String fromTime = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format( from );
        String toTime = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format( to );
        String acceptableStates = makeInArg( terminatedStates);

        String products = "";
        if ( productIds != null && !productIds.isEmpty() ) {
            if (productIds.remove(CrmConstants.Product.UNDEFINED)) {
                products += " and (ob.product_id is null";
                if (!productIds.isEmpty()) {
                    products += " or ob.product_id in " + makeInArg(productIds);
                }
                products += ")";
            } else {
                products += " and ob.product_id in " + makeInArg(productIds);
            }
        }

        String companies = makeAndPartFromListIds(companiesIds, "ob.initiator_company");
        String managers = makeAndPartFromListIds(managersIds, "ob.manager");
        String importance = makeAndPartFromListIds(importanceIds, "ob.importance");
        String tags = tagsIds == null ? "" : " and ob.ID in (SELECT cot.case_id FROM case_object_tag cot WHERE cot.tag_id in " + makeInArg(tagsIds) + ")";

        // Активные задачи на момент начала интервала запроса
        String activeCasesAtIntervalStart =
                "SELECT case_id, cc.created, CSTATE_ID" +
                        " FROM case_comment cc" +
                        "        LEFT OUTER JOIN case_object ob on ob.id = cc.CASE_ID" +
                        " WHERE cc.created = (" +
                        "   SELECT max(created) last" +
                        "   FROM case_comment" +
                        "   WHERE case_id = cc.CASE_ID" +
                        "     and created < '" + fromTime + "'" +  // # левая граница
                        "     and CSTATE_ID is not null" +
                        " )" +
                        "   and CSTATE_ID in " + acceptableStates
                        + products
                        + companies
                        + managers
                        + importance
                        + tags
                ;

        // Задачи переходящие в активное состояние в интервале запроса
        String activeCasesInInterval =
                "SELECT case_id, cc.created, CSTATE_ID" +
                        " FROM case_comment cc" +
                        "        LEFT OUTER JOIN case_object ob on ob.id = cc.CASE_ID" +
                        " WHERE cc.created > '" + fromTime + "'" +  // # левая граница
                        "   and cc.created < '" + toTime + "' " +  //# правая граница
                        "   and CSTATE_ID in " + acceptableStates
                        + products
                        + companies
                        + managers
                        + importance
                        + tags
                ;

        String query =
                "SELECT case_id, created, CSTATE_ID" +
                        " FROM case_comment outerComment" +
                        " WHERE outerComment.case_id in (" +
                        "   SELECT DISTINCT case_id" +
                        "   from (" +
                        activeCasesAtIntervalStart +
                        " union " +
                        activeCasesInInterval +
                        "        ) as beforeAndInInterval " +
                        " )" +
                        " and created < '" + toTime + "' " + //# правая граница
                        "  ORDER BY created ASC;";

        try {
            return jdbcTemplate.query( query, rm );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    RowMapper<CaseComment> rm = new RowMapper<CaseComment>() {
        @Override
        public CaseComment mapRow( ResultSet r, int i ) throws SQLException {
            CaseComment comment = new CaseComment();

            comment.setCaseId( r.getLong( "case_id" ) );
            Long cstateId = r.getLong( "CSTATE_ID" );
            comment.setCaseStateId( r.wasNull() ? null : cstateId );
            comment.setCreated( new Date( r.getTimestamp( "created" ).getTime() ) );

            return comment;
        }
    };

    private String makeAndPartFromListIds(final List<?> list, final String field){
        return list == null ? "" : " and " + field + " in " + makeInArg(list);
    }
}
