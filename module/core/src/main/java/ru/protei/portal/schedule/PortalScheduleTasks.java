package ru.protei.portal.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import ru.protei.portal.core.service.EmployeeRegistrationReminderService;

import javax.annotation.PostConstruct;

public class PortalScheduleTasks {

    @PostConstruct
    public void init(  ) {

        //   Ежедневно в 11:10
        scheduler.schedule(this::remindAboutEmployeeProbationPeriod, new CronTrigger( "0 10 11 * * ?"));
    }

    public void remindAboutEmployeeProbationPeriod() {
        employeeRegistrationReminderService.notifyAboutProbationPeriod();
        employeeRegistrationReminderService.notifyAboutDevelopmentAgenda();
        employeeRegistrationReminderService.notifyAboutEmployeeFeedback();
    }

    @Autowired
    private ThreadPoolTaskScheduler scheduler;

    @Autowired
    EmployeeRegistrationReminderService employeeRegistrationReminderService;

    private static final Logger log = LoggerFactory.getLogger( PortalScheduleTasks.class );
}
