package ru.protei.portal.jira.service;

import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.jira.dto.JiraHookEventData;

public interface JiraIntegrationQueueService {

    boolean enqueue(JiraEndpoint endpoint, JiraHookEventData eventData);
}
