package ru.protei.portal.jira.handlers;

import com.atlassian.jira.event.issue.IssueEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.protei.portal.jira.factory.JiraEventTypeHandlersFactory;

@Component
public class JiraEventHandlerImpl {
    private static final Logger log = LoggerFactory.getLogger(JiraEventHandlerImpl.class);

    @Autowired
    JiraEventTypeHandlersFactory factory;

    @EventListener
    public void onIssueEvent(IssueEvent issueEvent) {
        JiraEventTypeHandlersFactory.returnHandler(issueEvent.getEventTypeId()).handle(issueEvent);
    }
}
