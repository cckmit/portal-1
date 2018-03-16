package ru.protei.portal.redmine.handlers;

import ru.protei.portal.core.event.AssembledCaseEvent;

public interface BackchannelEventHandler {
    void handle(AssembledCaseEvent event);
}
