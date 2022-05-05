package ru.protei.portal.test.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CompanyDepartmentDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.dao.WorkerPositionDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.WorkerEntry;
import ru.protei.portal.schedule.PortalScheduleTasks;
import ru.protei.portal.test.api.model.WorkerRecordTestAPI;
import ru.protei.portal.test.api.model.WorkerTestApiEntityFactory;
import ru.protei.portal.test.api.service.WorkerTestApiService;

@RestController
@RequestMapping(value = "/test-api", headers = "Accept=application/json")
@EnableWebMvc
public class PortalTestApiController {

    @Autowired
    PortalScheduleTasks portalScheduleTasks;
    @Autowired
    WorkerTestApiService workerTestApiService;
    @Autowired
    CompanyDepartmentDAO companyDepartmentDAO;
    @Autowired
    WorkerPositionDAO workerPositionDao;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    UserLoginDAO userLoginDAO;

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
    public Result<En_ResultStatus> addWorker(@RequestBody WorkerRecordTestAPI workerRecordTestAPI) {
        Result<En_ResultStatus>  validationResult = workerRecordTestAPI.validateWorkerRecord(workerRecordTestAPI);
        if (validationResult.isError()) {
            return validationResult;
        }

        Person person = WorkerTestApiEntityFactory.createPerson(workerRecordTestAPI);
        WorkerEntry workerEntry = WorkerTestApiEntityFactory.createWorkerEntry(workerRecordTestAPI);
        UserLogin userLogin = WorkerTestApiEntityFactory.createUserLogin(workerRecordTestAPI);

        return workerTestApiService.addWorker(person, workerEntry, userLogin);
    }
}
