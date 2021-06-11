package ru.protei.portal.core.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.util.DiffCollectionResult;
import ru.protei.portal.core.model.util.DiffResult;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.System.currentTimeMillis;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public class AssembledDeliveryEvent extends ApplicationEvent implements HasCaseComments {
    private Delivery oldDeliveryState;
    private Delivery newDeliveryState;
    private DiffResult<String> name = new DiffResult<>();
    private DiffResult<String> info = new DiffResult<>();
    private Long deliveryId;
    private Long initiatorId;//кто инициировал рассылку
    private Person initiator;
    private boolean isCreateEvent;
    private DiffCollectionResult<CaseComment> comments = new DiffCollectionResult<>();
    private DiffCollectionResult <Attachment> attachments = new DiffCollectionResult<>();

    private Map<Long, DiffCollectionResult<Attachment>> commentToAttachmentDiffs = new HashMap<>();

    private long lastUpdated;

    public AssembledDeliveryEvent(AbstractDeliveryEvent event) {
        super(event.getSource());
        this.lastUpdated = currentTimeMillis();
        this.initiatorId = event.getPersonId();
        this.deliveryId = event.getDeliveryId();
        this.isCreateEvent = event.isCreateEvent();
    }

    public void attachUpdateEvent(DeliveryUpdateEvent event) {
        this.lastUpdated = currentTimeMillis();
        this.oldDeliveryState = event.getOldDeliveryState();
        this.newDeliveryState = event.getNewDeliveryState();
        this.deliveryId = event.getDeliveryId();
        this.initiatorId = event.getPersonId();
    }

    public void attachCaseNameAndDescriptionEvent(DeliveryNameAndDescriptionEvent event) {
        this.lastUpdated = currentTimeMillis();
        this.name = synchronizeDiffs(this.name, event.getName());
        this.info = synchronizeDiffs(this.info, event.getInfo());
    }

    private <T> DiffResult<T> synchronizeDiffs(DiffResult<T> source, DiffResult<T> other) {
        if(!source.isEmpty() && !other.hasDifferences()) return source;
        synchronized (source) {
            DiffResult<T> result = new DiffResult<>();
            result.setInitialState(source.hasInitialState() ? source.getInitialState() : other.getInitialState());
            result.setNewState(other.getNewState());
            return result;
        }
    }

    public void attachCommentEvent(DeliveryCommentEvent event) {
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

    public void attachAttachmentEvent(DeliveryAttachmentEvent event) {
        DiffCollectionResult<Attachment> attachmentDiffCollectionResult = commentToAttachmentDiffs.computeIfAbsent(event.getCommentId(), value -> new DiffCollectionResult<>());

        attachmentDiffCollectionResult.putAddedEntries(event.getAddedAttachments());
        attachmentDiffCollectionResult.putRemovedEntries(event.getRemovedAttachments());
    }

    public boolean isNameChanged() {
        return isEditEvent() && !Objects.equals(oldDeliveryState.getName(), newDeliveryState.getName());
    }

    public boolean isDepartureDateChanged() {
        return isEditEvent() && !Objects.equals(oldDeliveryState.getDepartureDate().getTime(), newDeliveryState.getDepartureDate().getTime());
    }

    public boolean isDescriptionChanged() {
        return isEditEvent() && !Objects.equals(oldDeliveryState.getDescription(), newDeliveryState.getDescription());
    }

    public boolean isStateChanged() {
        return isEditEvent() && !Objects.equals(oldDeliveryState.getStateId(), newDeliveryState.getStateId());
    }

    public boolean isTypeChanged() {
        return isEditEvent() && !Objects.equals(oldDeliveryState.getType(), newDeliveryState.getType());
    }

    public boolean isAttributeChanged() {
        return isEditEvent() && !Objects.equals(oldDeliveryState.getAttribute(), newDeliveryState.getAttribute());
    }

    public boolean isContractChanged() {
        return isEditEvent() && !Objects.equals(oldDeliveryState.getContractId(), newDeliveryState.getContractId());
    }

    public boolean isInitiatorChanged() {
        return isEditEvent() && !Objects.equals(oldDeliveryState.getInitiatorId(), newDeliveryState.getInitiatorId());
    }

    public boolean isProjectChanged() {
        return isEditEvent() && !Objects.equals(oldDeliveryState.getProjectId(), newDeliveryState.getProjectId());
    }

    public DiffCollectionResult<DevUnit> getProductDiffs() {
        if (isEditEvent()) {
            return diffCollection(
                    oldDeliveryState.getProject()==null ? null : oldDeliveryState.getProject().getProducts(),
                    newDeliveryState.getProject()==null ? null : newDeliveryState.getProject().getProducts());
        }

        DiffCollectionResult<DevUnit> diffCollectionResult = new DiffCollectionResult<>();
        if (newDeliveryState.getProject() == null){
            return diffCollectionResult;
        }
        diffCollectionResult.putSameEntries(newDeliveryState.getProject().getProducts());
        return diffCollectionResult;
    }

    public Delivery getOldDeliveryState() {
        return oldDeliveryState;
    }

    public Delivery getNewDeliveryState() {
        return newDeliveryState;
    }

    public void setNewDeliveryState(Delivery newDeliveryState) {
        this.newDeliveryState = newDeliveryState;
    }

    public DiffResult<String> getName() {
        return name;
    }

    public DiffResult<String> getInfo() {
        return info;
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

    public boolean isDeliveryNameFilled() {
        synchronized (name) {
            return name.hasNewState();
        }
    }

    public boolean isDeliveryInfoFilled() {
        synchronized (info) {
            return info.hasNewState();
        }
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public boolean isCaseCommentsFilled() {
        return comments.getSameEntries() != null;
    }

    public boolean isAttachmentsFilled() {
        synchronized (attachments){
            return attachments.hasSameEntries();
        }
    }

    public boolean isDeliveryFilled() {
        return newDeliveryState != null;
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

    public Collection<Attachment> getAddedAttachments() {
        return attachments.getAddedEntries();
    }

    public List<Attachment> getExistingAttachments() {
        attachments = synchronizeExists( attachments, Attachment::getId );
        return attachments.getSameEntries();
    }

    public void setExistingAttachments( List<Attachment> existingAttachments ) {
        attachments.putSameEntries( existingAttachments );
    }

    public Collection<Attachment> getRemovedAttachments() {
        return attachments.getRemovedEntries();
    }

    private <T> DiffCollectionResult<T> synchronizeExists(DiffCollectionResult<T> diff, Function<T, Object> getId) {
        log.info( "synchronizeExists(): before +{} -{} {} ", toList( diff.getAddedEntries(), getId ), toList( diff.getRemovedEntries(), getId ), toList( diff.getSameEntries(), getId ) );
        if (diff.hasSameEntries()){
            synchronized (diff) {
                diff.getSameEntries().removeAll( emptyIfNull( diff.getAddedEntries() ) );
                diff.getSameEntries().removeAll( emptyIfNull( diff.getRemovedEntries() ) );
            }
        }
        log.info( "synchronizeExists(): after +{} -{} {} ", toList( diff.getAddedEntries(), getId ), toList( diff.getRemovedEntries(), getId ), toList( diff.getSameEntries(), getId ) );
        return diff;
    }

    public Map<Long, DiffCollectionResult<Attachment>> getCommentToAttachmentDiffs() {
        return commentToAttachmentDiffs;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public boolean isEditEvent() {
        return oldDeliveryState != null;
    }

    public boolean isCreateEvent() {
        return isCreateEvent;
    }

    public boolean isPublicCommentsChanged() {
        List<CaseComment> allEntries = new ArrayList<>();
        allEntries.addAll(emptyIfNull(comments.getAddedEntries()));
        allEntries.addAll(emptyIfNull(comments.getRemovedEntries()));
        allEntries.addAll(emptyIfNull(comments.getChangedEntries()).stream().map(DiffResult::getNewState).collect(Collectors.toList()));

        return allEntries.stream().anyMatch(comment -> !comment.isPrivateComment());
    }

    public boolean isPublicAttachmentsChanged() {
        List<Attachment> allEntries = new ArrayList<>();
        allEntries.addAll(emptyIfNull(attachments.getAddedEntries()));
        allEntries.addAll(emptyIfNull(attachments.getRemovedEntries()));
        allEntries.addAll(emptyIfNull(attachments.getChangedEntries()).stream().map(DiffResult::getNewState).collect(Collectors.toList()));

        return allEntries.stream().anyMatch(attachment -> !attachment.isPrivate());
    }

    private static final Logger log = LoggerFactory.getLogger(AssembledDeliveryEvent.class);
}
