package ru.protei.portal.jira.service;

import org.springframework.context.event.EventListener;
import ru.protei.portal.core.event.AssembledCaseEvent;

public interface JiraBackchannelHandler {
    @EventListener
    void handle(AssembledCaseEvent event);
}
