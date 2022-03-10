package ru.protei.portal.core.service.syncronization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.AssembledEmployeeRegistrationEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseLinkQuery;
import ru.protei.portal.core.model.query.EmployeeRegistrationQuery;
import ru.protei.portal.core.model.struct.Pair;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.YoutrackService;
import ru.protei.portal.core.service.events.EventPublisherService;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;
import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public class EmployeeRegistrationYoutrackSynchronizerImpl implements EmployeeRegistrationYoutrackSynchronizer {
    private final Logger log = LoggerFactory.getLogger( EmployeeRegistrationYoutrackSynchronizerImpl.class);

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
    private CaseStateDAO caseStateDAO;
    @Autowired
    private HistoryDAO historyDAO;
    @Autowired
    private EmployeeRegistrationHistoryDAO employeeRegistrationHistoryDAO;
    @Autowired
    private CaseAttachmentDAO caseAttachmentDAO;
    @Autowired
    private AttachmentDAO attachmentDAO;
    @Autowired
    private MigrationEntryDAO migrationEntryDAO;
    @Autowired
    private EventPublisherService publisherService;

    @Autowired
    private PortalConfig config;
    
    @PostConstruct
    public void init(){
        if (!config.data().integrationConfig().isYoutrackBackchannelEnabled()) {
            log.debug("init(): employee registration youtrack synchronizer is not started because YouTrack integration is disabled in configuration");
            return;
        }

        YOUTRACK_USER_ID = config.data().youtrack().getYoutrackUserId();
        if (YOUTRACK_USER_ID == null) {
            log.warn("init(): bad youtrack configuration: user id for synchronization not set. Employee registration youtrack synchronizer not started");
            return;
        }

        String equipmentProjectName = config.data().youtrack().getEquipmentProject();
        if (StringUtils.isNotBlank(equipmentProjectName)) {
            YOUTRACK_PROJECTS.add(equipmentProjectName);
        }

        String supportProjectName = config.data().youtrack().getSupportProject();
        if (StringUtils.isNotBlank(supportProjectName)) {
            YOUTRACK_PROJECTS.add(supportProjectName);
        }

        String phoneProjectName = config.data().youtrack().getPhoneProject();
        if (StringUtils.isNotBlank(phoneProjectName)) {
            YOUTRACK_PROJECTS.add(phoneProjectName);
        }

        log.debug("init(): employee registration youtrack synchronizer initialized: " +
                        "project for equipment={}, project for admin={}, project for phone={}, youtrack user id={}",
                equipmentProjectName, supportProjectName, phoneProjectName, YOUTRACK_USER_ID);


        if (YOUTRACK_PROJECTS.isEmpty()) {
            log.warn("scheduleSynchronization(): bad youtrack configuration: project set is empty. Employee registration youtrack synchronizer not started");
            return;
        }
    }

    @Override
    public boolean isScheduleSynchronizationNeeded(){
        if (YOUTRACK_USER_ID == null) {
            return false;
        }

        return !CollectionUtils.isEmpty( YOUTRACK_PROJECTS );
    }

    @Async(BACKGROUND_TASKS)
    @Transactional
    @Override
    public void synchronizeAll() {
        log.debug("synchronizeAll(): start synchronization");
        Date synchronizationStarted = new Date();

        MigrationEntry migrationEntry = migrationEntryDAO.getOrCreateEntry(En_MigrationEntry.YOUTRACK_EMPLOYEE_REGISTRATION_ISSUE);
        Date lastUpdate = migrationEntry.getLastUpdate();

        Set<String> updatedIssueIds = getUpdatedIssueIds(lastUpdate);

        if (CollectionUtils.isEmpty(updatedIssueIds)) {
            log.debug("synchronizeAll(): nothing to synchronize. Complete.");
            return;
        }

        Map<Long, String> stateIdToName = caseStateDAO.getAll()
                .stream()
                .collect(Collectors.toMap(CaseState::getId, CaseState::getState));

        log.debug("synchronizeAll(): synchronize updates for {}",  Arrays.toString(updatedIssueIds.toArray()));

        EmployeeRegistrationQuery query = new EmployeeRegistrationQuery();
        query.setLinkedIssueIds(updatedIssueIds);
        List<EmployeeRegistration> employeeRegistrations = employeeRegistrationDAO.getListByQuery(query);

        for (EmployeeRegistration employeeRegistration : employeeRegistrations) {
            synchronizeEmployeeRegistration(employeeRegistration, stateIdToName, lastUpdate.getTime());
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

    private void synchronizeEmployeeRegistration(EmployeeRegistration employeeRegistration, Map<Long, String> stateIdToName, long lastYtSynchronization) {
        log.debug("synchronizeEmployeeRegistration(): start synchronizing employee registration={}", employeeRegistration);
        Long oldState = employeeRegistration.getStateId();

        CaseLinkQuery linkQuery = new CaseLinkQuery();
        linkQuery.setCaseId(employeeRegistration.getId());
        linkQuery.setType(En_CaseLink.YT);
        List<CaseLink> issues = caseLinkDAO.getListByQuery(linkQuery);
        if (CollectionUtils.isEmpty(issues))
            return;

        updateIssues(employeeRegistration, stateIdToName, lastYtSynchronization, issues);

        if (!saveEmployeeRegistration(employeeRegistration))
            log.warn("synchronizeEmployeeRegistration(): failed to execute DB merge for employee registration={}", employeeRegistration);


        Long newState = employeeRegistration.getStateId();
        if (!Objects.equals(newState, oldState) && newState == CrmConstants.State.DONE) {
            log.debug("synchronizeEmployeeRegistration(): state was changed to DONE, sending notification");
            fireEmployeeRegistrationEvent(employeeRegistration);
        }
    }

    private void updateIssues(EmployeeRegistration employeeRegistration, Map<Long, String> stateIdToName, long lastYtSynchronization, Collection<CaseLink> caseLinks) {

        List<Long> caseStates = new ArrayList<>();

        for (CaseLink caseLink : caseLinks) {
            String ytIssueId = caseLink.getRemoteId();
            YouTrackIssueInfo ytIssue = youtrackService.getIssueInfo(ytIssueId).getData();
            if (ytIssue == null) continue;
            List<YouTrackIssueStateChange> ytIssueStateChanges = youtrackService.getIssueStateChanges(ytIssueId).getData();

            log.info("updateIssues(): caseId={}, caseLink={}", employeeRegistration.getId(), caseLink);

            caseStates.add(ytIssue.getState().getId());

            parseStateChanges(employeeRegistration.getId(), stateIdToName, lastYtSynchronization, caseLink.getId(), ytIssueStateChanges);
            parseAndUpdateComments(employeeRegistration.getId(), lastYtSynchronization, caseLink.getId(), ytIssue.getComments());
            parseAndUpdateAttachments(employeeRegistration.getId(), lastYtSynchronization, caseLink.getId(), ytIssue.getAttachments());
        }

        Long stateId = getGeneralState(caseStates);
        employeeRegistration.setStateId(stateId);
    }

    private Long getGeneralState(Collection<Long> caseStates) {

        Predicate<Long> isDone = cs -> cs == CrmConstants.State.DONE || cs == CrmConstants.State.CLOSED;
        Predicate<Long> isCanceled = cs -> cs == CrmConstants.State.IGNORED || cs == CrmConstants.State.CANCELED;
        Predicate<Long> isCreated = cs -> cs == CrmConstants.State.CREATED;

        if (caseStates.stream().allMatch(isDone))
            return CrmConstants.State.DONE;
        if (caseStates.stream().allMatch(isCanceled))
            return CrmConstants.State.CANCELED;
        if (caseStates.stream().allMatch(isCreated))
            return CrmConstants.State.CREATED;
        if (caseStates.stream().allMatch(isDone.or(isCanceled)) &&
                caseStates.stream().anyMatch(isDone))
            return CrmConstants.State.DONE;
        return CrmConstants.State.ACTIVE;
    }

    private boolean saveEmployeeRegistration(EmployeeRegistration employeeRegistration) {
        CaseObject caseObject = caseObjectDAO.get(employeeRegistration.getId());
        caseObject.setStateId(employeeRegistration.getStateId());
        return caseObjectDAO.merge(caseObject) && employeeRegistrationDAO.merge(employeeRegistration);
    }

    private void parseStateChanges(Long caseId, Map<Long, String> stateIdToName, long lastYtSynchronization, Long caseLinkId, List<YouTrackIssueStateChange> issueStateChanges) {
        log.info("parseStateChanges(): caseId={}, lastYtSynchronization={}, caseLinkId={}, issueStateChangesSize={}", caseId, lastYtSynchronization, caseLinkId, size(issueStateChanges));

        if (CollectionUtils.isEmpty(issueStateChanges)) {
            return;
        }

        List<EmployeeRegistrationHistory> employeeRegistrationHistories = new ArrayList<>(issueStateChanges.size());

        for (YouTrackIssueStateChange issueStateChange : issueStateChanges) {
            if (issueStateChange == null) {
                log.info("parseStateChanges(): issueStateChange is null");
                continue;
            }

            if (issueStateChange.getTimestamp() == null) {
                log.info("parseStateChanges(): timestamp is null");
                continue;
            }

            if (issueStateChange.getTimestamp() < lastYtSynchronization) {
                log.info("parseStateChanges(): timestamp is less than lastYtSynchronization");
                continue;
            }

            if (issueStateChange.getAddedCaseStateId() == null) {
                log.info("parseStateChanges(): newState is null");
                continue;
            }

            History historyStateChange = parseStateChange(caseId, caseLinkId, stateIdToName, issueStateChange);

            Long historyId = historyDAO.persist(historyStateChange);

            employeeRegistrationHistories.add(createEmployeeRegistrationHistory(historyId, caseLinkId, issueStateChange.getAuthorFullName()));
            log.debug("parseStateChanges(): create new state change: YT: {}, PORTAL: {}", issueStateChanges, historyStateChange);
        }

        employeeRegistrationHistoryDAO.persistBatch(employeeRegistrationHistories);
    }

    private EmployeeRegistrationHistory createEmployeeRegistrationHistory(Long historyId, Long caseLinkId, String originalAuthorName) {
        EmployeeRegistrationHistory employeeRegistrationHistory = new EmployeeRegistrationHistory();

        employeeRegistrationHistory.setHistoryId(historyId);
        employeeRegistrationHistory.setRemoteLinkId(caseLinkId);
        employeeRegistrationHistory.setOriginalAuthorName(originalAuthorName);

        return employeeRegistrationHistory;
    }

    private History parseStateChange(Long caseId, Long caseLinkId, Map<Long, String> stateIdToName, YouTrackIssueStateChange issueStateChange) {
        log.info("parseStateChange(): issueStateChange={}", issueStateChange);

        Long newState = issueStateChange.getAddedCaseStateId();
        Long timestamp = issueStateChange.getTimestamp();

        History lastHistory = historyDAO.getLastHistoryForEmployeeRegistration(caseId, caseLinkId, En_HistoryType.CASE_STATE);

        Long oldState = lastHistory == null ? null : lastHistory.getNewId();

        String oldStateName = oldState == null ? null : stateIdToName.get(oldState);
        En_HistoryAction historyAction = oldState == null ? En_HistoryAction.ADD : En_HistoryAction.CHANGE;

        return new History(
                YOUTRACK_USER_ID, new Date(timestamp), caseId, historyAction,
                En_HistoryType.CASE_STATE, oldState, oldStateName, newState, stateIdToName.get(newState)
        );
    }

    private void parseAndUpdateComments(Long caseId, long lastYtSynchronization, Long caseLinkId, List<CaseComment> comments) {

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
                .map(CaseComment::getRemoteId)
                .collect(Collectors.toList()));

        List<CaseComment> caseCommentsWithoutDup = makeCaseCommentListWithoutDuplicates(caseComments);

        for (CaseComment comment : comments) {

            CaseComment caseComment = caseCommentsWithoutDup.stream()
                    .filter(cc -> Objects.equals(cc.getRemoteId(), comment.getRemoteId()))
                    .findFirst()
                    .orElse(null);

            boolean isDeleted = comment.isDeleted();
            boolean existInDb = caseComment != null;

            if (isDeleted) {
                if (existInDb) {
                    commentsToDelete.add(caseComment.getId());
                    log.debug("parseAndUpdateComments(): delete comment: YT: {}, PORTAL: {}", comment, caseComment);
                }
                continue;
            }

            if (!existInDb) {
                caseComment = new CaseComment();
                caseComment.setCaseId(caseId);
                caseComment.setRemoteLinkId(caseLinkId);
                caseComment.setAuthorId(comment.getAuthorId());
                caseComment.setCreated(comment.getCreated());
                caseComment.setRemoteId(comment.getRemoteId());
                caseComment.setOriginalAuthorName(comment.getOriginalAuthorName());
                caseComment.setOriginalAuthorFullName(comment.getOriginalAuthorFullName());
                caseComment.setText(comment.getText());
                caseComment.setPrivacyType(En_CaseCommentPrivacyType.PUBLIC);
                commentsToAdd.add(caseComment);
                log.debug("parseAndUpdateComments(): create new comment: YT: {}, PORTAL: {}", comment, caseComment);
                continue;
            }

            long lastCommentChange = Math.max(
                    comment.getUpdated() == null ? 0 : comment.getUpdated().getTime(),
                    comment.getCreated() == null ? 0 : comment.getCreated().getTime()
            );
            boolean commentContentChanged = lastCommentChange > lastYtSynchronization;

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

    private void parseAndUpdateAttachments(Long caseId, long lastYtSynchronization, Long caseLinkId, List<Pair<Attachment, CaseAttachment>> attachments) {
        List<CaseAttachment> existingAttachments = caseAttachmentDAO.getListByCaseId(caseId);

        for (Pair<Attachment, CaseAttachment> ytAttachment : attachments) {
            if (ytAttachment == null) {
                continue;
            }
            Attachment attachment = ytAttachment.getA();
            CaseAttachment caseAttachment = ytAttachment.getB();

            Optional<CaseAttachment> existingAttachment = CollectionUtils.find(
                existingAttachments,
                ca -> Objects.equals(caseAttachment.getRemoteId(), ca.getRemoteId())
            );

            if (existingAttachment.isPresent()) {
                existingAttachments.remove(existingAttachment.get());
            } else {
                Long attachmentId = attachmentDAO.persist(attachment);
                if (attachmentId == null) {
                    log.warn("parseAndUpdateAttachments(): attachment not saved {}", attachment);
                    continue;
                }
                attachment.setId(attachmentId);
                caseAttachment.setCaseId(caseId);
                caseAttachment.setAttachmentId(attachmentId);
                Long caseAttachmentId = caseAttachmentDAO.persist(caseAttachment);
                if (caseAttachmentId == null) {
                    log.warn("parseAndUpdateAttachments(): caseAttachment not saved {}", caseAttachment);
                    continue;
                }
                caseAttachment.setId(caseAttachmentId);
                log.debug("parseAndUpdateAttachments(): create new attachment: YT: {}, PORTAL attachment: {} case attachment: {}", ytAttachment, attachment, caseAttachment);
            }
        }
        caseAttachmentDAO.removeBatch(existingAttachments);
    }

    private void fireEmployeeRegistrationEvent(EmployeeRegistration employeeRegistration) {
        publisherService.publishEvent(new AssembledEmployeeRegistrationEvent(this, null, employeeRegistration));
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
