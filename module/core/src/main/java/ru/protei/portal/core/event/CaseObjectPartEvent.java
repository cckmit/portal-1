package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;

public class CaseObjectPartEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public CaseObjectPartEvent(Object source) {
        super(source);
    }
}
