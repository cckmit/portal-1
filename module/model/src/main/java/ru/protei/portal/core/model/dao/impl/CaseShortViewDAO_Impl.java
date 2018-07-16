package ru.protei.portal.core.model.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseShortViewDAO;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.length;
import static ru.protei.portal.core.model.helper.CollectionUtils.trim;

/**
 * Created by michael on 19.05.16.
 */
public class CaseShortViewDAO_Impl extends PortalBaseJdbcDAO<CaseShortView> implements CaseShortViewDAO {

    @Autowired
    private CaseObjectSqlBuilder caseObjectSqlBuilder;

    @Override
    public List< CaseShortView > getCases( CaseQuery query ) {
        String join = "";
        if (isSearchAtComments(query)) {
            join += " LEFT JOIN case_comment ON case_object.id = case_comment.CASE_ID";
        }

        SqlCondition where = createSqlCondition(query);

        JdbcQueryParameters parameters = new JdbcQueryParameters();
        if (where.isConditionDefined())
            parameters.withCondition(where.condition, where.args);

        parameters.withOffset(query.getOffset());
        parameters.withLimit(query.getLimit());
        parameters.withSort(TypeConverters.createSort( query ));
        parameters.withJoins( join );

        return getList(parameters);
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

    @SqlConditionBuilder
    public SqlCondition caseQueryCondition ( CaseQuery query) {
        return caseObjectSqlBuilder.caseCommonQuery(query);
    }

    public static boolean isSearchAtComments(CaseQuery query) {
        return query.isSearchStringAtComments()
                && length(trim( query.getSearchString() )) >= CrmConstants.Issue.MIN_LENGTH_FOR_SEARCH_BY_COMMENTS;
    }
}
