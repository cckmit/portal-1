package ru.protei.portal.jira.service;

import ru.protei.portal.jira.dto.JiraHookEventData;

public interface JiraIntegrationQueueService {

    boolean enqueue(long companyId, JiraHookEventData eventData);
}
