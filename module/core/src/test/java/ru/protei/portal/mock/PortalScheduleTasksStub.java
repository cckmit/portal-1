package ru.protei.portal.mock;

import ru.protei.portal.schedule.PortalScheduleTasksImpl;

/**
 * Disable scheduled tasks
 */
public class PortalScheduleTasksStub extends PortalScheduleTasksImpl {

    @Override
    public void init() {}

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

    @Override
    public void scheduleProjectPauseTimeNotification( Long projectId, Long pauseDate ) {}
}
