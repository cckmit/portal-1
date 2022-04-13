package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.CaseTypeDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.sqlcondition.Query;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcHelper;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.*;

import static java.lang.Boolean.TRUE;
import static ru.protei.portal.core.model.dao.impl.CaseShortViewDAO_Impl.isFilterByTagNames;
import static ru.protei.portal.core.model.dict.En_CaseType.CRM_SUPPORT;
import static ru.protei.portal.core.model.ent.CaseObject.Columns.EXT_APP;
import static ru.protei.portal.core.model.helper.StringUtils.length;
import static ru.protei.portal.core.model.helper.StringUtils.trim;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

/**
 * Created by michael on 19.05.16.
 */
public class CaseObjectDAO_Impl extends PortalBaseJdbcDAO<CaseObject> implements CaseObjectDAO {

    public static final String LEFT_JOIN_CASE_COMMENT = " LEFT JOIN case_comment ON case_object.id = case_comment.CASE_ID";
    public static final String LEFT_JOIN_CASE_TAG =
            " LEFT JOIN case_object_tag on case_object.ID = case_object_tag.case_id join case_tag on case_tag.id = case_object_tag.tag_id";
    public static final String LEFT_JOIN_HISTORY = " LEFT JOIN history ON case_object.id = history.case_object_id";
    public static final String LEFT_JOIN_PLAN_ORDER =
            " LEFT JOIN plan_to_case_object plan ON case_object.id = plan.case_object_id";

    private static final String COLUMN_EMAIL_LAST_ID = "email_last_id";

    @Autowired
    CaseTypeDAO caseTypeDAO;

    @Autowired
    CaseObjectSqlBuilder caseObjectSqlBuilder;

    public Map<Long,Long> getNumberToIdMap (En_CaseType caseType) {
        Map<Long, Long> numberToIdMap = new HashMap<>();

        partialGetListByCondition("CASE_TYPE=?", Collections.singletonList(caseType.getId()),"id","CASENO")
                .forEach(o -> numberToIdMap.put(o.getCaseNumber(), o.getId()));

        return numberToIdMap;
    }

    @Override
    public CaseObject getByExternalAppCaseId(String externalApplicationCaseId) {
        return getByCondition("EXT_APP_ID=?", externalApplicationCaseId);
    }

    @Override
    public Long getCaseIdByNumber(En_CaseType caseType, long number) {
        Query q = query()
                .where("case_type").equal(caseType.getId())
                    .and("caseno").equal(number)
                .asQuery();
        CaseObject obj = partialGetByCondition(q.buildSql(), Arrays.asList(q.args()), getIdColumnName());
        return obj != null ? obj.getId() : null;
    }

    @Override
    public Long getCaseNumberById( long caseId) {
        CaseObject obj = partialGetByCondition("id=?", Collections.singletonList(caseId), "CASENO");
        return obj != null ? obj.getCaseNumber() : null;
    }

    @Override
    public CaseObject getCaseByNumber(En_CaseType caseType, long caseNo) {
        Query q = query()
                .where("case_type").equal(caseType.getId())
                    .and("caseno").equal(caseNo)
                .asQuery();
        return getByCondition(q.buildSql(), q.args());
    }

    @Override
    public Long insertCase(CaseObject object) {

        En_CaseType type = object.getType();

        Long caseNumber = caseTypeDAO.generateNextId(type);//HelperFunc.nvlt(getMaxValue("CASENO", Long.class, "case_type=?", type.getId()),0L) + 1;

        object.setCaseNumber(caseNumber);
        object.setExtId(type.makeGUID(caseNumber));

        return persist(object);
    }

    @Override
    public List< CaseObject > getCases(CaseQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return getList(parameters);
    }

    @Override
    public SearchResult<CaseObject> getSearchResult(CaseQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return getSearchResult(parameters);
    }

    @Override
    public int countByQuery(CaseQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return JdbcHelper.getObjectsCount(this.getObjectMapper(), this.jdbcTemplate, parameters);
    }

    @Override
    public String getExternalAppName( Long caseId ) {
        String select = query().
                select( EXT_APP ).from( getTableName() ).where( getIdColumnName() ).equal( caseId ).asQuery().buildSql();

        return jdbcTemplate.queryForObject( select, String.class, caseId );
    }

    @Override
    public List<Long> getCaseNumbersByPlatformId(Long id) {
        String sql = "SELECT case_object.CASENO FROM " + getTableName() + " WHERE case_object.platform_id = " + id;
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    @Override
    @Transactional
    public Long getAndIncrementEmailLastId( Long caseId ) {
        String selectForUpdate = query().forUpdate().
                select( COLUMN_EMAIL_LAST_ID ).from( getTableName() ).where( getIdColumnName() ).equal( caseId ).asQuery().buildSql();

        Long lastId = jdbcTemplate.queryForObject( selectForUpdate, Long.class, caseId );
        String sql = "UPDATE " + getTableName() + " SET " + COLUMN_EMAIL_LAST_ID + " = " + COLUMN_EMAIL_LAST_ID + "+1 WHERE " + getIdColumnName() + " = " + caseId;
        jdbcTemplate.update(sql);
        return lastId;
    }

    @Override
    public CaseObject getByCaseNameLike(String name) {
        return getByCondition("CASE_NAME like ?", "%" + name + "%");
    }

    @Override
    public List<Long> getCaseIdToAutoOpen() {
        Query query = query().select( "id" )
                .from( "case_object" )
                .where("case_object.state" ).equal(CrmConstants.State.CREATED)
                .and("case_object.case_type").equal(CRM_SUPPORT.getId())
                .and(query()
                        .select( "SELECT company.auto_open_issue" ).from( "company" )
                            .whereExpression( "company.id = case_object.initiator_company" ))
                .asQuery();

        return jdbcTemplate.queryForList(query.buildSql(), query.args(), Long.class);
    }

    @Override
    public boolean isJiraDuplicateByProjectId(String projectId) {
        Query subQuery = query().select("JSON_EXTRACT(EXT_APP_DATA, '$.projectId') projectIds")
                .from("case_object")
                .where("EXT_APP").equal("jira")
            .asQuery();

        String query = "SELECT true WHERE '" + projectId + "' IN (" + subQuery.buildSql() + ");";

        return !jdbcTemplate.queryForList(query, subQuery.args()).isEmpty();
    }

    @SqlConditionBuilder
    public SqlCondition caseQueryCondition (CaseQuery query) {
        return caseObjectSqlBuilder.caseCommonQuery(query);
    }

    public static boolean isSearchAtComments(CaseQuery query) {
        return query.isSearchStringAtComments()
                && length(trim( query.getSearchString() )) >= CrmConstants.Issue.MIN_LENGTH_FOR_SEARCH_BY_COMMENTS;
    }

    private JdbcQueryParameters buildJdbcQueryParameters(CaseQuery query) {

        JdbcQueryParameters parameters = new JdbcQueryParameters();

        SqlCondition where = createSqlCondition(query);
        if (where.isConditionDefined()) {
            parameters.withCondition(where.condition, where.args);
        }

        parameters.withOffset(query.getOffset());
        parameters.withLimit(query.getLimit());
        parameters.withSort(TypeConverters.createSort( query ));

        if (query.getPlanId() != null) {
            parameters.withDistinct(false);
            parameters.withJoins(LEFT_JOIN_PLAN_ORDER);
        } else {
            String joins = "";
            if (isNeedJoinComments(query) || query.getModifiedRange() != null) {
                joins += LEFT_JOIN_CASE_COMMENT;
            }
            if (isFilterByTagNames(query)) {
                joins += LEFT_JOIN_CASE_TAG;
            }
            if (TRUE.equals(query.isCheckImportanceHistory()) || query.getModifiedRange() != null) {
                joins += LEFT_JOIN_HISTORY;
            }
            if (!joins.equals("")) {
                parameters.withDistinct(true);
                parameters.withJoins(joins);
            }
        }
        return parameters;
    }

    private boolean isNeedJoinComments(CaseQuery caseQuery) {
        if (isSearchAtComments(caseQuery)) {
            return true;
        }

        if (CollectionUtils.isNotEmpty(caseQuery.getTimeElapsedTypeIds())) {
            return true;
        }

        return false;
    }
}
