package ru.protei.portal.jira.factory;

import com.atlassian.jira.event.issue.JiraIssueEvent;
import ru.protei.portal.core.model.ent.CaseObject;

public interface JiraEventTypeHandler {
    public CaseObject handle(JiraIssueEvent event);
}
