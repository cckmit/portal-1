package ru.protei.portal.jira.factory;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.ExternalCaseAppDAO;
import ru.protei.portal.core.model.dao.JiraEndpointDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.ExternalCaseAppData;
import ru.protei.portal.core.model.ent.JiraEndpoint;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.jira.utils.CommonUtils;

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
