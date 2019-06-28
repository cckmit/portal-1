package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.CaseTypeDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by michael on 19.05.16.
 */
public class CaseObjectDAO_Impl extends PortalBaseJdbcDAO<CaseObject> implements CaseObjectDAO {

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
    public Long getCaseNo(long caseId) {
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
    public List< CaseObject > getCases( CaseQuery query ) {
//        SqlCondition condition = caseQueryCondition( query );
//        return partialGetListByCondition( condition.condition, condition.args, query.offset, query.limit, TypeConverters.createSort( query ),
//                "id", "CASENO", "IMPORTANCE", "STATE", "CREATED", "INFO", "InitiatorName" ).getResults();

        //

        return listByQuery(query);
    }

    @Override
    public List<CaseObject> getCaseIdAndNumbersByCaseNumbers(List<Long> caseNumbers) {
        return partialGetListByCondition("case_object.CASENO in (" + caseNumbers.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ")) + ")", Collections.emptyList(), "id", "CASENO"
        );
    }

    @Override
    public boolean updateEmailLastId(Long caseId, Long emailLastId) {
        String sql = "UPDATE " + getTableName() + " SET " + COLUMN_EMAIL_LAST_ID + " = " + emailLastId + " WHERE " + getIdColumnName() + " = " + caseId;
        return jdbcTemplate.update(sql) > 0;
    }

    @Override
    public boolean updateNullCreatorByExtAppType(String extAppType) {
        String sql = "UPDATE " + getTableName() + " SET creator = initiator WHERE creator IS NULL AND EXT_APP = ?";
        return jdbcTemplate.update(sql, String.class, extAppType) > 0;
    }

    @Override
    public Long getEmailLastId(Long caseId) {
        String sql = "SELECT " + COLUMN_EMAIL_LAST_ID + " FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, caseId);
    }

    @SqlConditionBuilder
    public SqlCondition caseQueryCondition ( CaseQuery query) {
        return caseObjectSqlBuilder.caseCommonQuery(query);
    }
}
