package ru.protei.portal.core.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.dict.En_PersonRoleType;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.util.DiffCollectionResult;
import ru.protei.portal.core.model.util.DiffResult;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public class AssembledProjectEvent extends ApplicationEvent implements HasCaseComments {
    private Project oldProjectState;
    private Project newProjectState;
    private Long projectId;
    private Person initiator;
    private Long initiatorId;
    private boolean isCreateEvent;
    private DiffCollectionResult<CaseComment> comments = new DiffCollectionResult<>();
    private DiffCollectionResult<CaseLink> links = new DiffCollectionResult<>();

    private Map<Long, DiffCollectionResult<Attachment>> commentToAttachmentDiffs = new HashMap<>();
    private List<Attachment> existingAttachments;

    private long lastUpdated;

    public AssembledProjectEvent(AbstractProjectEvent event) {
        super(event.getSource());
        this.lastUpdated = currentTimeMillis();
        this.initiatorId = event.getPersonId();
        this.projectId = event.getProjectId();
        this.isCreateEvent = event.isCreateEvent();
    }

    public void attachUpdateEvent(ProjectUpdateEvent event) {
        this.lastUpdated = currentTimeMillis();
        this.oldProjectState = event.getOldProjectState();
        this.newProjectState = event.getNewProjectState();
        this.projectId = event.getProjectId();
        this.initiatorId = event.getPersonId();
    }

    public void attachCommentEvent(ProjectCommentEvent event) {
        this.lastUpdated = currentTimeMillis();
        if (event.getOldComment() != null) {
            comments.putChangedEntry(event.getOldComment(), event.getNewComment());
        } else if (event.getNewComment() != null) {
            comments.putAddedEntry(event.getNewComment());
        }
        if (event.getRemovedComment() != null) {
            comments.putRemovedEntry(event.getRemovedComment());
        }
    }

    public void attachAttachmentEvent(ProjectAttachmentEvent event) {
        DiffCollectionResult<Attachment> attachmentDiffCollectionResult = commentToAttachmentDiffs.computeIfAbsent(event.getCommentId(), value -> new DiffCollectionResult<>());

        attachmentDiffCollectionResult.putAddedEntries(event.getAddedAttachments());
        attachmentDiffCollectionResult.putRemovedEntries(event.getRemovedAttachments());
    }

    public void attachLinkEvent(ProjectLinkEvent event) {
        this.lastUpdated = currentTimeMillis();
        if (event.getAddedLink() != null) {
            // check if link is removed and added once more
            if (links.getRemovedEntries() == null || !links.getRemovedEntries().remove(event.getAddedLink())) {
                links.putAddedEntry(event.getAddedLink());
            }
        }
        if (event.getRemovedLink() != null) {
            // check if link is added and removed once more
            if (links.getAddedEntries() == null || !links.getAddedEntries().remove(event.getRemovedLink())) {
                links.putRemovedEntry(event.getRemovedLink());
            }
        }
    }

    public boolean isNameChanged() {
        return isEditEvent() && !Objects.equals(oldProjectState.getName(), newProjectState.getName());
    }

    public boolean isPauseDateChanged() {
        return isEditEvent() && !Objects.equals(oldProjectState.getPauseDate(), newProjectState.getPauseDate());
    }

    public boolean isDescriptionChanged() {
        return isEditEvent() && !Objects.equals(oldProjectState.getDescription(), newProjectState.getDescription());
    }

    public boolean isStateChanged() {
        return isEditEvent() && !Objects.equals(oldProjectState.getStateId(), newProjectState.getStateId());
    }

    public boolean isRegionChanged() {
        return isEditEvent() && !Objects.equals(oldProjectState.getRegion(), newProjectState.getRegion());
    }

    public boolean isCompanyChanged() {
        return isEditEvent() && !Objects.equals(oldProjectState.getCustomer(), newProjectState.getCustomer());
    }

    public boolean isCustomerTypeChanged() {
        return isEditEvent() && !Objects.equals(oldProjectState.getCustomerType(), newProjectState.getCustomerType());
    }

    public boolean isProductDirectionChanged() {
        return isEditEvent() && !Objects.equals(oldProjectState.getProductDirections(), newProjectState.getProductDirections());
    }

    public boolean isProductChanged() {
        return isEditEvent() && !Objects.equals(oldProjectState.getProducts(), newProjectState.getProducts());
    }

    public boolean isSupportValidityChanged() {
        return isEditEvent() && !Objects.equals(oldProjectState.getTechnicalSupportValidity(), newProjectState.getTechnicalSupportValidity());
    }

    public boolean isWorkCompletionDateChanged() {
        return isEditEvent() && !Objects.equals(oldProjectState.getWorkCompletionDate(), newProjectState.getWorkCompletionDate());
    }

    public boolean isPurchaseDateChanged() {
        return isEditEvent() && !Objects.equals(oldProjectState.getPurchaseDate(), newProjectState.getPurchaseDate());
    }

    public boolean isTeamChanged() {
        if (!isEditEvent()) {
            return false;
        }

        DiffCollectionResult<PersonProjectMemberView> diffResult = diffCollection(oldProjectState.getTeam(), newProjectState.getTeam());

        if (isNotEmpty(diffResult.getAddedEntries())) {
            return true;
        }

        if (isNotEmpty(diffResult.getRemovedEntries())) {
            return true;
        }

        return false;
    }

    public boolean isSlaChanged() {
        if (!isEditEvent()) {
            return false;
        }

        if (isSameSlaLists(oldProjectState.getProjectSlas(), newProjectState.getProjectSlas())) {
            return false;
        }

        return true;
    }

    public boolean isCommentsChanged() {
        if (isNotEmpty(comments.getAddedEntries())) {
            return true;
        }

        if (isNotEmpty(comments.getChangedEntries())) {
            return true;
        }

        if (isNotEmpty(comments.getRemovedEntries())) {
            return true;
        }

        return false;
    }

    public boolean isAttachmentChanged() {
        return !commentToAttachmentDiffs.isEmpty();
    }

    public boolean isLinksChanged() {
        if (isNotEmpty(links.getAddedEntries())) {
            return true;
        }

        if (isNotEmpty(links.getRemovedEntries())) {
            return true;
        }

        return false;
    }

    public Map<En_PersonRoleType, DiffCollectionResult<PersonShortView>> getTeamDiffs() {
        Map<En_PersonRoleType, DiffCollectionResult<PersonShortView>> teamDiffs = new TreeMap<>(Comparator.comparingInt(En_PersonRoleType::getId));

        if (!isEditEvent()) {
            DiffCollectionResult<PersonProjectMemberView> diffResult = diffCollection(null, newProjectState.getTeam());

            for (PersonProjectMemberView currentPerson : emptyIfNull(diffResult.getAddedEntries())) {
                DiffCollectionResult<PersonShortView> roleDiffs = teamDiffs.computeIfAbsent(currentPerson.getRole(), k -> new DiffCollectionResult<>());
                roleDiffs.putSameEntry(currentPerson);
            }

            return teamDiffs;
        }

        DiffCollectionResult<PersonProjectMemberView> diffResult = diffCollection(oldProjectState.getTeam(), newProjectState.getTeam());

        for (PersonProjectMemberView currentPerson : emptyIfNull(diffResult.getAddedEntries())) {
            DiffCollectionResult<PersonShortView> roleDiffs = teamDiffs.computeIfAbsent(currentPerson.getRole(), k -> new DiffCollectionResult<>());
            roleDiffs.putAddedEntry(currentPerson);
        }

        for (PersonProjectMemberView currentPerson : emptyIfNull(diffResult.getRemovedEntries())) {
            DiffCollectionResult<PersonShortView> roleDiffs = teamDiffs.computeIfAbsent(currentPerson.getRole(), k -> new DiffCollectionResult<>());
            roleDiffs.putRemovedEntry(currentPerson);
        }

        for (PersonProjectMemberView currentPerson : emptyIfNull(diffResult.getSameEntries())) {
            DiffCollectionResult<PersonShortView> roleDiffs = teamDiffs.computeIfAbsent(currentPerson.getRole(), k -> new DiffCollectionResult<>());
            roleDiffs.putSameEntry(currentPerson);
        }

        return teamDiffs;
    }

    public Map<String, DiffResult<ProjectSla>> getSlaDiffs() {
        Map<String, DiffResult<ProjectSla>> slaDiffs = new LinkedHashMap<>();
        List<ProjectSla> newSlaList = emptyIfNull(newProjectState.getProjectSlas());

        boolean isNewSlaListEmpty = isSlaListEmpty(newSlaList);

        if (!isEditEvent()) {
            newSlaList.forEach(sla -> slaDiffs.put(
                    sla.getImportanceCode(),
                    new DiffResult<>(sla, sla))
            );

            return slaDiffs;
        }

        List<ProjectSla> oldSlaList = emptyIfNull(oldProjectState.getProjectSlas());

        boolean isOldSlaListEmpty = isSlaListEmpty(oldSlaList);

        if (isOldSlaListEmpty && isNewSlaListEmpty) {
            return slaDiffs;
        }

        Set<ProjectSla> allSla = new LinkedHashSet<>();
        allSla.addAll(newSlaList);
        allSla.addAll(oldSlaList);

        for (ProjectSla nextProjectSla : allSla) {
            ProjectSla oldSla = getSlaByImportance(oldSlaList, nextProjectSla.getImportanceLevelId());
            ProjectSla newSla = getSlaByImportance(newSlaList, nextProjectSla.getImportanceLevelId());

            if (oldSla == null && newSla == null) {
                continue;
            }

            slaDiffs.put(nextProjectSla.getImportanceCode(), new DiffResult<>(oldSla == null ? new ProjectSla() : oldSla, newSla == null ? new ProjectSla() : newSla));
        }

        return slaDiffs;
    }

    public DiffCollectionResult<DevUnit> getProductDirectionDiffs() {
        if (!isEditEvent()) {
            DiffCollectionResult<DevUnit> diffCollectionResult = new DiffCollectionResult<>();
            diffCollectionResult.putSameEntries(newProjectState.getProductDirections());
            return diffCollectionResult;
        }

        return diffCollection(oldProjectState.getProductDirections(), newProjectState.getProductDirections());
    }

    public DiffCollectionResult<DevUnit> getProductDiffs() {
        if (!isEditEvent()) {
            DiffCollectionResult<DevUnit> diffCollectionResult = new DiffCollectionResult<>();
            diffCollectionResult.putSameEntries(newProjectState.getProducts());
            return diffCollectionResult;
        }

        return diffCollection(oldProjectState.getProducts(), newProjectState.getProducts());
    }

    // Убрать из существующих элементов добавленные и удаленные
    private <T> DiffCollectionResult<T> synchronizeExists(DiffCollectionResult<T> diff, Function<T, Object> getId) {
        log.info("synchronizeExists(): before +{} -{} {} ", toList(diff.getAddedEntries(), getId), toList(diff.getRemovedEntries(), getId), toList(diff.getSameEntries(), getId));

        if (diff.hasSameEntries()) {
            diff.getSameEntries().removeAll(emptyIfNull(diff.getAddedEntries()));
            diff.getSameEntries().removeAll(emptyIfNull(diff.getRemovedEntries()));
        }

        log.info("synchronizeExists(): after +{} -{} {} ", toList(diff.getAddedEntries(), getId), toList(diff.getRemovedEntries(), getId), toList(diff.getSameEntries(), getId));
        return diff;
    }

    private ProjectSla getSlaByImportance(List<ProjectSla> slaList, int importanceLevelId) {
        return slaList
                .stream()
                .filter(sla -> sla.getImportanceLevelId() == importanceLevelId)
                .findAny()
                .orElse(null);
    }

    private boolean isSameSlaLists(List<ProjectSla> oldSlaList, List<ProjectSla> newSlaList) {
        if (oldSlaList == null || newSlaList == null) {
            return oldSlaList == null && newSlaList == null;
        }

        if (oldSlaList.size() != newSlaList.size()) {
            return false;
        }

        return oldSlaList
                .stream()
                .allMatch(oldSla -> isSameSla(oldSla, getSlaByImportance(newSlaList, oldSla.getImportanceLevelId())));
    }

    private boolean isSameSla(ProjectSla oldSla, ProjectSla newSla) {
        if (oldSla == null || newSla == null) {
            return oldSla == null && newSla == null;
        }

        if (!Objects.equals(oldSla.getImportanceLevelId(), newSla.getImportanceLevelId())) {
            return false;
        }

        if (!Objects.equals(oldSla.getReactionTime(), newSla.getReactionTime())) {
            return false;
        }

        if (!Objects.equals(oldSla.getTemporarySolutionTime(), newSla.getTemporarySolutionTime())) {
            return false;
        }

        if (!Objects.equals(oldSla.getFullSolutionTime(), newSla.getFullSolutionTime())) {
            return false;
        }

        return true;
    }

    private boolean isSlaListEmpty(List<ProjectSla> slaList) {
        return emptyIfNull(slaList).stream().allMatch(ProjectSla::isEmpty);
    }

    public Project getOldProjectState() {
        return oldProjectState;
    }

    public void setOldProjectState(Project oldProjectState) {
        this.oldProjectState = oldProjectState;
    }

    public Project getNewProjectState() {
        return newProjectState;
    }

    public void setNewProjectState(Project newProjectState) {
        this.newProjectState = newProjectState;
    }

    public Person getInitiator() {
        return initiator;
    }

    public void setInitiator(Person initiator) {
        this.initiator = initiator;
    }

    public Long getInitiatorId() {
        return initiator == null ? initiatorId : initiator.getId();
    }

    public Person getCreator() {
        return newProjectState.getCreator();
    }

    public Long getProjectId() {
        return projectId;
    }

    public boolean isCaseCommentsFilled() {
        return comments.getSameEntries() != null;
    }

    public boolean isAttachmentsFilled() {
        return existingAttachments != null;
    }

    public boolean isLinksFilled() {
        return links.hasSameEntries();
    }

    public boolean isProjectFilled() {
        return newProjectState != null;
    }

    public void setExistingLinks(List<CaseLink> sameLinks) {
        links.putSameEntries(sameLinks);
    }

    public List<CaseComment> getAddedCaseComments() {
        return emptyIfNull(comments.getAddedEntries());
    }

    public List<CaseComment> getChangedCaseComments() {
        if (isEmpty(comments.getChangedEntries())) {
            return Collections.emptyList();
        }

        return comments.getChangedEntries().stream().map(DiffResult::getInitialState).collect(Collectors.toList());
    }

    public List<CaseComment> getRemovedCaseComments() {
        return emptyIfNull(comments.getRemovedEntries());
    }

    public List<CaseComment> getAllComments() {
        Set<CaseComment> allComments = new HashSet<>();

        allComments.addAll(emptyIfNull(comments.getSameEntries()));
        allComments.addAll(emptyIfNull(comments.getAddedEntries()));
        allComments.addAll(emptyIfNull(comments.getRemovedEntries()));

        return listOf( allComments ).stream()
                .sorted( Comparator.comparing( CaseComment::getId ) ).collect( Collectors.toList());
    }

    public void setExistingComments(List<CaseComment> existingComments) {
        comments.putSameEntries(existingComments);
    }

    public void setExistingAttachments(List<Attachment> existingAttachments) {
        this.existingAttachments = existingAttachments;
    }

    public List<Attachment> getExistingAttachments() {
        return existingAttachments;
    }

    public Map<Long, DiffCollectionResult<Attachment>> getCommentToAttachmentDiffs() {
        return commentToAttachmentDiffs;
    }

    public DiffCollectionResult<CaseLink> getLinks() {
        synchronizeExists(links, CaseLink::getId);
        return links;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public boolean isEditEvent() {
        return oldProjectState != null;
    }

    public boolean isCreateEvent() {
        return isCreateEvent;
    }

    private static final Logger log = LoggerFactory.getLogger(AssembledProjectEvent.class);
}
