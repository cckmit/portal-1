package ru.protei.portal.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.service.ContractReminderService;
import ru.protei.portal.core.service.EmployeeRegistrationReminderService;

import javax.annotation.PostConstruct;

public class PortalScheduleTasks {

    @PostConstruct
    public void init() {
        if (!config.data().isTaskSchedulerEnabled()) {
            log.info("portal task's scheduler is not started because disabled in configuration");
            return;
        }
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

    private void notifyAboutContractDates() {
        contractReminderService.notifyAboutDates();
    }

    @Autowired
    PortalConfig config;

    @Autowired
    private ThreadPoolTaskScheduler scheduler;

    @Autowired
    EmployeeRegistrationReminderService employeeRegistrationReminderService;
    @Autowired
    ContractReminderService contractReminderService;

    private static final Logger log = LoggerFactory.getLogger( PortalScheduleTasks.class );
}
