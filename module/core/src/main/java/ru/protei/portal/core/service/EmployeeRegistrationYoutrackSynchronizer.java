package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.client.youtrack.YoutrackConstansMapping;
import ru.protei.portal.core.event.EmployeeRegistrationEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_MigrationEntry;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.DateUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseLinkQuery;
import ru.protei.portal.core.model.query.EmployeeRegistrationQuery;
import ru.protei.portal.core.model.yt.Change;
import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.Comment;
import ru.protei.portal.core.model.yt.api.issue.YtIssueAttachment;
import ru.protei.portal.core.model.yt.fields.change.StringArrayWithIdArrayOldNewChangeField;
import ru.protei.portal.core.service.events.EventPublisherService;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class EmployeeRegistrationYoutrackSynchronizer {
    private final Logger log = LoggerFactory.getLogger(EmployeeRegistrationYoutrackSynchronizer.class);

    private Set<String> YOUTRACK_PROJECTS = new LinkedHashSet<>();
    private Long YOUTRACK_USER_ID;

    @Autowired
    private YoutrackService youtrackService;

    @Autowired
    private CaseCommentDAO caseCommentDAO;

    @Autowired
    private EmployeeRegistrationDAO employeeRegistrationDAO;

    @Autowired
    private CaseObjectDAO caseObjectDAO;

    @Autowired
    private CaseLinkDAO caseLinkDAO;

    @Autowired
    private CaseAttachmentDAO caseAttachmentDAO;

    @Autowired
    private AttachmentDAO attachmentDAO;

    @Autowired
    private MigrationEntryDAO migrationEntryDAO;

    @Autowired
    private EventPublisherService publisherService;

    @Autowired
    private ThreadPoolTaskScheduler scheduler;

    @Autowired
    private PortalConfig config;


    @PostConstruct
    public void startSynchronization() {
        if (!config.data().integrationConfig().isYoutrackEnabled()) {
            log.debug("employee registration youtrack synchronizer is not started because YouTrack integration is disabled in configuration");
            return;
        }

        YOUTRACK_USER_ID = config.data().youtrack().getYoutrackUserId();
        if (YOUTRACK_USER_ID == null) {
            log.warn("bad youtrack configuration: user id for synchronization not set. Employee registration youtrack synchronizer not started");
            return;
        }

        String equipmentProjectName = config.data().youtrack().getEquipmentProject();
        if (StringUtils.isNotBlank(equipmentProjectName)) {
            YOUTRACK_PROJECTS.add(equipmentProjectName);
        }

        String adminProjectName = config.data().youtrack().getAdminProject();
        if (StringUtils.isNotBlank(adminProjectName)) {
            YOUTRACK_PROJECTS.add(adminProjectName);
        }

        String phoneProjectName = config.data().youtrack().getPhoneProject();
        if (StringUtils.isNotBlank(phoneProjectName)) {
            YOUTRACK_PROJECTS.add(phoneProjectName);
        }

        if (YOUTRACK_PROJECTS.isEmpty()) {
            log.warn("bad youtrack configuration: project set is empty. Employee registration youtrack synchronizer not started");
            return;
        }

        String syncCronSchedule = config.data().youtrack().getEmployeeRegistrationSyncSchedule();
        scheduler.schedule(this::synchronizeAll, new CronTrigger(syncCronSchedule));

        log.debug("employee registration youtrack synchronizer was started: " +
                "schedule={}, project for equipment={}, project for admin={}, project for phone={}, youtrack user id={}",
                syncCronSchedule, equipmentProjectName, adminProjectName, phoneProjectName, YOUTRACK_USER_ID);
    }

    @Transactional
    public void synchronizeAll() {
        log.debug("synchronizeAll(): start synchronization");
        Date synchronizationStarted = new Date();

        MigrationEntry migrationEntry = migrationEntryDAO.getOrCreateEntry(En_MigrationEntry.YOUTRACK_EMPLOYEE_REGISTRATION_ISSUE);
        Date lastUpdate = migrationEntry.getLastUpdate();

        Set<String> updatedIssueIds = getUpdatedIssueIds(lastUpdate);

        if (CollectionUtils.isEmpty(updatedIssueIds)) {
            log.debug("synchronizeAll(): nothing to synchronize");
            return;
        }

        log.debug("synchronizeAll(): synchronize updates for {}",  Arrays.toString(updatedIssueIds.toArray()));

        EmployeeRegistrationQuery query = new EmployeeRegistrationQuery();
        query.setLinkedIssueIds(updatedIssueIds);
        List<EmployeeRegistration> employeeRegistrations = employeeRegistrationDAO.getListByQuery(query);

        for (EmployeeRegistration employeeRegistration : employeeRegistrations) {
            synchronizeEmployeeRegistration(employeeRegistration, lastUpdate);
        }

        migrationEntry.setLastUpdate(synchronizationStarted);
        if (!migrationEntryDAO.saveOrUpdate(migrationEntry))
            log.warn("synchronizeAll(): failed to update migration entry");
    }

    private Set<String> getUpdatedIssueIds(Date lastUpdate) {
        Set<String> issueIds = new HashSet<>();
        for (String project : YOUTRACK_PROJECTS) {
            issueIds.addAll(youtrackService.getIssueIdsByProjectAndUpdatedAfter(project, lastUpdate).getData());
        }
        return issueIds;
    }

    private void synchronizeEmployeeRegistration(EmployeeRegistration employeeRegistration, Date lastYtSynchronization) {
        log.debug("synchronizeEmployeeRegistration(): start synchronizing employee registration={}", employeeRegistration);
        En_CaseState oldState = employeeRegistration.getState();

        CaseLinkQuery linkQuery = new CaseLinkQuery();
        linkQuery.setCaseId(employeeRegistration.getId());
        linkQuery.setType(En_CaseLink.YT);
        List<CaseLink> issues = caseLinkDAO.getListByQuery(linkQuery);
        if (CollectionUtils.isEmpty(issues))
            return;

        updateIssues(employeeRegistration, lastYtSynchronization, issues);

        if (!saveEmployeeRegistration(employeeRegistration))
            log.warn("synchronizeEmployeeRegistration(): failed to execute DB merge for employee registration={}", employeeRegistration);


        En_CaseState newState = employeeRegistration.getState();
        if (newState != oldState && newState == En_CaseState.DONE) {
            log.debug("synchronizeEmployeeRegistration(): state was changed to DONE, sending notification");
            fireEmployeeRegistrationEvent(employeeRegistration);
        }
    }

    private void updateIssues(EmployeeRegistration employeeRegistration, Date lastYtSynchronization, Collection<CaseLink> caseLinks) {
        Map<CaseLink, ChangeResponse> issueToChanges = new HashMap<>();

        for (CaseLink caseLink : caseLinks) {
            ChangeResponse issueChanges = youtrackService.getIssueChanges(caseLink.getRemoteId()).getData();
            issueToChanges.put(caseLink, issueChanges);
        }

        En_CaseState state = getGeneralState(issueToChanges.values());
        employeeRegistration.setState(state);

        for (CaseLink caseLink : caseLinks) {
            ChangeResponse changes = issueToChanges.get(caseLink);
            if (changes == null)
                continue;

            log.info("updateIssues(): caseId={}, caseLink={}, changes={}", employeeRegistration.getId(), caseLink, changes);

            parseStateChanges(employeeRegistration, lastYtSynchronization, caseLink.getId(), changes.getChange());
            parseAndUpdateComments(employeeRegistration.getId(), lastYtSynchronization, caseLink.getId(), changes.getIssue().getComment());
        }
        parseAndUpdateAttachments(employeeRegistration, caseLinks);
    }

    private En_CaseState getGeneralState(Collection<ChangeResponse> changes) {
        List<En_CaseState> caseStates = changes.stream()
                .map(change -> YoutrackConstansMapping.toCaseState(change.getIssue().getStateId()))
                .collect(Collectors.toList());

        Predicate<En_CaseState> isDone = cs -> cs == En_CaseState.DONE || cs == En_CaseState.CLOSED;
        Predicate<En_CaseState> isCanceled = cs -> cs == En_CaseState.IGNORED || cs == En_CaseState.CANCELED;
        Predicate<En_CaseState> isCreated = cs -> cs == En_CaseState.CREATED;

        if (caseStates.stream().allMatch(isDone))
            return En_CaseState.DONE;
        if (caseStates.stream().allMatch(isCanceled))
            return En_CaseState.CANCELED;
        if (caseStates.stream().allMatch(isCreated))
            return En_CaseState.CREATED;
        if (caseStates.stream().allMatch(isDone.or(isCanceled)) &&
                caseStates.stream().anyMatch(isDone))
            return En_CaseState.DONE;
        return En_CaseState.ACTIVE;
    }

    private boolean saveEmployeeRegistration(EmployeeRegistration employeeRegistration) {
        CaseObject caseObject = caseObjectDAO.get(employeeRegistration.getId());
        caseObject.setState(employeeRegistration.getState());
        return caseObjectDAO.merge(caseObject) && employeeRegistrationDAO.merge(employeeRegistration);
    }

    private void parseStateChanges(EmployeeRegistration employeeRegistration, Date lastYtSynchronization, Long caseLinkId, List<Change> changes) {
        List<CaseComment> stateChanges = new LinkedList<>();
        changes.sort(Comparator.comparing(Change::getUpdated, Comparator.nullsFirst(Date::compareTo)));
        for (Change change : changes) {
            if (change == null)
                continue;
            if (DateUtils.beforeNotNull(change.getUpdated(), lastYtSynchronization))
                continue;

            CaseComment stateChange = parseStateChange(employeeRegistration, caseLinkId, change);
            if (stateChange == null)
                continue;
            stateChanges.add(stateChange);
            log.debug("parseStateChanges(): create new state change: YT: {}, PORTAL: {}", change, stateChange);
        }
        caseCommentDAO.persistBatch(stateChanges);
    }

    private CaseComment parseStateChange(EmployeeRegistration employeeRegistration, Long caseLinkId, Change change) {
        StringArrayWithIdArrayOldNewChangeField stateChangeField = change.getStateChangeField();
        if (change.getUpdated() == null || stateChangeField == null)
            return null;

        /* Чтобы предотвратить создание комментария об одном и том же изменении дважды,
           в качестве remoteId указываем дату изменения. По связке номера задачи и даты изменения можно отличать изменения */

        String remoteId = String.valueOf(change.getUpdated().getTime());

        if (caseCommentDAO.checkExistsByRemoteIdAndRemoteLinkId(remoteId, caseLinkId))
            return null;

        En_CaseState newState = YoutrackConstansMapping.toCaseState(stateChangeField.getNewValue());

        CaseComment stateChange = new CaseComment();
        stateChange.setRemoteId(remoteId);
        stateChange.setCaseId(employeeRegistration.getId());
        stateChange.setCreated(change.getUpdated());
        stateChange.setRemoteLinkId(caseLinkId);
        stateChange.setAuthorId(YOUTRACK_USER_ID);
        stateChange.setCaseStateId((long) newState.getId());
        stateChange.setOriginalAuthorName(change.getUpdaterName());
        return stateChange;
    }

    private void parseAndUpdateComments(Long caseId, Date lastSynchronization, Long caseLinkId, List<Comment> comments) {

        if (CollectionUtils.isEmpty(comments)) {
            return;
        }

        comments = comments.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Set<CaseComment> commentsToAdd = new LinkedHashSet<>();
        Set<CaseComment> commentsToMerge = new LinkedHashSet<>();
        Set<Long> commentsToDelete = new LinkedHashSet<>();

        List<CaseComment> caseComments = caseCommentDAO.listByRemoteIds(comments.stream()
                .map(Comment::getId)
                .collect(Collectors.toList()));

        List<CaseComment> caseCommentsWithoutDup = makeCaseCommentListWithoutDuplicates(caseComments);

        for (Comment comment : comments) {

            CaseComment caseComment = caseCommentsWithoutDup.stream()
                    .filter(cc -> Objects.equals(cc.getRemoteId(), comment.getId()))
                    .findFirst()
                    .orElse(null);

            Boolean isDeleted = comment.getDeleted();
            boolean existInDb = caseComment != null;

            if (Boolean.TRUE.equals(isDeleted)) {
                if (existInDb) {
                    commentsToDelete.add(caseComment.getId());
                    log.debug("parseAndUpdateComments(): delete comment: YT: {}, PORTAL: {}", comment, caseComment);
                }
                continue;
            }

            if (!existInDb) {
                caseComment = new CaseComment();
                caseComment.setAuthorId(YOUTRACK_USER_ID);
                caseComment.setCreated(comment.getCreated());
                caseComment.setCaseId(caseId);
                caseComment.setRemoteId(comment.getId());
                caseComment.setRemoteLinkId(caseLinkId);
                caseComment.setOriginalAuthorName(comment.getAuthor());
                caseComment.setOriginalAuthorFullName(comment.getAuthorFullName());
                caseComment.setText(comment.getText());
                commentsToAdd.add(caseComment);
                log.debug("parseAndUpdateComments(): create new comment: YT: {}, PORTAL: {}", comment, caseComment);
                continue;
            }

            Date lastCommentChange = DateUtils.max(comment.getUpdated(), comment.getCreated());
            boolean commentContentChanged = DateUtils.beforeNotNull(lastSynchronization, lastCommentChange);

            if (commentContentChanged) {
                caseComment.setText(comment.getText());
                commentsToMerge.add(caseComment);
                log.debug("parseAndUpdateComments(): update comment text: YT: {}, PORTAL: {}", comment, caseComment);
            }
        }

        log.info("parseAndUpdateComments(): caseId={}, commentsToAdd={}, commentsToMerge={}, commentsToDelete={}",
                caseId, commentsToAdd, commentsToMerge, commentsToDelete);

        caseCommentDAO.persistBatch(commentsToAdd);
        caseCommentDAO.mergeBatch(commentsToMerge);
        caseCommentDAO.removeByKeys(commentsToDelete);
    }

    private void parseAndUpdateAttachments(EmployeeRegistration employeeRegistration, Collection<CaseLink> caseLinks) {
        List<CaseAttachment> attachementsToRemove = caseAttachmentDAO.getListByCaseId(employeeRegistration.getId());

        List<YtIssueAttachment> ytAttachments = new LinkedList<>();
        for (CaseLink caseLink : caseLinks) {
            List<YtIssueAttachment> issueAttachments = youtrackService.getIssueAttachments(caseLink.getRemoteId()).getData();
            ytAttachments.addAll(issueAttachments);
        }

        for (YtIssueAttachment ytAttachment : ytAttachments) {
            if (ytAttachment == null || ytAttachment.id == null)
                continue;

            Optional<CaseAttachment> existingAttachment = CollectionUtils.find(
                    attachementsToRemove,
                    ca -> Objects.equals(ytAttachment.id, ca.getRemoteId())
            );

            if (existingAttachment.isPresent())
                attachementsToRemove.remove(existingAttachment.get());
            else {
                Attachment attachment = new Attachment();
                attachment.setCreated(new Date(ytAttachment.created));
                attachment.setCreatorId(YOUTRACK_USER_ID);
                attachment.setFileName(ytAttachment.name);
                attachment.setExtLink(ytAttachment.url);

                Long attachmentId = attachmentDAO.persist(attachment);
                if (attachmentId == null) {
                    log.warn("parseAndUpdateAttachments(): attachment {} not saved", attachment);
                    continue;
                }

                CaseAttachment ca = new CaseAttachment(employeeRegistration.getId(), attachmentId);
                ca.setRemoteId(ytAttachment.id);
                caseAttachmentDAO.persist(ca);
                log.debug("parseAndUpdateAttachments(): create new attachment: YT: {}, PORTAL attachment: {} case attachment: {}", ytAttachment, attachment, ca);
            }
        }
        caseAttachmentDAO.removeBatch(attachementsToRemove);
    }

    private void fireEmployeeRegistrationEvent(EmployeeRegistration employeeRegistration) {
        publisherService.publishEvent(new EmployeeRegistrationEvent(this, employeeRegistration));
    }

    private List<CaseComment> makeCaseCommentListWithoutDuplicates(List<CaseComment> caseComments) {
        List<CaseComment> checkedCaseComments = new ArrayList<>();
        List<String> checkedRemoteIds = new ArrayList<>();
        for (CaseComment caseComment : caseComments) {
            if (checkedRemoteIds.contains(caseComment.getRemoteId())) {
                log.warn("makeCaseCommentListWithoutDuplicates(): comment duplication detected at database: " +
                        "[remoteId={}], [caseComment={}]", caseComment.getRemoteId(), caseComment);
                continue;
            }
            checkedRemoteIds.add(caseComment.getRemoteId());
            checkedCaseComments.add(caseComment);
        }
        return checkedCaseComments;
    }
}
