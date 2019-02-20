package ru.protei.portal.jira.handlers;

import ru.protei.portal.core.event.AssembledCaseEvent;

public interface JiraBackchannelHandler {
    void handle(AssembledCaseEvent event);
}
