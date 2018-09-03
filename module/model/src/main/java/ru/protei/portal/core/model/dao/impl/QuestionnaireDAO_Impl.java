package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.QuestionnaireDAO;
import ru.protei.portal.core.model.ent.Questionnaire;
import ru.protei.portal.core.model.query.QuestionnaireQuery;
import ru.protei.winter.jdbc.JdbcBaseDAO;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

public class QuestionnaireDAO_Impl extends JdbcBaseDAO<Long, Questionnaire> implements QuestionnaireDAO {

    @Override
    public List<Questionnaire> getListByQuery(QuestionnaireQuery query) {
        JdbcQueryParameters queryParameters = new JdbcQueryParameters()
                .withOffset(query.getOffset());
        if (query.limit > 0) {
            queryParameters = queryParameters.withLimit(query.getLimit());
        }
        return getList(queryParameters);
    }

    @Override
    public int countByQuery(QuestionnaireQuery query) {
        return getObjectsCount();
    }
}
