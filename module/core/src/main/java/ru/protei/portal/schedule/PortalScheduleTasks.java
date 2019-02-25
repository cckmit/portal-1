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

    @Scheduled(cron = "0 0/1 * * * ?")
    public void printInLog() {
        log.info( "printInLog(): {}", new Date() );
        employeeRegistrationServiceImpl.notifyAboutProbationPeriod();
    }
}
