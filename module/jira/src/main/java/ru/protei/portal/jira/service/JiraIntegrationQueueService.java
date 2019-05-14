package ru.protei.portal.jira.service;

import ru.protei.portal.jira.utils.JiraHookEventData;

public interface JiraIntegrationQueueService {

    boolean enqueue(long companyId, JiraHookEventData eventData);
}
