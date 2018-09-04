package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.lang3.StringUtils;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.QuestionnaireDAO;
import ru.protei.portal.core.model.ent.Questionnaire;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.QuestionnaireQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcBaseDAO;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

public class QuestionnaireDAO_Impl extends JdbcBaseDAO<Long, Questionnaire> implements QuestionnaireDAO {

    private static final String JOINS = " JOIN case_object CO ON CO.ID = questionnaire.id ";

    @Override
    public List<Questionnaire> getListByQuery(QuestionnaireQuery query) {
        SqlCondition where = createSqlCondition(query);
        JdbcQueryParameters queryParameters = new JdbcQueryParameters()
                .withJoins(JOINS)
                .withCondition(where.condition, where.args)
                .withDistinct(true)
                .withSort(TypeConverters.createSort(query, "CO.CREATED"))
                .withOffset(query.getOffset());
        if (query.limit > 0) {
            queryParameters = queryParameters.withLimit(query.getLimit());
        }
        return getList(queryParameters);
    }

    @Override
    public int countByQuery(QuestionnaireQuery query) {
        SqlCondition where = createSqlCondition(query);
        return getObjectsCount(where.condition, where.args, JOINS, true);
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(QuestionnaireQuery query) {
        return new SqlCondition().build(((condition, args) -> {
            condition.append("1=1");

            if (StringUtils.isNotEmpty(query.getSearchString())) {
                condition.append(" and (CO.CASE_NAME like ? or questionnaire.post like ?)");
                String likeArg = HelperFunc.makeLikeArg(query.getSearchString(), true);
                args.add(likeArg);
                args.add(likeArg);
            }

            if (CollectionUtils.isNotEmpty(query.getStates())) {
                condition.append(" and CO.state in ");
                condition.append(HelperFunc.makeInArg(query.getStates(), s -> String.valueOf(s.getId())));
            }

            if (query.getCreatedFrom() != null) {
                condition.append(" and questionnaire.employment_date >= ?");
                args.add(query.getCreatedFrom());
            }

            if (query.getCreatedTo() != null) {
                condition.append(" and questionnaire.employment_date <= ?");
                args.add(query.getCreatedTo());
            }
        }));
    }
}
