package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseCommentShortViewDAO;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.view.CaseCommentShortView;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcQueryParameters;

/**
 * Created by michael on 19.05.16.
 */
public class CaseCommentShortViewDAO_Impl extends PortalBaseJdbcDAO<CaseCommentShortView> implements CaseCommentShortViewDAO {

    @Autowired
    private CaseCommentSqlBuilder caseCommentSqlBuilder;

    @Override
    public SearchResult<CaseCommentShortView> getSearchResult(CaseCommentQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return getSearchResult(parameters);
    }

    @SqlConditionBuilder
    @Override
    public SqlCondition caseCommentQueryCondition(CaseCommentQuery query) {
        return caseCommentSqlBuilder.createSqlCondition(query);
    }

    private JdbcQueryParameters buildJdbcQueryParameters(CaseCommentQuery query) {

        JdbcQueryParameters parameters = new JdbcQueryParameters();

        SqlCondition where = createSqlCondition(query);
        if (where.isConditionDefined()) {
            parameters.withCondition(where.condition, where.args);
        }

        parameters.withOffset(query.getOffset());
        parameters.withLimit(query.getLimit());
        parameters.withSort(TypeConverters.createSort( query ));

        return parameters;
    }
}
