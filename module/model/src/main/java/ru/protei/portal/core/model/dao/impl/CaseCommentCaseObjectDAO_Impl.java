package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CaseCommentCaseObjectDAO;
import ru.protei.portal.core.model.ent.CaseCommentCaseObject;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

public class CaseCommentCaseObjectDAO_Impl extends PortalBaseJdbcDAO<CaseCommentCaseObject> implements CaseCommentCaseObjectDAO {

    @Autowired
    CaseObjectSqlBuilder caseObjectSqlBuilder;
    @Autowired
    CaseCommentSqlBuilder caseCommentSqlBuilder;

    @Override
    public Long count(CaseQuery caseQuery, CaseCommentQuery caseCommentQuery) {
        SqlCondition where = makeSqlCondition(caseQuery, caseCommentQuery);
        return (long) getObjectsCount(where.condition, where.args);
    }

    @Override
    public List<CaseCommentCaseObject> getListByQueries(CaseQuery caseQuery, CaseCommentQuery caseCommentQuery) {
        SqlCondition where = makeSqlCondition(caseQuery, caseCommentQuery);
        JdbcQueryParameters parameters = new JdbcQueryParameters();
        if (where.isConditionDefined()) {
            parameters.withCondition(where.condition, where.args);
        }
        parameters.withOffset(caseCommentQuery.getOffset());
        parameters.withLimit(caseCommentQuery.getLimit());
        parameters.withSort(TypeConverters.createSort( caseCommentQuery ));
        return getList(parameters);
    }

    private SqlCondition makeSqlCondition(CaseQuery caseQuery, CaseCommentQuery caseCommentQuery) {
        SqlCondition caseCommentCondition = caseCommentSqlBuilder.createSqlCondition(caseCommentQuery);
        if (caseQuery != null) {
            SqlCondition caseObjectCondition = caseObjectSqlBuilder.caseCommonQuery(caseQuery);
            caseCommentCondition.condition += " AND case_comment.case_id IN (SELECT id FROM case_object WHERE " + caseObjectCondition.condition +")";
            caseCommentCondition.args.addAll(caseObjectCondition.args);
        }
        return caseCommentCondition;
    }
}
