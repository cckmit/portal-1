package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;

public class ProcessNewReportsEvent extends ApplicationEvent {

    public ProcessNewReportsEvent(Object source) {
        super(source);
    }
}
