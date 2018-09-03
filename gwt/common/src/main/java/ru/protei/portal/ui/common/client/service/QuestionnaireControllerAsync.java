package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Questionnaire;
import ru.protei.portal.core.model.query.QuestionnaireQuery;

import java.util.List;

public interface QuestionnaireControllerAsync {
    void getQuestionnaires(QuestionnaireQuery query, AsyncCallback<List<Questionnaire>> callback);

    void getQuestionnaireCount(QuestionnaireQuery query, AsyncCallback<Integer> callback);

    void getQuestionnaire(Long id, AsyncCallback<Questionnaire> callback);

    void createQuestionnaire(Questionnaire questionnaire, AsyncCallback<Long> callback);
}
