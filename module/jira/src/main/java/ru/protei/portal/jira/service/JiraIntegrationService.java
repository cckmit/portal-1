package ru.protei.portal.jira.service;

import com.atlassian.jira.rest.client.api.domain.Issue;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.jira.dto.JiraHookEventData;

import java.util.concurrent.CompletableFuture;

public interface JiraIntegrationService {

    Result<JiraEndpoint> selectEndpoint( Issue issue, Long originalCompanyId );

    @Transactional
    CompletableFuture<AssembledCaseEvent> create ( JiraEndpoint endpoint, JiraHookEventData event);

    @Transactional
    CompletableFuture<AssembledCaseEvent> updateOrCreate(JiraEndpoint endpoint, JiraHookEventData event);
}
