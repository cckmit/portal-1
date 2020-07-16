package ru.protei.portal.mock;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import ru.protei.portal.schedule.PortalScheduleTasksImpl;

import java.util.Date;

/**
 * Disable scheduled tasks
 */
public class PortalScheduleTasksStub extends PortalScheduleTasksImpl {

//    @Override
//    public void init() {}


    @Override
    public void onApplicationStartOrRefreshContext( ContextRefreshedEvent event ) {

    }

    @Override
    public void processNewReportsSchedule() {}

    @Override
    public void processOldReportsSchedule() {}

//    @Override
//    public void processHangReportsSchedule() {}

    @Override
    public void processScheduledMailReportsWeekly() {}

    @Override
    public void processScheduledMailReportsDaily() {}

//    @Override
//    public void scheduleProjectPauseTimeNotification( Long projectId, Long pauseDate ) {}

    @Override
    public void scheduleEvent( ApplicationEvent publishEvent, Date date ) {

    }
}
