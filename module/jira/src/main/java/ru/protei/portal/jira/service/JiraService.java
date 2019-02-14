package ru.protei.portal.jira.service;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.User;
import org.springframework.context.event.EventListener;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.core.model.ent.Person;

public interface JiraService {
    Issue getIssueById(int id, JiraEndpoint endpoint);

    void checkForNewIssues(JiraEndpoint endpoint);

    void checkForUpdatedIssues(JiraEndpoint endpoint);

    void updateIssue(Issue issue, JiraEndpoint endpoint);

    @EventListener
    void onAssembledCaseEvent(AssembledCaseEvent event);

    User findUser(Person person, JiraEndpoint endpoint);
}
