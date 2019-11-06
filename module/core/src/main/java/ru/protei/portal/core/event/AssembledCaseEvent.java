package ru.protei.portal.core.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.util.DiffCollectionResult;

import java.util.*;

import static java.lang.System.currentTimeMillis;
import static ru.protei.portal.core.model.helper.CollectionUtils.toList;

public class AssembledCaseEvent extends ApplicationEvent {

    private Long caseObjectId;
    private CaseObject lastState;
    private CaseObject initState;
    protected CaseComment newComment;
    private CaseComment oldComment;
    private CaseComment removedComment;
    protected Collection<Attachment> addedAttachments;
    private Collection<Attachment> removedAttachments;
    private Collection<Attachment> existingAttachments;
    private DiffCollectionResult <CaseLink> mergeLinks;

    private Person initiator;
    private ServiceModule serviceModule;
    private List<CaseComment> caseComments;
    private boolean isEagerEvent;
    // Measured in ms
    private final long timeCreated;
    protected long lastUpdated;

    public AssembledCaseEvent(AbstractCaseEvent ace) {
        this(ace.getSource(), ace.getServiceModule(), ace.getCaseObjectId(), ace.getPerson(), ace.isEagerEvent());
    }

    private AssembledCaseEvent( Object source, ServiceModule serviceModule, Long caseObjectId, Person initiator, boolean isEagerEvent ) {
        super( source );
        this.caseObjectId = caseObjectId;
        this.initiator = initiator;
        this.serviceModule = serviceModule;
        this.timeCreated = currentTimeMillis();
        lastUpdated = timeCreated;
        this.isEagerEvent = isEagerEvent;
    }

    public void attachCaseObjectEvent( CaseObjectEvent objectEvent ) {
        this.lastUpdated = currentTimeMillis();
        this.initState = objectEvent.getOldState();
        this.lastState = objectEvent.getNewState();
        this.initiator = objectEvent.getPerson();
        this.serviceModule = objectEvent.getServiceModule();
        mergeLinks = objectEvent.getMergeLinks();
        isEagerEvent = isEagerEvent||objectEvent.isEagerEvent();
    }

    public void attachCommentEvent( CaseCommentEvent commentEvent ) {
        this.lastUpdated = currentTimeMillis();
        oldComment = commentEvent.getOldCaseComment();
        newComment = commentEvent.getNewCaseComment();
        removedComment = commentEvent.getRemovedCaseComment();
        isEagerEvent = isEagerEvent||commentEvent.isEagerEvent();
    }

    public void attachAttachmentEvent( CaseAttachmentEvent event ) {
        this.lastUpdated = currentTimeMillis();
        synchronizeAttachments( event.getExistingAttachments(), event.getAddedAttachments(), event.getRemovedAttachments() );
    }

    public CaseComment getCaseComment() {
        return newComment;
    }

    public boolean isCreateEvent() {
        return this.initState == null;
    }

    private boolean isUpdateEvent() {
        return this.initState != null ;//&& lastState!=null;
    }

    public boolean isCommentAttached() {
        return this.newComment != null;
    }

    public boolean isCommentRemoved() {
        return removedComment != null;
    }

    public boolean isCaseStateChanged() {
        return isUpdateEvent() && lastState.getState() != initState.getState();
    }

    public boolean isTimeElapsedChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getTimeElapsed(), initState.getTimeElapsed());
    }

    public boolean isCaseImportanceChanged() {
        return isUpdateEvent() && !lastState.getImpLevel().equals(initState.getImpLevel());
    }

    public boolean isManagerChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getManagerId(), initState.getManagerId());
    }

    public boolean isProductChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getProductId(), initState.getProductId());
    }

    public boolean isInitiatorChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getInitiatorId(), initState.getInitiatorId());
    }

    public boolean isInitiatorCompanyChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getInitiatorCompanyId(), initState.getInitiatorCompanyId());
    }

    public boolean isInfoChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getInfo(), initState.getInfo());
    }

    public boolean isNameChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getName(), initState.getName());
    }

    public boolean isPrivacyChanged() {
        return isUpdateEvent() && lastState.isPrivateCase() != initState.isPrivateCase();
    }

    public boolean isPlatformChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getPlatformId(), initState.getPlatformId());
    }

    public boolean isEagerEvent() {
        return isEagerEvent;
    }

    @Deprecated
    public void attachCaseComment(CaseComment caseComment) {
        newComment = caseComment;
        lastUpdated = currentTimeMillis();
    }

    @Deprecated
    public void includeCaseComments (List<CaseComment> commentList) {
        newComment = CollectionUtils.lastOrDefault(commentList, newComment );
        lastUpdated = currentTimeMillis();
    }

    @Deprecated
    public void includeCaseAttachments (List<Attachment> attachments) {
        this.addedAttachments.addAll(attachments);
        this.lastUpdated = currentTimeMillis();
    }

    public CaseComment getOldComment() {
        return oldComment;
    }

    public CaseComment getRemovedComment() {
        return removedComment;
    }

    public Collection<Attachment> getAddedAttachments() {
        return addedAttachments;
    }

    public Collection<Attachment> getExistingAttachments() {
        return existingAttachments;
    }

    public Collection<Attachment> getRemovedAttachments() {
        return removedAttachments;
    }

    private void synchronizeAttachments( Collection<Attachment> existing, Collection<Attachment> added, Collection<Attachment> removed ){
        if(added == null)
            added = Collections.emptyList();
        if(removed == null)
            removed = Collections.emptyList();
//        if(existing == null)
//            existing = Collections.emptyList();

        log.info( "synchronizeAttachments(): before +{} -{} {} ", toList( addedAttachments, Attachment::getId ), toList( removedAttachments, Attachment::getId ), toList( existingAttachments, Attachment::getId ) );

        if (addedAttachments == null) addedAttachments = new ArrayList<>();
        if (removedAttachments == null) removedAttachments = new ArrayList<>();
        if (existingAttachments == null && existing!=null) existingAttachments = new ArrayList<>(existing);

        log.info( "synchronizeAttachments(): -> +{} -{} {} ", toList( added, Attachment::getId ), toList( removed, Attachment::getId ), toList( existing, Attachment::getId ) );
        addedAttachments.addAll(added);
        removedAttachments.addAll(removed);

        Iterator<Attachment> it = removedAttachments.iterator();
        while (it.hasNext()){
            Attachment removedAttachment = it.next();
            if(addedAttachments.contains(removedAttachment)){ //if you add and remove an attachment in a row
                addedAttachments.remove(removedAttachment);
                it.remove();
                continue;
            }
        }

        if(existingAttachments!=null){
            if (existing != null) existingAttachments.retainAll( existing );
            existingAttachments.removeAll( addedAttachments );
            existingAttachments.removeAll( removedAttachments );
        }

        log.info( "synchronizeAttachments(): after +{} -{} {} ", toList( addedAttachments, Attachment::getId ), toList( removedAttachments, Attachment::getId ), toList( existingAttachments, Attachment::getId ) );
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public Date getEventDate() {
        return new Date(getTimestamp());
    }

    public CaseObject getCaseObject() {
        return lastState != null ? lastState : initState;
    }

    public CaseObject getLastState() {
        return lastState;
    }

    public CaseObject getInitState() {
        return initState;
    }

    public Person getInitiator() {
        return initiator;
    }

    public DiffCollectionResult<CaseLink> getMergeLinks() {
        return mergeLinks;
    }

    public boolean isCoreModuleEvent () {
        return serviceModule == null || serviceModule == ServiceModule.GENERAL;
    }

    public boolean isSendToCustomers() {
        return isCreateEvent()
                || (
                        (!isCommentAttached() || isAttachedCommentNotPrivate())
                                && (!isCommentRemoved() || isRemovedCommentNotPrivate()))
                || isPublicChangedWithOutComments();
    }

    private boolean isAttachedCommentNotPrivate() {
        return newComment != null && !newComment.isPrivateComment();
    }

    private boolean isRemovedCommentNotPrivate() {
        return removedComment != null && !removedComment.isPrivateComment();
    }

    private boolean isPublicChangedWithOutComments() {
        return  isCaseImportanceChanged()
                || isCaseStateChanged()
                || isInfoChanged()
                || isInitiatorChanged()
                || isInitiatorCompanyChanged()
                || isManagerChanged()
                || isNameChanged()
                || isPrivacyChanged()
                || isProductChanged();
    }


    public boolean isCaseObjectFilled() {
        return initState!=null;
    }

    public boolean isCaseCommentsFilled() {
        return caseComments!=null;
    }

    public Long getCaseNumber() {
        CaseObject caseObject = getCaseObject();
        if(caseObject==null) return null;
        return caseObject.getCaseNumber();
    }

    public void setLastCaseObject( CaseObject caseObject ) {
        lastState = caseObject;
    }

    public void setInitialCaseComments( List<CaseComment> caseComments ) {
        this.caseComments = caseComments;
    }

    public List<CaseComment> getAllComments() {
        List<CaseComment> comments = new ArrayList<>(  caseComments );
        if (getRemovedComment() != null) {
            comments.add(getRemovedComment());
        }

        if (getCaseComment() != null) {
            boolean isNewCommentPresents = comments.stream()
                    .anyMatch(comment ->
                            Objects.equals(comment.getId(), getCaseComment().getId())
                    );

            if (!isNewCommentPresents) {
                comments.add(getCaseComment());
            }
        }
        return comments;
    }

    public Person getCreator() {
        return getCaseObject().getCreator();
    }

    public Person getManager() {
        return getCaseObject().getManager();
    }

    public Long getCaseObjectId() {
        CaseObject caseObject = getCaseObject();
        if(caseObject==null) return caseObjectId;
        return caseObject.getId();
    }

    private static final Logger log = LoggerFactory.getLogger( AssembledCaseEvent.class );

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
    }
}
