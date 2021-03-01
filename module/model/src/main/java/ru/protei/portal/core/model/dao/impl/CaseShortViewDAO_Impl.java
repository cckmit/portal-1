package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseShortViewDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.sqlcondition.Query;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.StringUtils.length;
import static ru.protei.portal.core.model.helper.StringUtils.trim;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

/**
 * Created by michael on 19.05.16.
 */
public class CaseShortViewDAO_Impl extends PortalBaseJdbcDAO<CaseShortView> implements CaseShortViewDAO {

    public static final String LEFT_JOIN_CASE_COMMENT = " LEFT JOIN case_comment ON case_object.id = case_comment.CASE_ID";
    public static final String LEFT_JOIN_CASE_TAG =
            " LEFT JOIN case_object_tag on case_object.ID = case_object_tag.case_id join case_tag on case_tag.id = case_object_tag.tag_id";
    public static final String LEFT_JOIN_PLAN_ORDER =
            " LEFT JOIN plan_to_case_object plan ON case_object.id = plan.case_object_id";

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
    public CaseShortView getCaseByNumber(En_CaseType caseType, Long caseNo) {
        Query q = query()
                .where("case_type").equal(caseType.getId())
                    .and("caseno").equal(caseNo)
                .asQuery();
        return getByCondition(q.buildSql(), q.args());
    }

    @SqlConditionBuilder
    public SqlCondition caseQueryCondition ( CaseQuery query) {
        return caseObjectSqlBuilder.caseCommonQuery(query);
    }

    public static boolean isSearchAtComments(CaseQuery query) {
        return query.isSearchStringAtComments()
                && length(trim( query.getSearchString() )) >= CrmConstants.Issue.MIN_LENGTH_FOR_SEARCH_BY_COMMENTS;
    }

    public static boolean isFilterByTagNames(CaseQuery query) {
        return isNotEmpty(query.getCaseTagsNames());
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

        String joins = "";
        if (isSearchAtComments(query)) {
            joins += LEFT_JOIN_CASE_COMMENT;
        }
        if (isFilterByTagNames(query)) {
            joins += LEFT_JOIN_CASE_TAG;
        }
        if (query.getPlanId() != null) {
            joins += LEFT_JOIN_PLAN_ORDER;
        }
        if (!joins.equals("")) {
            parameters.withDistinct(true);
            parameters.withJoins(joins);
        }

        return parameters;
    }
}
