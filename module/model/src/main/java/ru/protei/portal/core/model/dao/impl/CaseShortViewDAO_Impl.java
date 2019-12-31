package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseShortViewDAO;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

import static ru.protei.portal.core.model.helper.StringUtils.length;
import static ru.protei.portal.core.model.helper.StringUtils.trim;

/**
 * Created by michael on 19.05.16.
 */
public class CaseShortViewDAO_Impl extends PortalBaseJdbcDAO<CaseShortView> implements CaseShortViewDAO {

    public static final String LEFT_JOIN_CASE_COMMENT = " LEFT JOIN case_comment ON case_object.id = case_comment.CASE_ID";

    @Autowired
    private CaseObjectSqlBuilder caseObjectSqlBuilder;

    @Override
    public SearchResult<CaseShortView> getSearchResult(CaseQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return getSearchResult(parameters);
    }

    @Override
    public List<CaseShortView> partialGetCases(CaseQuery query, String... columns) {
        SqlCondition where = createSqlCondition(query);
        if (where.isConditionDefined()) {
            return partialGetListByCondition(where.condition, where.args, columns);
        } else {
            return partialGetAll(columns);
        }
    }

    @Override
    public CaseShortView getCase(Long caseNo) {
        return getByCondition("case_object.caseno=?", caseNo);
    }

    @Override
    public List<CaseShortView> getListByCompanyId(Long companyId) {
        return getListByCondition("case_object.initiator_company = ?", companyId);
    }

    @SqlConditionBuilder
    public SqlCondition caseQueryCondition ( CaseQuery query) {
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
