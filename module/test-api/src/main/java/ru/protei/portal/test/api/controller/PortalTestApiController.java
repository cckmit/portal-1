package ru.protei.portal.test.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.schedule.PortalScheduleTasks;
import ru.protei.portal.test.api.model.WorkerRecordTestAPI;
import ru.protei.portal.test.api.service.WorkerTestApiService;

@RestController
@RequestMapping(value = "/test-api", headers = "Accept=application/json")
@EnableWebMvc
public class PortalTestApiController {

    @Autowired
    PortalScheduleTasks portalScheduleTasks;
    @Autowired
    WorkerTestApiService workerTestApiService;


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

    @PostMapping(value = "/worker/add")
    public ResponseEntity<String> addWorker(@RequestBody WorkerRecordTestAPI workerRecordTestAPI) {
        Result<WorkerRecordTestAPI> result = workerTestApiService.addWorker(workerRecordTestAPI);
        if (result.isError() && result.getStatus() == En_ResultStatus.VALIDATION_ERROR) {
            return new ResponseEntity<>(result.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY );
        }
        if (result.isError()) {
            return new ResponseEntity<>(result.getStatus().name(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
