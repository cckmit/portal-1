package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Questionnaire;
import ru.protei.portal.core.model.query.QuestionnaireQuery;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/QuestionnaireController")
public interface QuestionnaireController extends RemoteService {
    List<Questionnaire> getQuestionnaires(QuestionnaireQuery query) throws RequestFailedException;

    Integer getQuestionnaireCount(QuestionnaireQuery query) throws RequestFailedException;

    Questionnaire getQuestionnaire(Long id) throws RequestFailedException;

    Long createQuestionnaire(Questionnaire questionnaire) throws RequestFailedException;
}
