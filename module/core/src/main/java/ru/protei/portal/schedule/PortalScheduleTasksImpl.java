package ru.protei.portal.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.service.ContractReminderService;
import ru.protei.portal.core.service.EmployeeRegistrationReminderService;
import ru.protei.portal.core.service.IpReservationService;
import ru.protei.portal.core.service.ReportControlService;

import javax.annotation.PostConstruct;

public class PortalScheduleTasksImpl implements PortalScheduleTasks {

    @PostConstruct
    public void init() {
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

    @Scheduled(fixedRate = 60 * 60 * 1000) // every hour
    public void processHangReportsSchedule() {
        reportControlService.processHangReports().ifError(response ->
                log.warn("fail to process reports : status={}", response.getStatus() )
         );
    }

    private void notifyAboutContractDates() {
        contractReminderService.notifyAboutDates();
    }

    public void remindAboutNeedToReleaseIp() {
        ipReservationService.notifyOwnerAboutReleaseIp();
        ipReservationService.notifyAdminsAboutExpiredReleaseDates();
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

    private static final Logger log = LoggerFactory.getLogger( PortalScheduleTasksImpl.class );
}
