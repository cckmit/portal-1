package ru.protei.portal.test.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.protei.portal.schedule.PortalScheduleTasks;

@RestController
@RequestMapping(value = "/test-api", headers = "Accept=application/json")
@EnableWebMvc
public class PortalTestApiController {

    @Autowired
    PortalScheduleTasks portalScheduleTasks;

    @GetMapping(value = "/case-filter/notification")
    public void processPersonCaseFilterMailNotification () {
        portalScheduleTasks.processPersonCaseFilterMailNotification();
    }

    @GetMapping(value = "/daily-report/notification")
    public void processScheduledMailReportsDaily () {
        portalScheduleTasks.processScheduledMailReportsDaily();
    }

    @GetMapping(value = "/weekly-report/notification")
    public void processScheduledMailReportsWeekly () {
        portalScheduleTasks.processScheduledMailReportsWeekly();
    }

    @GetMapping(value = "/release-ip/notification")
    public void remindAboutNeedToReleaseIp () {
        portalScheduleTasks.remindAboutNeedToReleaseIp();
    }

    @GetMapping(value = "/employee-probation-period/notification")
    public void remindAboutEmployeeProbationPeriod () {
        portalScheduleTasks.remindAboutEmployeeProbationPeriod();
    }

    @GetMapping(value = "/contract-date/notification")
    public void notifyAboutContractDates () {
        portalScheduleTasks.notifyAboutContractDates();
    }

    @GetMapping(value = "/birthday/notification")
    public void notifyAboutBirthdays () {
        portalScheduleTasks.notifyAboutBirthdays();
    }

    @GetMapping(value = "/technical-support-validity/notification")
    public void notifyExpiringTechnicalSupportValidity () {
        portalScheduleTasks.notifyExpiringTechnicalSupportValidity();
    }

    @GetMapping(value = "/employee/fire-by-date")
    public void updateFiredByDate () {
        portalScheduleTasks.updateFiredByDate();
    }

    @GetMapping(value = "/employee/update-position-by-date")
    public void updatePositionByDate () {
        portalScheduleTasks.updatePositionByDate();
    }

    @GetMapping(value = "/issue/auto-close-by-deadline")
    public void processAutoClose() {
        portalScheduleTasks.processAutoCloseByDeadLine();
    }

    @GetMapping(value = "/issue/deadline-expire/notification")
    public void notifyAboutDeadline() {
        portalScheduleTasks.notifyAboutDeadlineExpire();
    }
}
