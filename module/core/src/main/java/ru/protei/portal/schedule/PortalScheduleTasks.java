package ru.protei.portal.schedule;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.Date;

public interface PortalScheduleTasks {

    @EventListener
    void onApplicationStartOrRefreshContext( ContextRefreshedEvent event );

    void scheduleEvent( ApplicationEvent projectPauseTimeEvent, Date date  );

}
