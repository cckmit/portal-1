package ru.protei.portal.jira.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class JiraEventTypeHandlersFactory {
    private static final Logger logger = LoggerFactory.getLogger(JiraEventTypeHandlersFactory.class);

    private static final Map<String, JiraEventTypeHandler> handlers =
            new HashMap<String, JiraEventTypeHandler>() {{
                put("jira:issue_created", new JiraIssueCreatedEventHandler());
                put("jira:issue_updated", new JiraIssueUpdatedEventHandler());
            }};

    public static JiraEventTypeHandler returnHandler(String eventType) {
        return handlers.getOrDefault(eventType,
                x -> {logger.debug("No handler found for event type id {}", x); return null;});
    }
}
