package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.CaseTypeDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.DataQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.*;

import static ru.protei.portal.core.model.dao.impl.CaseShortViewDAO_Impl.isSearchAtComments;

/**
 * Created by michael on 19.05.16.
 */
public class CaseObjectDAO_Impl extends PortalBaseJdbcDAO<CaseObject> implements CaseObjectDAO {

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

    @SqlConditionBuilder
    public SqlCondition caseQueryCondition ( CaseQuery query) {
        return caseObjectSqlBuilder.caseCommonQuery(query);
    }

    @Override
    public Long count(CaseQuery query) {
        if (!isSearchAtComments(query)) {
            return super.count(query);
        }

        String join = " LEFT JOIN case_comment ON case_object.id = case_comment.CASE_ID";
        SqlCondition where = createSqlCondition(query);
        boolean distinct = false;

        return (long) getObjectsCount( where.condition, where.args, join, distinct );
    }
}
