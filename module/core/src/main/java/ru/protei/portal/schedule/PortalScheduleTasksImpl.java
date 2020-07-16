package ru.protei.portal.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.PauseTimeOnStartupEvent;
import ru.protei.portal.core.model.dict.En_ReportScheduledType;
import ru.protei.portal.core.service.*;
import ru.protei.portal.core.service.events.EventPublisherService;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PortalScheduleTasksImpl implements PortalScheduleTasks {

    @EventListener
    @Override
    public void onApplicationStartOrRefreshContext( ContextRefreshedEvent event) {
        log.info("onApplicationStartOrRefresh() ContextRefreshedEvent counter {} {}",  contextRefreshedEventCounter.getAndIncrement(), event.getSource());
        if (isPortalStarted.getAndSet( true )) return;

        schedulePauseTimeNotifications();

        if (!config.data().isTaskSchedulerEnabled()) {
            log.info("portal task's scheduler is not started because disabled in configuration");
            return;
        }
        // Ежедневно в 10:00
        scheduler.schedule(this::remindAboutNeedToReleaseIp, new CronTrigger( "0 00 10 * * ?"));
        // Ежедневно в 11:10
        scheduler.schedule(this::remindAboutEmployeeProbationPeriod, new CronTrigger( "0 10 11 * * ?"));
        // Ежедневно в 11:14
        scheduler.schedule(this::notifyAboutContractDates, new CronTrigger("0 14 11 * * ?"));
        // at 06:00:00 am every day
        scheduler.schedule(this::processScheduledMailReportsDaily, new CronTrigger( "0 0 6 * * ?"));
        // at 05:00:00 am every MONDAY
        scheduler.schedule(this::processScheduledMailReportsWeekly, new CronTrigger( "0 0 5 * * MON"));
        // at 10:00:00 am every day
        scheduler.schedule(this::processPersonCaseFilterMailNotification, new CronTrigger( "0 0 10 * * ?"));

    }

    public void remindAboutEmployeeProbationPeriod() {
        employeeRegistrationReminderService.notifyAboutProbationPeriod();
        employeeRegistrationReminderService.notifyAboutDevelopmentAgenda();
        employeeRegistrationReminderService.notifyAboutEmployeeFeedback();
    }

    // Методы для автоматической обработки, контролирования и управления отчетами
    @Scheduled(fixedRate = 30 * 1000) // every 30 seconds
    public void processNewReportsSchedule() {
        reportControlService.processNewReports().ifError(response ->
                log.warn( "fail to process reports : status={}", response.getStatus() )
         );
    }

    @Scheduled(cron = "0 0 5 * * ?") // at 05:00:00 am every day
    public void processOldReportsSchedule() {
        reportControlService.processOldReports().ifError(response ->
                log.warn("fail to process reports : status={}", response.getStatus() )
        );
    }

//    @Scheduled(fixedRate = 60 * 60 * 1000) // every hour
//    public void processHangReportsSchedule() {
//        reportControlService.processHangReports().ifError(response ->
//                log.warn("fail to process reports : status={}", response.getStatus() )
//         );
//    }

    public void processScheduledMailReportsDaily() {
        reportControlService.processScheduledMailReports(En_ReportScheduledType.DAILY).ifError(response ->
                log.warn("fail to process reports : status={}", response.getStatus() )
        );
    }

    public void processScheduledMailReportsWeekly() {
        reportControlService.processScheduledMailReports(En_ReportScheduledType.WEEKLY).ifError(response ->
                log.warn("fail to process reports : status={}", response.getStatus() )
        );
    }

    private void notifyAboutContractDates() {
        contractReminderService.notifyAboutDates();
    }

    public void remindAboutNeedToReleaseIp() {
        log.info("remindAboutNeedToReleaseIp start");
        ipReservationService.notifyOwnersAboutReleaseIp();
        ipReservationService.notifyAdminsAboutExpiredReleaseDates();
        log.info("remindAboutNeedToReleaseIp end");
    }

    public void processPersonCaseFilterMailNotification() {
        personCaseFilterService.processMailNotification();
    }

    @Override
    public void scheduleEvent( ApplicationEvent publishEvent, Date date ) {
        log.info( "scheduleEvent(): date= {} event={}", date, publishEvent );
        scheduler.schedule( () -> publisherService.publishEvent( publishEvent ), date);
    }

    private void schedulePauseTimeNotifications() {
        publisherService.publishEvent( new PauseTimeOnStartupEvent( this ) );
    }

    @Autowired
    PortalConfig config;

    @Autowired
    private ThreadPoolTaskScheduler scheduler;

    @Autowired
    EmployeeRegistrationReminderService employeeRegistrationReminderService;
    @Autowired
    ContractReminderService contractReminderService;
    @Autowired
    ReportControlService reportControlService;
    @Autowired
    IpReservationService ipReservationService;
    @Autowired
    PersonCaseFilterService personCaseFilterService;
    @Autowired
    EventPublisherService publisherService;

    private static AtomicBoolean isPortalStarted = new AtomicBoolean(false);
    private static AtomicInteger contextRefreshedEventCounter = new AtomicInteger(0);

    private static final Logger log = LoggerFactory.getLogger( PortalScheduleTasksImpl.class );
}
