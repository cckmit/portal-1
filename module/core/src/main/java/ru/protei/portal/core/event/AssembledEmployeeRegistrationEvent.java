package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.model.util.DiffCollectionResult;
import ru.protei.portal.core.model.util.DiffResult;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public class AssembledEmployeeRegistrationEvent extends ApplicationEvent implements HasCaseComments {
    private EmployeeRegistration oldState;
    private EmployeeRegistration newState;

    private Long employeeRegistrationId;
    private Long initiatorId;
    private DiffCollectionResult<CaseComment> comments = new DiffCollectionResult<>();
    private long lastUpdated;
    private boolean hasComments;
    private Map<Long, DiffCollectionResult<Attachment>> commentToAttachmentDiffs = new HashMap<>();
    private List<Attachment> existingAttachments;
    private List<NotificationEntry> registrationSubscribers;

    public AssembledEmployeeRegistrationEvent(Object source, EmployeeRegistration oldState, EmployeeRegistration newState) {
        super(source);
        this.oldState = oldState;
        this.newState = newState;
    }

    public AssembledEmployeeRegistrationEvent(Object source, EmployeeRegistration oldState, EmployeeRegistration newState, List<NotificationEntry> registrationSubscribers) {
        super(source);
        this.oldState = oldState;
        this.newState = newState;
        this.registrationSubscribers = registrationSubscribers;
    }

    public AssembledEmployeeRegistrationEvent(AbstractEmployeeRegistrationEvent event) {
        super(event.getSource());
        this.lastUpdated = currentTimeMillis();
        this.initiatorId = event.getPersonId();
        this.employeeRegistrationId = event.getEmployeeRegistrationId();
    }

    public boolean isEditEvent() {
        return oldState != null;
    }

    public boolean isEmploymentDateChanged() {
        return isEditEvent() && !Objects.equals(oldState.getEmploymentDate().getTime(), newState.getEmploymentDate().getTime());
    }

    public boolean isCuratorsChanged() {
        return isEditEvent() && !CollectionUtils.equals(oldState.getCuratorsIds(), newState.getCuratorsIds());
    }

    public DiffCollectionResult<PersonShortView> getCuratorsDiff() {
        DiffCollectionResult<PersonShortView> curatorsDiffs = new DiffCollectionResult<>();

        if (!isEditEvent()) {
            curatorsDiffs.putSameEntries(newState.getCurators());
            return curatorsDiffs;
        }

        return CollectionUtils.diffCollection(oldState.getCurators(), newState.getCurators());
    }

    public EmployeeRegistration getOldState() {
        return oldState;
    }

    public EmployeeRegistration getNewState() {
        return newState;
    }

    public void attachCommentEvent(EmployeeRegistrationCommentEvent event) {
        this.hasComments = true;
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

    @Override
    public List<CaseComment> getAddedCaseComments() {
        return emptyIfNull(comments.getAddedEntries());
    }

    @Override
    public List<CaseComment> getChangedCaseComments() {
        if (isEmpty(comments.getChangedEntries())) {
            return Collections.emptyList();
        }

        return comments.getChangedEntries().stream().map(DiffResult::getInitialState).collect(Collectors.toList());
    }

    @Override
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

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public Long getEmployeeRegistrationId() {
        return employeeRegistrationId;
    }

    public Long getInitiatorId() {
        return initiatorId;
    }

    public boolean isCaseCommentsFilled() {
        return comments.getSameEntries() != null;
    }

    public boolean isEmployeeRegistrationFilled() {
        return newState != null;
    }

    public void setNewEmployeeRegistrationState(EmployeeRegistration newState) {
        this.newState = newState;
    }

    public boolean hasComments() {
        return hasComments;
    }

    public void attachAttachmentEvent(EmployeeRegistrationAttachmentEvent event) {
        DiffCollectionResult<Attachment> attachmentDiffCollectionResult = commentToAttachmentDiffs.computeIfAbsent(event.getCommentId(), value -> new DiffCollectionResult<>());

        attachmentDiffCollectionResult.putAddedEntries(event.getAddedAttachments());
        attachmentDiffCollectionResult.putRemovedEntries(event.getRemovedAttachments());
    }

    public boolean isAttachmentChanged() {
        return !commentToAttachmentDiffs.isEmpty();
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

    public boolean isAttachmentsFilled() {
        return existingAttachments != null;
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

    public List<NotificationEntry> getRegistrationSubscribers() {
        return registrationSubscribers;
    }
}
