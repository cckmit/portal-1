package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseShortViewDAO;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.utils.TypeConverters;
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
    public List< CaseShortView > getCases( CaseQuery query ) {
        SqlCondition where = createSqlCondition(query);

        JdbcQueryParameters parameters = new JdbcQueryParameters();
        if (where.isConditionDefined())
            parameters.withCondition(where.condition, where.args);

        parameters.withOffset(query.getOffset());
        parameters.withLimit(query.getLimit());
        parameters.withSort(TypeConverters.createSort( query ));
        if (isSearchAtComments(query)) {
            parameters.withDistinct(true);
            parameters.withJoins(LEFT_JOIN_CASE_COMMENT);
        }

        return getList(parameters);
    }

    @Override
    public CaseShortView getCase(Long caseNo) {
        return getByCondition("case_object.caseno=?", caseNo);
    }

    @Override
    public Long count(CaseQuery query) {
        if (!isSearchAtComments(query)) {
            return super.count(query);
        }
        StringBuilder sql = new StringBuilder("select count(distinct case_object.id) from ").append(getTableName())
                .append(LEFT_JOIN_CASE_COMMENT);

        SqlCondition whereCondition = createSqlCondition(query);

        if (!whereCondition.condition.isEmpty()) {
            sql.append(" where ").append(whereCondition.condition);
        }

        return jdbcTemplate.queryForObject(sql.toString(), Long.class, whereCondition.args.toArray());
    }

    @SqlConditionBuilder
    public SqlCondition caseQueryCondition ( CaseQuery query) {
        return caseObjectSqlBuilder.caseCommonQuery(query);
    }

    public static boolean isSearchAtComments(CaseQuery query) {
        return query.isSearchStringAtComments()
                && length(trim( query.getSearchString() )) >= CrmConstants.Issue.MIN_LENGTH_FOR_SEARCH_BY_COMMENTS;
    }
}
