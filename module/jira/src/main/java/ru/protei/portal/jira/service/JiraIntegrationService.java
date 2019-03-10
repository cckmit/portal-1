package ru.protei.portal.jira.service;

import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.jira.utils.JiraHookEventData;

public interface JiraIntegrationService {

    @Transactional
    CaseObject create (long companyId, JiraHookEventData event);

    @Transactional
    CaseObject updateOrCreate(long companyId, JiraHookEventData event);
}
