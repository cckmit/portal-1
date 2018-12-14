package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CaseCommentTimeElapsedSumDAO;
import ru.protei.portal.core.model.ent.CaseCommentTimeElapsedSum;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

public class CaseCommentTimeElapsedSumDAO_Impl extends PortalBaseJdbcDAO<CaseCommentTimeElapsedSum> implements CaseCommentTimeElapsedSumDAO {

    @Autowired
    CaseCommentSqlBuilder caseCommentSqlBuilder;

    @Override
    public List<CaseCommentTimeElapsedSum> getListByQuery(CaseCommentQuery caseCommentQuery) {
        return getList(makeJdbcQueryParameters(caseCommentQuery));
    }

    private JdbcQueryParameters makeJdbcQueryParameters(CaseCommentQuery caseCommentQuery) {
        SqlCondition where = caseCommentSqlBuilder.createSqlCondition(caseCommentQuery);
        JdbcQueryParameters parameters = new JdbcQueryParameters();
        if (where.isConditionDefined()) {
            parameters.withCondition(where.condition, where.args);
        }
        parameters.withOffset(caseCommentQuery.getOffset());
        parameters.withLimit(caseCommentQuery.getLimit());
        parameters.withSort(TypeConverters.createSort(caseCommentQuery));
        parameters.withGroupBy("author_id", "case_id");
        return parameters;
    }
}
