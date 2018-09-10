package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.CaseLinkDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.EmployeeRegistrationDAO;
import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseLinkQuery;
import ru.protei.portal.core.model.yt.Change;
import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.Comment;

import java.util.Date;
import java.util.List;

@Component
public class EmployeeRegistrationYoutrackSynchronizer {
    private final Logger log = LoggerFactory.getLogger(EmployeeRegistrationYoutrackSynchronizer.class);

    @Autowired
    private YoutrackService youtrackService;

    @Autowired
    private UserLoginDAO userLoginDAO;

    @Autowired
    private CaseLinkDAO caseLinkDAO;

    @Autowired
    private EmployeeRegistrationDAO employeeRegistrationDAO;

    @Autowired
    private CaseObjectDAO caseObjectDAO;


    @Autowired
    public EmployeeRegistrationYoutrackSynchronizer(ThreadPoolTaskScheduler scheduler, PortalConfig config) {
        String syncCronSchedule = config.data().youtrack().getEmployeeRegistrationSyncSchedule();
        scheduler.schedule(this::synchronizeAll, new CronTrigger(syncCronSchedule));
    }

    private void synchronizeAll() {
        log.debug("synchronizeAll(): start synchronization");
        employeeRegistrationDAO.getAll().forEach(this::synchronizeEmployeeRegistration);
    }

    @Transactional
    public void synchronizeEmployeeRegistration(EmployeeRegistration employeeRegistration) {
        if (employeeRegistration == null)
            return;

        log.debug("synchronizeEmployeeRegistration(): start synchronizing employee registration {}", employeeRegistration);
        Date synchronizationStarted = new Date();

        List<CaseLink> issues = caseLinkDAO.getListByQuery(new CaseLinkQuery(employeeRegistration.getId(), En_CaseLink.YT));
        if (CollectionUtils.isEmpty(issues))
            return;
        if (issues.size() > 2) {
            log.warn("synchronizeEmployeeRegistration(): found more than 2 linked YouTrack issues for {}", employeeRegistration);
            return;
        }

        if (issues.size() == 1) {
            updateOneIssue(employeeRegistration, issues.get(0).getRemoteId());
        } else if (issues.size() == 2) {
            updateTwoIssues(employeeRegistration, issues.get(0).getRemoteId(), issues.get(1).getRemoteId());
        }

        employeeRegistration.setLastYoutrackSynchronization(synchronizationStarted);

        if (!saveEmployeeRegistration(employeeRegistration))
            log.warn("synchronizeEmployeeRegistration(): failed to execute DB merge for employee registration={}", employeeRegistration);
    }

    private void updateOneIssue(EmployeeRegistration employeeRegistration, String issueId) {
        ChangeResponse changes = youtrackService.getIssueChanges(issueId);

        En_CaseState newStatus = getNewStatus(changes);
        employeeRegistration.setState(newStatus);

        parseChanges(employeeRegistration, changes.getChange());
    }

    private void updateTwoIssues(EmployeeRegistration employeeRegistration, String issue1, String issue2) {
        ChangeResponse changes1 = youtrackService.getIssueChanges(issue1);
        ChangeResponse changes2 = youtrackService.getIssueChanges(issue2);

        En_CaseState newStatus = getNewStatus(changes1, changes2);
        employeeRegistration.setState(newStatus);

        List<Change> changes = changes1.getChange();
        changes.addAll(changes2.getChange());
        parseChanges(employeeRegistration, changes);
    }

    private En_CaseState getNewStatus(ChangeResponse changes) {
        En_CaseState newStatus = toCaseState(changes.getIssue().getStateId());
        if (newStatus == null || newStatus == En_CaseState.DONE || newStatus == En_CaseState.CREATED)
            return newStatus;
        return En_CaseState.ACTIVE;
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

    @Transactional
    public boolean saveEmployeeRegistration(EmployeeRegistration employeeRegistration) {
        CaseObject caseObject = caseObjectDAO.get(employeeRegistration.getId());
        caseObject.setModified(employeeRegistration.getLastYoutrackSynchronization());
        caseObject.setState(employeeRegistration.getState());
        return caseObjectDAO.merge(caseObject) && employeeRegistrationDAO.merge(employeeRegistration);
    }

    private void parseChanges(EmployeeRegistration employeeRegistration, List<Change> changes) {
        for (Change change : changes) {
            if (change == null)
                continue;
            if (change.getUpdated() != null && change.getUpdated().before(employeeRegistration.getLastYoutrackSynchronization()))
                continue;

        }
    }

    private CaseComment toCaseComment(Comment ytComment) {
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
        return caseComment;
    }
}
