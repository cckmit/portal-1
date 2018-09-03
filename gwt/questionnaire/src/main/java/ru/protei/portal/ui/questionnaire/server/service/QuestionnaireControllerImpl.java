package ru.protei.portal.ui.questionnaire.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Questionnaire;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.QuestionnaireQuery;
import ru.protei.portal.core.service.QuestionnaireService;
import ru.protei.portal.core.service.QuestionnaireServiceImpl;
import ru.protei.portal.ui.common.client.service.QuestionnaireController;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service("QuestionnaireController")
public class QuestionnaireControllerImpl implements QuestionnaireController {
    @Override
    public List<Questionnaire> getQuestionnaires(QuestionnaireQuery query) throws RequestFailedException {
        log.debug("get questionnaires: offset={} | limit={}", query.getOffset(), query.getLimit());
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<List<Questionnaire>> response = questionnaireService.questionnaireList(descriptor.makeAuthToken(), query);

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Integer getQuestionnaireCount(QuestionnaireQuery query) throws RequestFailedException {
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        log.debug("get questionnaire count(): query={}", query);
        return questionnaireService.count(descriptor.makeAuthToken(), query).getData();
    }

    @Override
    public Questionnaire getQuestionnaire(Long id) throws RequestFailedException {
        log.debug("get questionnaire, id: {}", id);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<Questionnaire> response = questionnaireService.getQuestionnaire(descriptor.makeAuthToken(), id);
        log.debug("get questionnaire, id: {} -> {} ", id, response.isError() ? "error" : response.getData());

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Long createQuestionnaire(Questionnaire questionnaire) throws RequestFailedException {
        if (questionnaire == null) {
            log.warn("null questionnaire in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        log.debug("create questionnaire, id: {}", HelperFunc.nvlt(questionnaire.getId(), "new"));

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        questionnaire.setCreator(descriptor.getPerson());
        CoreResponse<Long> response = questionnaireService.createQuestionnaire(descriptor.makeAuthToken(), questionnaire);

        log.debug("create questionnaire, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.debug("create questionnaire, applied id: {}", response.getData());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    private UserSessionDescriptor getDescriptorAndCheckSession() throws RequestFailedException {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor(httpRequest);
        log.info("userSessionDescriptor={}", descriptor);
        if (descriptor == null) {
            throw new RequestFailedException(En_ResultStatus.SESSION_NOT_FOUND);
        }

        return descriptor;
    }

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpRequest;

    private static final Logger log = LoggerFactory.getLogger(QuestionnaireServiceImpl.class);
}
