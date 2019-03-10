package ru.protei.portal.jira.service;

import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.jira.utils.JiraHookEventData;

public interface JiraIntegrationService {

    @Transactional
    CaseObject create (JiraEndpoint endpoint, JiraHookEventData event);

    @Transactional
    CaseObject updateOrCreate(JiraEndpoint endpoint, JiraHookEventData event);
}
