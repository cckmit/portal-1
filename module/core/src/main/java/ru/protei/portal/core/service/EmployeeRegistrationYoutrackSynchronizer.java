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

import java.util.*;
import java.util.stream.Collectors;

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

    private static En_CaseState toCaseState(String ytStateId) {
        if (ytStateId == null)
            return null;
        switch (ytStateId) {
            case "New":
                return En_CaseState.CREATED;
            case "Done":
                return En_CaseState.DONE;
            default:
                return En_CaseState.ACTIVE;
        }
    }

    private static En_CaseState toCaseState(List<String> ytStateIds) {
        if (ytStateIds == null || ytStateIds.size() != 1)
            return null;
        return toCaseState(ytStateIds.get(0));
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
            updateIssues(employeeRegistration, issues.get(0).getRemoteId());
        } else if (issues.size() == 2) {
            updateIssues(employeeRegistration, issues.get(0).getRemoteId(), issues.get(1).getRemoteId());
        }

        employeeRegistration.setLastYoutrackSynchronization(synchronizationStarted);

        if (!saveEmployeeRegistration(employeeRegistration))
            log.warn("synchronizeEmployeeRegistration(): failed to execute DB merge for employee registration={}", employeeRegistration);
    }

    private void updateIssues(EmployeeRegistration employeeRegistration, String... issueIds) {
        Map<String, ChangeResponse> issueToChanges = new HashMap<>();

        for (String issueId : issueIds) {
            ChangeResponse issueChanges = youtrackService.getIssueChanges(issueId);
            issueToChanges.put(issueId, issueChanges);
        }

        En_CaseState state = getGeneralState(issueToChanges.values());
        employeeRegistration.setState(state);

        for (String issueId : issueIds) {
            ChangeResponse changes = issueToChanges.get(issueId);
            if (changes == null)
                continue;
            parseChanges(employeeRegistration, issueId, changes.getChange());
            parseAndUpdateComments(employeeRegistration.getId(), employeeRegistration.getLastYoutrackSynchronization(), changes.getIssue().getComment());
        }
    }

    private En_CaseState getGeneralState(Collection<ChangeResponse> changes) {
        List<En_CaseState> caseStates = changes.stream()
                .map(change -> toCaseState(change.getIssue().getStateId()))
                .collect(Collectors.toList());

        if (caseStates.stream().allMatch(cs -> cs == En_CaseState.DONE))
            return En_CaseState.DONE;
        if (caseStates.stream().allMatch(cs -> cs == En_CaseState.CREATED))
            return En_CaseState.CREATED;
        return En_CaseState.ACTIVE;
    }

    private boolean saveEmployeeRegistration(EmployeeRegistration employeeRegistration) {
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

    private void parseAndUpdateComments(Long caseId, Date lastSynchronization, List<Comment> comments) {
        List<CaseComment> commentsToAdd = new LinkedList<>();
        List<CaseComment> commentsToMerge = new LinkedList<>();
        for (Comment comment : comments) {
            if (comment == null || comment.getDeleted() == Boolean.TRUE)
                continue;

            Date lastCommentChange = Optional.ofNullable(comment.getUpdated()).orElse(comment.getCreated());
            if (DateUtils.beforeNotNull(lastCommentChange, lastSynchronization))
                continue;

            CaseComment caseComment = caseCommentDAO.getByRemoteId(comment.getId());

            boolean isNew = caseComment == null;

            if (isNew) {
                caseComment = new CaseComment();
                caseComment.setAuthorId(findPersonIdByLogin(comment.getAuthor()));
                caseComment.setCreated(comment.getCreated());
                caseComment.setCaseId(caseId);
                caseComment.setRemoteId(comment.getId());
            }
            caseComment.setText(comment.getText());

            if (isNew)
                commentsToAdd.add(caseComment);
            else
                commentsToMerge.add(caseComment);
        }
        caseCommentDAO.persistBatch(commentsToAdd);
        caseCommentDAO.mergeBatch(commentsToMerge);
    }

    private Long findPersonIdByLogin(String login) {
        if (login == null)
            return null;
        UserLogin user = userLoginDAO.findByLogin(login);
        return user == null ? null : user.getPersonId();
    }
}
