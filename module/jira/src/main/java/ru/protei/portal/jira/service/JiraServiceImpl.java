package ru.protei.portal.jira.service;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.User;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.core.model.ent.Person;

public class JiraServiceImpl implements JiraService {

    @Override
    public Issue getIssueById(int id, JiraEndpoint endpoint) {
        return null;
    }

    @Override
    public void checkForNewIssues(JiraEndpoint endpoint) {

    }

    @Override
    public void checkForUpdatedIssues(JiraEndpoint endpoint) {

    }

    @Override
    public void updateIssue(Issue issue, JiraEndpoint endpoint) {

    }

    @Override
    public void onAssembledCaseEvent(AssembledCaseEvent event) {

    }

    @Override
    public User findUser(Person person, JiraEndpoint endpoint) {
        return null;
    }
}
