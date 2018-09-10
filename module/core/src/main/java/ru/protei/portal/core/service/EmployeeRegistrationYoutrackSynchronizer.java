package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.DateUtils;
import ru.protei.portal.core.model.query.CaseLinkQuery;
import ru.protei.portal.core.model.yt.Change;
import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.Comment;
import ru.protei.portal.core.model.yt.fields.change.StringArrayWithIdArrayOldNewChangeField;

import java.util.Date;
import java.util.LinkedList;
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
    private CaseCommentDAO caseCommentDAO;

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
        List<EmployeeRegistration> employeeRegistrations = employeeRegistrationDAO.getAll();
        for (EmployeeRegistration employeeRegistration : employeeRegistrations) {
            synchronizeEmployeeRegistration(employeeRegistration);
        }
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

        parseChanges(employeeRegistration, issueId, changes.getChange());
    }

    private void updateTwoIssues(EmployeeRegistration employeeRegistration, String issue1, String issue2) {
        ChangeResponse changes1 = youtrackService.getIssueChanges(issue1);
        ChangeResponse changes2 = youtrackService.getIssueChanges(issue2);

        En_CaseState newStatus = getNewStatus(changes1, changes2);
        employeeRegistration.setState(newStatus);

        parseChanges(employeeRegistration, issue1, changes1.getChange());
        parseChanges(employeeRegistration, issue2, changes2.getChange());
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

    @Transactional
    public boolean saveEmployeeRegistration(EmployeeRegistration employeeRegistration) {
        CaseObject caseObject = caseObjectDAO.get(employeeRegistration.getId());
        caseObject.setModified(employeeRegistration.getLastYoutrackSynchronization());
        caseObject.setState(employeeRegistration.getState());
        return caseObjectDAO.merge(caseObject) && employeeRegistrationDAO.merge(employeeRegistration);
    }

    private void parseChanges(EmployeeRegistration employeeRegistration, String issueId, List<Change> changes) {
        List<CaseComment> stateChanges = new LinkedList<>();
        for (Change change : changes) {
            if (change == null)
                continue;
            if (DateUtils.beforeNotNull(change.getUpdated(), employeeRegistration.getLastYoutrackSynchronization()))
                continue;

            StringArrayWithIdArrayOldNewChangeField stateChangeField = change.getStateChangeField();

            if (stateChangeField == null)
                continue;

            En_CaseState newState = toCaseState(stateChangeField.getNewValue());

            CaseComment stateChange = new CaseComment();
            stateChange.setCaseId(employeeRegistration.getId());
            stateChange.setCreated(change.getUpdated());
            stateChange.setAuthorId(findPersonIdByLogin(change.getUpdaterName()));
            stateChange.setCaseStateId((long) newState.getId());
            stateChange.setText(issueId);
            stateChanges.add(stateChange);
        }
        caseCommentDAO.persistBatch(stateChanges);
    }

    private CaseComment toCaseComment(Comment ytComment) {
        if (ytComment == null)
            return null;
        CaseComment caseComment = new CaseComment();
        caseComment.setAuthorId(findPersonIdByLogin(ytComment.getAuthor()));
        caseComment.setCreated(ytComment.getCreated());
        caseComment.setText(ytComment.getText());
        return caseComment;
    }

    private Long findPersonIdByLogin(String login) {
        if (login == null)
            return null;
        UserLogin user = userLoginDAO.findByLogin(login);
        return user == null ? null : user.getPersonId();
    }

    private void createAndStoreStatusChangeComment(Long personId, Long caseId, En_CaseState caseState, String youtrackIssueId) {
        CaseComment caseComment = new CaseComment();
        caseComment.setText(youtrackIssueId);
        caseComment.setCaseStateId((long) caseState.getId());
        caseComment.setCaseId(caseId);
        caseComment.setCreated(new Date());

    }

    private static En_CaseState toCaseState(String ytStateId) {
        if (ytStateId == null)
            return null;
        switch (ytStateId) {
            case "New": return En_CaseState.CREATED;
            case "Done": return En_CaseState.DONE;
            default: return En_CaseState.ACTIVE;
        }
    }

    private static En_CaseState toCaseState(List<String> ytStateIds) {
        if (ytStateIds == null || ytStateIds.size() != 1)
            return null;
        return toCaseState(ytStateIds.get(0));
    }
}
