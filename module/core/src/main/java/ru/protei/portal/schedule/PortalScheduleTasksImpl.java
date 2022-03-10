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
import ru.protei.portal.core.event.SchedulePauseTimeOnStartupEvent;
import ru.protei.portal.core.model.dict.En_ReportScheduledType;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.service.*;
import ru.protei.portal.core.service.autoopencase.AutoOpenCaseService;
import ru.protei.portal.core.service.bootstrap.BootstrapService;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.service.syncronization.EmployeeRegistrationYoutrackSynchronizer;

import java.time.LocalDate;
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

        if (config.data().getAutoOpenConfig().getEnable()) {
            autoOpenCaseService.scheduleCaseOpen();
        } else {
            log.debug("Case open is disabled in config");
        }

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
        // at 08:00:00 am every day
        scheduler.schedule(this::notifyExpiringTechnicalSupportValidity, new CronTrigger( "0 0 8 * * ?"));
        // at 09:00:00 am every MONDAY
        scheduler.schedule(this::notifyAboutBirthdays, new CronTrigger( "0 0 9 * * MON"));
        // every 5 minutes
        scheduler.scheduleAtFixedRate(mailReceiverService::performReceiveMailAndAddComments, TimeUnit.MINUTES.toMillis(5));
        // at 06:00:00 am every day
        scheduler.schedule(this::processScheduledMailReportsDaily, new CronTrigger( "0 0 6 * * ?"));
        // at 05:00:00 am every MONDAY
        scheduler.schedule(this::processScheduledMailReportsWeekly, new CronTrigger( "0 0 5 * * MON"));
        // at 04:04:00 am every day
        scheduler.schedule(this::updateFiredByDate, new CronTrigger( "0 04 4 * * ?"));
        // at 04:00:00 am every day
        scheduler.schedule(this::updatePositionByDate, new CronTrigger( "0 0 4 * * ?"));

        scheduleNotificationsAboutPauseTime();
    }

    @Override
    public void remindAboutEmployeeProbationPeriod() {
        employeeRegistrationReminderService.notifyAboutProbationPeriod();
        employeeRegistrationReminderService.notifyAboutDevelopmentAgenda();
        employeeRegistrationReminderService.notifyAboutEmployeeFeedback();
    }

    @Scheduled(fixedRate = 30 * 1000) // every 30 seconds
    public void processNewReportsSchedule() {
        if (isNotConfiguredSystemId()) {
            return;
        }
        reportControlService.processNewReports().ifError(response ->
                log.warn( "fail to process reports : status={}", response.getStatus() )
         );
    }

    @Scheduled(cron = "0 0 5 * * ?") // at 05:00:00 am every day
    public void processOldReportsSchedule() {
        if (isNotConfiguredSystemId()) {
            return;
        }
        reportControlService.processOldReports().ifError(response ->
                log.warn("fail to process reports : status={}", response.getStatus() )
        );
    }

    @Override
    public void processScheduledMailReportsDaily() {
        if (isNotConfiguredSystemId()) {
            return;
        }
        reportControlService.processScheduledMailReports(En_ReportScheduledType.DAILY).ifError(response ->
                log.warn("fail to process reports : status={}", response.getStatus() )
        );
    }

    @Override
    public void processScheduledMailReportsWeekly() {
        if (isNotConfiguredSystemId()) {
            return;
        }
        reportControlService.processScheduledMailReports(En_ReportScheduledType.WEEKLY).ifError(response ->
                log.warn("fail to process reports : status={}", response.getStatus() )
        );
    }

    @Override
    public void notifyAboutContractDates() {
        contractReminderService.notifyAboutDates();
    }

    @Override
    public void remindAboutNeedToReleaseIp() {
        log.info("remindAboutNeedToReleaseIp start");
        ipReservationService.notifyOwnersAboutReleaseIp();
        ipReservationService.notifyAdminsAboutExpiredReleaseDates();
        log.info("remindAboutNeedToReleaseIp end");
    }

    @Override
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

    @Override
    public void notifyAboutBirthdays() {
        log.info( "notifyAboutBirthdays" );
        employeeService.notifyAboutBirthdays();
    }

    @Override
    public void notifyExpiringTechnicalSupportValidity() {
        log.info( "notifyExpiringTechnicalSupportValidity" );
        projectService.notifyExpiringProjectTechnicalSupportValidity(LocalDate.now());
    }

    @Override
    public void updateFiredByDate() {
        log.info( "updateFiredByDate" );
        workerEntryService.updateFiredByDate(new Date());
    }

    @Override
    public void updatePositionByDate() {
        log.info( "updatePositionByDate" );
        workerEntryService.updatePositionByDate(new Date());
    }

    private boolean isNotConfiguredSystemId() {
        if (HelperFunc.isEmpty(config.data().getCommonConfig().getSystemId())) {
            log.warn("reports is not started because system.id not set in configuration");
            return true;
        }
        return false;
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
    ProjectService projectService;
    @Autowired
    EmployeeRegistrationYoutrackSynchronizer employeeRegistrationYoutrackSynchronizer;
    @Autowired
    WorkerEntryService workerEntryService;

    private static AtomicBoolean isPortalStarted = new AtomicBoolean(false);
    private static AtomicInteger contextRefreshedEventCounter = new AtomicInteger(0);

    private static final Logger log = LoggerFactory.getLogger( PortalScheduleTasksImpl.class );
}
