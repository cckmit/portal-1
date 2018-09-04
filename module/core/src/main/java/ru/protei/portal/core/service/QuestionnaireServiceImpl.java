package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.CaseTypeDAO;
import ru.protei.portal.core.model.dao.QuestionnaireDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Questionnaire;
import ru.protei.portal.core.model.query.QuestionnaireQuery;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.Date;
import java.util.List;

public class QuestionnaireServiceImpl implements QuestionnaireService {

    @Autowired
    QuestionnaireDAO questionnaireDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseTypeDAO caseTypeDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Override
    public CoreResponse<Integer> count(AuthToken token, QuestionnaireQuery query) {
        return new CoreResponse<Integer>().success(questionnaireDAO.countByQuery(query));
    }

    @Override
    public CoreResponse<List<Questionnaire>> questionnaireList(AuthToken token, QuestionnaireQuery query) {
        List<Questionnaire> list = questionnaireDAO.getListByQuery(query);
        if (list == null) {
            return new CoreResponse<List<Questionnaire>>().error(En_ResultStatus.INTERNAL_ERROR);
        }
        return new CoreResponse<List<Questionnaire>>().success(list);
    }

    @Override
    public CoreResponse<Questionnaire> getQuestionnaire(AuthToken token, Long id) {
        Questionnaire questionnaire = questionnaireDAO.get(id);
        if (questionnaire == null)
            return new CoreResponse<Questionnaire>().error(En_ResultStatus.NOT_FOUND);

        jdbcManyRelationsHelper.fillAll(questionnaire);
        return new CoreResponse<Questionnaire>().success(questionnaire);
    }

    @Override
    @Transactional
    public CoreResponse<Long> createQuestionnaire(AuthToken token, Questionnaire questionnaire) {
        if (questionnaire == null)
            return new CoreResponse<Long>().error(En_ResultStatus.INCORRECT_PARAMS);

        CaseObject caseObject = createCaseObjectFromQuestionnaire(questionnaire);
        Long id = caseObjectDAO.persist(caseObject);
        if (id == null)
            return new CoreResponse<Long>().error(En_ResultStatus.INTERNAL_ERROR);

        questionnaire.setId(id);
        questionnaireDAO.persist(questionnaire);

        return new CoreResponse<Long>().success(id);
    }

    private CaseObject createCaseObjectFromQuestionnaire(Questionnaire questionnaire) {
        CaseObject caseObject = new CaseObject();
        caseObject.setCaseType(En_CaseType.QUESTIONNAIRE);
        caseObject.setState(En_CaseState.ACTIVE);
        caseObject.setCaseNumber(caseTypeDAO.generateNextId(En_CaseType.QUESTIONNAIRE));
        caseObject.setCreated(new Date());

        caseObject.setInfo(questionnaire.getComment());
        caseObject.setInitiatorId(questionnaire.getHeadOfDepartmentId());
        caseObject.setCreatorId(questionnaire.getCreatorId());
        caseObject.setName(questionnaire.getEmployeeFullName());
        return caseObject;
    }
}
