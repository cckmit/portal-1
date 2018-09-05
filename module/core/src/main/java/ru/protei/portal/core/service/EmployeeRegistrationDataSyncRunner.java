package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.yt.ChangeResponse;

import javax.annotation.PostConstruct;

@Component
public class EmployeeRegistrationDataSyncRunner implements Runnable {
    @Autowired
    YoutrackService youtrackService;

    @Autowired
    public EmployeeRegistrationDataSyncRunner(ThreadPoolTaskScheduler scheduler, PortalConfig config) {
        CronTrigger cronTrigger = new CronTrigger(config.data().youtrack().getEmployeeRegistrationSyncSchedule());
        scheduler.schedule(this, cronTrigger);
    }

    @Override
    public void run() {
    }

    @PostConstruct
    public void __debug() {
        try {
            ChangeResponse pg53Changes = youtrackService.getIssueChanges("PG-53");
            ChangeResponse pg49Changes = youtrackService.getIssueChanges("PG-49");
            System.out.println("oloo");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
