package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Questionnaire;
import ru.protei.portal.core.model.query.QuestionnaireQuery;
import ru.protei.winter.jdbc.JdbcDAO;

import java.util.List;

public interface QuestionnaireDAO extends JdbcDAO<Long, Questionnaire> {

    List<Questionnaire> getListByQuery(QuestionnaireQuery query);

    int countByQuery(QuestionnaireQuery query);
}
