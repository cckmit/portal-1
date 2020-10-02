package ru.protei.portal.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.SchedulePauseTimeOnStartupEvent;
import ru.protei.portal.core.model.dict.En_ReportScheduledType;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.service.*;
import ru.protei.portal.core.service.autoopencase.AutoOpenCaseService;
import ru.protei.portal.core.service.bootstrap.BootstrapService;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.service.syncronization.EmployeeRegistrationYoutrackSynchronizer;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PortalScheduleTasksImpl implements PortalScheduleTasks {

    @EventListener
    @Override
    public void onApplicationStartOrRefreshContext(ContextRefreshedEvent event) {

        log.info("onApplicationStartOrRefresh() Context refresh counter={} refresh source: {}",  contextRefreshedEventCounter.getAndIncrement(), event.getSource());

        /**
         * Run ONCE tasks
         */
        if (isPortalStarted.getAndSet( true )) return;

        /**
         * Bootstrap data of application
         * First of ALL
         */
        bootstrapService.bootstrapApplication();
        documentService.documentBuildFullIndex();

        /**
         * Scheduled tasks
         */

        if (employeeRegistrationYoutrackSynchronizer.isScheduleSynchronizationNeeded()) {
            String syncCronSchedule = config.data().youtrack().getEmployeeRegistrationSyncSchedule();
            scheduler.schedule( () -> employeeRegistrationYoutrackSynchronizer.synchronizeAll(), new CronTrigger( syncCronSchedule ) );
        }

        autoOpenCaseService.scheduleCaseOpen();

        scheduleReports();

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
        // at 10:00:00 am every day
        scheduler.schedule(this::processPersonCaseFilterMailNotification, new CronTrigger( "0 0 10 * * ?"));
        // at 09:00:00 am every MONDAY
        scheduler.schedule(this::notifyAboutBirthdays, new CronTrigger( "0 0 9 * * MON"));
        // every 5 minutes
        scheduler.scheduleAtFixedRate(mailReceiverService::performReceiveMailAndAddComments, TimeUnit.MINUTES.toMillis(5));

        scheduleNotificationsAboutPauseTime();

        scheduleMailReports();

    }

    public void remindAboutEmployeeProbationPeriod() {
        employeeRegistrationReminderService.notifyAboutProbationPeriod();
        employeeRegistrationReminderService.notifyAboutDevelopmentAgenda();
        employeeRegistrationReminderService.notifyAboutEmployeeFeedback();
    }

    public void scheduleReports() {
        if (HelperFunc.isEmpty(config.data().getCommonConfig().getSystemId())) {
            log.warn("reports is not started because system id not set in configuration");
            return;
        }
        // every 30 seconds
        scheduler.scheduleAtFixedRate(this::processNewReportsSchedule, 30 * 1000);
        // at 05:00:00 am every day
        scheduler.schedule(this::processOldReportsSchedule, new CronTrigger( "0 0 5 * * ?"));
    }

    public void scheduleMailReports() {
        if (HelperFunc.isEmpty(config.data().getCommonConfig().getSystemId())) {
            log.warn("reports is not started because system id not set in configuration");
            return;
        }
        // at 06:00:00 am every day
        scheduler.schedule(this::processScheduledMailReportsDaily, new CronTrigger( "0 0 6 * * ?"));
        // at 05:00:00 am every MONDAY
        scheduler.schedule(this::processScheduledMailReportsWeekly, new CronTrigger( "0 0 5 * * MON"));
    }

    public void processNewReportsSchedule() {
        reportControlService.processNewReports().ifError(response ->
                log.warn( "fail to process reports : status={}", response.getStatus() )
         );
    }

    public void processOldReportsSchedule() {
        reportControlService.processOldReports().ifError(response ->
                log.warn("fail to process reports : status={}", response.getStatus() )
        );
    }

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

    private void scheduleNotificationsAboutPauseTime() {
        log.info( "scheduleNotificationsAboutPauseTime(): ." );
        publisherService.publishEvent( new SchedulePauseTimeOnStartupEvent( this ) );
    }

    private void notifyAboutBirthdays() {
        log.info( "notifyAboutBirthdays" );
        employeeService.notifyAboutBirthdays();
    }

    @Autowired
    PortalConfig config;
    @Autowired
    BootstrapService bootstrapService;

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
    EmployeeService employeeService;
    @Autowired
    PersonCaseFilterService personCaseFilterService;
    @Autowired
    EventPublisherService publisherService;
    @Autowired
    MailReceiverService mailReceiverService;
    @Autowired
    AutoOpenCaseService autoOpenCaseService;
    @Autowired
    DocumentService documentService;
    @Autowired
    EmployeeRegistrationYoutrackSynchronizer employeeRegistrationYoutrackSynchronizer;
    private static AtomicBoolean isPortalStarted = new AtomicBoolean(false);
    private static AtomicInteger contextRefreshedEventCounter = new AtomicInteger(0);

    private static final Logger log = LoggerFactory.getLogger( PortalScheduleTasksImpl.class );
}
