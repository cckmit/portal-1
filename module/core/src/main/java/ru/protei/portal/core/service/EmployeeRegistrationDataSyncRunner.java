package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.Comment;

import javax.annotation.PostConstruct;

@Component
public class EmployeeRegistrationDataSyncRunner implements Runnable {
    private final Logger log = LoggerFactory.getLogger(EmployeeRegistrationDataSyncRunner.class);

    @Autowired
    YoutrackService youtrackService;

    @Autowired
    PersonDAO personDAO;

    @Autowired
    UserLoginDAO userLoginDAO;

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

    private void updateTwoTask(String issue1, String issue2) {
        ChangeResponse changes1 = youtrackService.getIssueChanges(issue1);
        ChangeResponse changes2 = youtrackService.getIssueChanges(issue1);

        En_CaseState newStatus = getNewStatus(changes1, changes2);
    }

    private En_CaseState getNewStatus(ChangeResponse changes1, ChangeResponse changes2) {
        En_CaseState state1 = toCaseState(changes1.getIssue().getStateId());
        En_CaseState state2 = toCaseState(changes2.getIssue().getStateId());

        if (state1 == null || state2 == null)
            return null;

        if (state1 == state2)
            return state1;

        return En_CaseState.ACTIVE;
    }

    private static En_CaseState toCaseState(String ytStateId) {
        if (ytStateId == null)
            return null;
        switch (ytStateId) {
            case "New": return En_CaseState.CREATED;
            case "Active": return En_CaseState.ACTIVE;
            case "Done": return En_CaseState.DONE;
        }
        return null;
    }

    public CaseComment toCaseComment(Comment ytComment) {
        if (ytComment == null)
            return null;
        UserLogin user = userLoginDAO.findByLogin(ytComment.getAuthor());
        if (user == null) {
            log.error("");
            return null;
        }

        CaseComment caseComment = new CaseComment();
        caseComment.setAuthorId(user.getPersonId());
        caseComment.setCreated(ytComment.getCreated());
        caseComment.setText(ytComment.getText());
        caseComment.set
    }
}
