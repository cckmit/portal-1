package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.CaseTypeDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcHelper;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.ent.CaseObject.Columns.EXT_APP;
import static ru.protei.portal.core.model.helper.StringUtils.length;
import static ru.protei.portal.core.model.helper.StringUtils.trim;
import static ru.protei.portal.core.model.util.sqlcondition.SqlConditionBuilder.*;

/**
 * Created by michael on 19.05.16.
 */
public class CaseObjectDAO_Impl extends PortalBaseJdbcDAO<CaseObject> implements CaseObjectDAO {

    public static final String LEFT_JOIN_CASE_COMMENT = " LEFT JOIN case_comment ON case_object.id = case_comment.CASE_ID";

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
    public Long getCaseId(En_CaseType caseType, long number) {
        CaseObject obj = partialGetByCondition("case_type=? and caseno=?", Arrays.asList(caseType.getId(), number), getIdColumnName());
        return obj != null ? obj.getId() : null;
    }

    @Override
    public Long getCaseIdByNumber( long number ) {
        CaseObject obj = partialGetByCondition("caseno=?", Arrays.asList(number), getIdColumnName());
        return obj != null ? obj.getId() : null;
    }

    @Override
    public Long getCaseNumberById( long caseId) {
        CaseObject obj = partialGetByCondition("id=?", Collections.singletonList(caseId), "CASENO");
        return obj != null ? obj.getCaseNumber() : null;
    }

    @Override
    public CaseObject getCaseByCaseno(long caseno) {
        return getByCondition("case_object.caseno=?", caseno);
    }

    @Override
    public CaseObject getCase(En_CaseType caseType, long number) {
        return getByCondition("case_type=? and caseno=?", caseType.getId(), number);
    }

    @Override
    public Long insertCase(CaseObject object) {

        En_CaseType type = object.getCaseType();

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
    public int countByQuery(CaseQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return JdbcHelper.getObjectsCount(this.getObjectMapper(), this.jdbcTemplate, parameters);
    }

    @Override
    public String getExternalAppName( Long caseId ) {
        String select = query().
                select( EXT_APP ).from( getTableName() ).where( getIdColumnName() ).equal( caseId ).getSqlCondition();

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
                select( COLUMN_EMAIL_LAST_ID ).from( getTableName() ).where( getIdColumnName() ).equal( caseId ).getSqlCondition();

        Long lastId = jdbcTemplate.queryForObject( selectForUpdate, Long.class, caseId );
        String sql = "UPDATE " + getTableName() + " SET " + COLUMN_EMAIL_LAST_ID + " = " + COLUMN_EMAIL_LAST_ID + "+1 WHERE " + getIdColumnName() + " = " + caseId;
        jdbcTemplate.update(sql);
        return lastId;
    }

    @Override
    public boolean updateNullCreatorByExtAppType(String extAppType) {
        String sql = "UPDATE " + getTableName() + " SET creator = initiator WHERE creator IS NULL AND EXT_APP = ?";
        return jdbcTemplate.update(sql, extAppType) > 0;
    }

    @Override
    public int removeByNameLike(String name) {
        return removeByCondition("CASE_NAME like ?", "%" + name + "%");
    }

    @Override
    public CaseObject getByCaseNameLike(String name) {
        return getByCondition("CASE_NAME like ?", "%" + name + "%");
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
        if (isSearchAtComments(query)) {
            parameters.withDistinct(true);
            parameters.withJoins(LEFT_JOIN_CASE_COMMENT);
        }

        return parameters;
    }
}
