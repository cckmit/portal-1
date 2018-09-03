package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Questionnaire;
import ru.protei.portal.core.model.query.QuestionnaireQuery;

import java.util.List;

public interface QuestionnaireService {

    @Privileged(En_Privilege.QUESTIONNAIRE_VIEW)
    CoreResponse<Integer> count(AuthToken token, QuestionnaireQuery query);

    @Privileged(En_Privilege.QUESTIONNAIRE_VIEW)
    CoreResponse<List<Questionnaire>> questionnaireList(AuthToken token, QuestionnaireQuery query);

    @Privileged(En_Privilege.QUESTIONNAIRE_VIEW)
    CoreResponse<Questionnaire> getQuestionnaire(AuthToken token, Long id);

    @Privileged(requireAny = En_Privilege.QUESTIONNAIRE_CREATE)
    CoreResponse<Long> createQuestionnaire(AuthToken token, Questionnaire questionnaire);
}
