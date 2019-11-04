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

public class AssembledCaseEvent extends ApplicationEvent {

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

    public String getExtAppType() {
        CaseObject caseObject = getCaseObject();
        if(caseObject==null) return null;
        return caseObject.getExtAppType();
    }

    private Long caseObjectId;
    private CaseObject lastState;
    private CaseObject initState;
    private CaseComment newComment;
    private CaseComment oldComment;
    private CaseComment removedComment;
    private Collection<Attachment> addedAttachments;
    private Collection<Attachment> removedAttachments;
    private DiffCollectionResult <CaseLink> mergeLinks;



    private Person initiator;
    private ServiceModule serviceModule;
    private List<CaseComment> caseComments;
    private boolean isEagerEvent;
    // Measured in ms
    private final long timeCreated;
    private long lastUpdated;

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

//    public AssembledCaseEvent( Object source, CaseObject lastState, Person initiator) {
//        this(ServiceModule.GENERAL, source, lastState, lastState, initiator);
//    }
//
//    public AssembledCaseEvent(Object source, CaseObject initState, CaseObject lastState,
//                              Person currentPerson) {
//        this(ServiceModule.GENERAL, source, initState, lastState, currentPerson);
//    }
//
    public void attachEvent( CaseObjectEvent objectEvent ) {
    //    public AssembledCaseEvent(CaseObjectEvent objectEvent) {
    //        this(objectEvent.getServiceModule(), objectEvent.getSource(), objectEvent.getOldState(),
    //                objectEvent.getNewState(), objectEvent.getPerson());
        this.lastUpdated = currentTimeMillis();
        this.initState = objectEvent.getOldState();
        this.lastState = objectEvent.getNewState();
        this.initiator = objectEvent.getPerson();
        this.serviceModule = objectEvent.getServiceModule();
//        this.timeCreated = currentTimeMillis();
        this.addedAttachments = new ArrayList<>();
        this.removedAttachments = new ArrayList<>();
        mergeLinks = objectEvent.getMergeLinks();
        isEagerEvent = isEagerEvent||objectEvent.isEagerEvent();
    }

    public void attachEvent( CaseCommentEvent commentEvent ) {
//    public AssembledCaseEvent(CaseCommentEvent commentEvent) {
//        this(commentEvent.getServiceModule(), commentEvent.getSource(), commentEvent.getOldState(), commentEvent.getNewState(),
//                commentEvent.getPerson());
        this.lastUpdated = currentTimeMillis();
        oldComment = commentEvent.getOldCaseComment();
        newComment = commentEvent.getNewCaseComment();
        addedAttachments.addAll(commentEvent.getAddedAttachments());
        removedAttachments.addAll(commentEvent.getRemovedAttachments());
        removedComment = commentEvent.getRemovedCaseComment();
        isEagerEvent = isEagerEvent||commentEvent.isEagerEvent();
    }


    public void attachEvent( CaseObjectCommentEvent objectCommentEvent ) {
//    public AssembledCaseEvent(CaseObjectCommentEvent objectCommentEvent) {
//            this(objectCommentEvent.getServiceModule(), objectCommentEvent.getSource(),
//                    objectCommentEvent.getOldState(), objectCommentEvent.getNewState(),
//                    objectCommentEvent.getPerson());
        this.lastUpdated = currentTimeMillis();
        oldComment = objectCommentEvent.getOldCaseComment();
        newComment = objectCommentEvent.getCaseComment();
        addedAttachments.addAll( objectCommentEvent.getAddedAttachments() );
        removedAttachments.addAll( objectCommentEvent.getRemovedAttachments() );
        removedComment = objectCommentEvent.getRemovedCaseComment();
        mergeLinks = objectCommentEvent.getMergeLinks();
        isEagerEvent = isEagerEvent || objectCommentEvent.isEagerEvent();
    }

    public void attachEvent( CaseAttachmentEvent event ) {
        this.lastUpdated = currentTimeMillis();
        synchronizeAttachments( event.getAddedAttachments(), event.getRemovedAttachments() );

    }
//    public AssembledCaseEvent(CaseAttachmentEvent attachmentEvent) {
//        this(attachmentEvent.getSource(), attachmentEvent.getCaseObject(), attachmentEvent.getPerson());
//        addedAttachments.addAll(attachmentEvent.getAddedAttachments());
//        removedAttachments.addAll(attachmentEvent.getRemovedAttachments());
//        serviceModule = attachmentEvent.getServiceModule();
//    }
//
//    public AssembledCaseEvent(ServiceModule module, Object source,
//                              CaseObject state, CaseObject lastState, Person currentPerson) {
//        super(source);
//        this.initState = state;
//        this.lastState = lastState;
//        this.initiator = currentPerson;
//        this.serviceModule = module;
//        this.timeCreated = currentTimeMillis();
//        this.lastUpdated = timeCreated;
//        this.addedAttachments = new ArrayList<>();
//        this.removedAttachments = new ArrayList<>();
//
//    }

    public CaseComment getCaseComment() {
        return newComment;
    }

    public void setNewComment( CaseComment newComment ) {
        this.newComment = newComment;
    }

//    public boolean isLastStateSet() {
//        return lastState != null;
//    }

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

//    public void attachCaseObject(CaseObject caseObject) {
//        lastState = caseObject;
//        lastUpdated = currentTimeMillis();
//    }

    public void attachCaseComment(CaseComment caseComment) {
        newComment = caseComment;
        lastUpdated = currentTimeMillis();
    }

    public void includeCaseComments (List<CaseComment> commentList) {
        newComment = CollectionUtils.lastOrDefault(commentList, newComment );
        lastUpdated = currentTimeMillis();
    }

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

    public Collection<Attachment> getRemovedAttachments() {
        return removedAttachments;
    }

    private void synchronizeAttachments(Collection<Attachment> added, Collection<Attachment> removed){
        if(added == null)
            added = Collections.emptyList();
        if(removed == null)
            removed = Collections.emptyList();

        addedAttachments.addAll(added);
        removedAttachments.addAll(removed);

        Iterator<Attachment> it = removedAttachments.iterator();
        while (it.hasNext()){
            Attachment removedAttachment = it.next();
            boolean itRemove = false;
            if(addedAttachments.contains(removedAttachment)){ //if you add and remove an attachment in a row
                addedAttachments.remove(removedAttachment);
                itRemove = true;
            }
            if(lastState.getAttachments().contains(removedAttachment)){ //remove not mailed attachment in comment
                lastState.getAttachments().remove(removedAttachment);
                itRemove = true;
            }
            if (itRemove) {
                it.remove();
            }
        }
        lastUpdated = currentTimeMillis();
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public ServiceModule getServiceModule() {
        return serviceModule != null ? serviceModule : ServiceModule.GENERAL;
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

    private static final Logger log = LoggerFactory.getLogger( AssembledCaseEvent.class );

    @Override
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }
}
