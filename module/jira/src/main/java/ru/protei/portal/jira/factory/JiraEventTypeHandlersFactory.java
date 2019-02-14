package ru.protei.portal.jira.factory;

import com.atlassian.jira.event.issue.JiraIssueEvent;
import com.atlassian.jira.event.type.EventType;
import ru.protei.portal.core.model.ent.CaseObject;

import java.util.HashMap;
import java.util.Map;

public class JiraEventTypeHandlersFactory {
    private static final Map<Long, JiraEventTypeHandler> handlers =
            new HashMap<Long, JiraEventTypeHandler>() {{
                put(EventType.ISSUE_CREATED_ID, new JiraIssueCreatedEventHandler());
                put(EventType.)
            }};

    public static JiraEventTypeHandler returnHandler(long eventTypeId) {

    }

    public static class JiraIssueCreatedEventHandler implements JiraEventTypeHandler {

        @Override
        public CaseObject handle(JiraIssueEvent event) {
            return null;
        }
    }

}
