package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;

public class SchedulePauseTimeOnStartupEvent extends ApplicationEvent {

    public SchedulePauseTimeOnStartupEvent( Object source ) {
        super( source );
    }

}
