package ru.protei.portal.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import ru.protei.portal.core.service.EmployeeRegistrationService;

import java.util.Date;

public class PortalScheduleTasks {

    @Autowired
    EmployeeRegistrationService employeeRegistrationServiceImpl;

    private static final Logger log = LoggerFactory.getLogger( PortalScheduleTasks.class );

    //   Ежедневно в 11:10
    @Scheduled(cron = "0 10 11 * * ?")
    public void remindAboutEmployeeProbationPeriod() {
        employeeRegistrationServiceImpl.notifyAboutProbationPeriod();
        employeeRegistrationServiceImpl.notifyAboutDevelopmentAgenda();
        employeeRegistrationServiceImpl.notifyAboutEmployeeFeedback();
    }
}
