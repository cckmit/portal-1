package ru.protei.portal.redmine.handlers;

import org.springframework.context.event.EventListener;
import ru.protei.portal.core.event.AssembledCaseEvent;

public interface BackchannelEventHandler {
    @EventListener
    void onAssembledCaseEvent(AssembledCaseEvent event);
}
