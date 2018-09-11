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
import ru.protei.portal.core.model.yt.YtAttachment;
import ru.protei.portal.core.model.yt.fields.change.StringArrayWithIdArrayOldNewChangeField;
import ru.protei.winter.core.utils.services.lock.LockService;

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
    private CaseAttachmentDAO caseAttachmentDAO;

    @Autowired
    private AttachmentDAO attachmentDAO;

    @Autowired
    private LockService lockService;


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
        employeeRegistrations.stream().filter(Objects::nonNull).forEach(this::synchronizeEmployeeRegistration);
    }

    @Transactional
    public void synchronizeEmployeeRegistration(EmployeeRegistration employeeRegistration) {
        log.debug("synchronizeEmployeeRegistration(): start synchronizing employee registration {}", employeeRegistration);
        Date synchronizationStarted = new Date();

        List<CaseLink> issues = caseLinkDAO.getListByQuery(new CaseLinkQuery(employeeRegistration.getId(), En_CaseLink.YT));
        if (CollectionUtils.isEmpty(issues))
            return;

        String[] issueIds = issues.stream().map(CaseLink::getRemoteId).toArray(String[]::new);
        updateIssues(employeeRegistration, issueIds);
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
            parseStateChanges(employeeRegistration, issueId, changes.getChange());
            parseAndUpdateComments(employeeRegistration.getId(), employeeRegistration.getLastYoutrackSynchronization(), changes.getIssue().getComment());
        }
        parseAndUpdateAttachments(employeeRegistration, issueIds);
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

    private void parseStateChanges(EmployeeRegistration employeeRegistration, String issueId, List<Change> changes) {
        List<CaseComment> stateChanges = new LinkedList<>();
        changes.sort(Comparator.comparing(Change::getUpdated, Comparator.nullsFirst(Date::compareTo)));
        for (Change change : changes) {
            if (change == null)
                continue;
            if (DateUtils.beforeNotNull(change.getUpdated(), employeeRegistration.getLastYoutrackSynchronization()))
                continue;

            CaseComment stateChange = parseStateChange(employeeRegistration, issueId, change);
            if (stateChange != null)
                stateChanges.add(stateChange);
        }
        caseCommentDAO.persistBatch(stateChanges);
    }

    private CaseComment parseStateChange(EmployeeRegistration employeeRegistration, String issueId, Change change) {
        StringArrayWithIdArrayOldNewChangeField stateChangeField = change.getStateChangeField();

        if (stateChangeField == null)
            return null;

        En_CaseState newState = toCaseState(stateChangeField.getNewValue());

        CaseComment stateChange = new CaseComment();
        stateChange.setCaseId(employeeRegistration.getId());
        stateChange.setCreated(change.getUpdated());
        stateChange.setAuthorId(findPersonIdByLogin(change.getUpdaterName()));
        stateChange.setCaseStateId((long) newState.getId());
        stateChange.setText(issueId);
        return stateChange;
    }

    private void parseAndUpdateComments(Long caseId, Date lastSynchronization, List<Comment> comments) {
        List<CaseComment> commentsToAdd = new LinkedList<>();
        List<CaseComment> commentsToMerge = new LinkedList<>();
        for (Comment comment : comments) {
            if (comment == null || comment.getDeleted() == Boolean.TRUE)
                continue;

            Date lastCommentChange = DateUtils.max(comment.getUpdated(), comment.getCreated());
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

    private void parseAndUpdateAttachments(EmployeeRegistration employeeRegistration, String... issueIds) {
        List<CaseAttachment> oldCaseAttachments = caseAttachmentDAO.getListByCaseId(employeeRegistration.getId());

        List<YtAttachment> ytAttachments = new LinkedList<>();
        for (String issueId : issueIds) {
            List<YtAttachment> issueAttachments = youtrackService.getIssueAttachments(issueId);
            ytAttachments.addAll(issueAttachments);
        }

        for (YtAttachment ytAttachment : ytAttachments) {
            if (ytAttachment == null || ytAttachment.getId() == null)
                continue;

            Optional<CaseAttachment> caseAttachment = CollectionUtils.find(oldCaseAttachments,
                    ca -> ytAttachment.getId().equals(ca.getRemoteId()));

            if (caseAttachment.isPresent())
                oldCaseAttachments.remove(caseAttachment.get());
            else {
                Attachment attachment = new Attachment();
                attachment.setCreated(ytAttachment.getCreated());
                attachment.setCreatorId(findPersonIdByLogin(ytAttachment.getAuthorLogin()));
                attachment.setFileName(ytAttachment.getName());
                attachment.setExtLink(ytAttachment.getUrl());

                Long attachmentId = attachmentDAO.persist(attachment);
                if (attachmentId == null) {
                    log.warn("parseAndUpdateAttachments(): attachment {} not saved", attachment);
                    continue;
                }

                CaseAttachment ca = new CaseAttachment(employeeRegistration.getId(), attachmentId);
                ca.setRemoteId(ytAttachment.getId());
                caseAttachmentDAO.persist(ca);
            }
        }
        caseAttachmentDAO.removeBatch(oldCaseAttachments);
    }
}
