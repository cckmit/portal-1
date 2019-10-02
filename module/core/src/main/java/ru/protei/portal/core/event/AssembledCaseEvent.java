package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;

import java.util.*;

import static java.lang.System.currentTimeMillis;

public class AssembledCaseEvent extends ApplicationEvent {

    private CaseObject lastState;
    private CaseObject initState;
    private CaseComment comment;
    private CaseComment oldComment;
    private CaseComment removedComment;
    private Collection<Attachment> addedAttachments;
    private Collection<Attachment> removedAttachments;
    public Collection<Attachment> addedLinks;



    private Person initiator;
    private ServiceModule serviceModule;
    // Measured in ms
    private final long timeCreated;
    private long lastUpdated;

    public AssembledCaseEvent(Object source, CaseObject lastState, Person initiator) {
        this(ServiceModule.GENERAL, source, lastState, lastState, initiator);
    }

    public AssembledCaseEvent(Object source, CaseObject initState, CaseObject lastState,
                              Person currentPerson) {
        this(ServiceModule.GENERAL, source, initState, lastState, currentPerson);
    }

    public AssembledCaseEvent(CaseObjectEvent objectEvent) {
        this(objectEvent.getServiceModule(), objectEvent.getSource(), objectEvent.getOldState(),
                objectEvent.getNewState(), objectEvent.getPerson());
    }

    public AssembledCaseEvent(CaseCommentEvent commentEvent) {
        this(commentEvent.getServiceModule(), commentEvent.getSource(), commentEvent.getOldState(), commentEvent.getNewState(),
                commentEvent.getPerson());
        oldComment = commentEvent.getOldCaseComment();
        comment = commentEvent.getCaseComment();
        addedAttachments.addAll(commentEvent.getAddedAttachments());
        removedAttachments.addAll(commentEvent.getRemovedAttachments());
        removedComment = commentEvent.getRemovedCaseComment();
    }

    public AssembledCaseEvent(CaseObjectCommentEvent objectCommentEvent) {
        this(objectCommentEvent.getServiceModule(), objectCommentEvent.getSource(),
                objectCommentEvent.getOldState(), objectCommentEvent.getNewState(),
                objectCommentEvent.getPerson());
        oldComment = objectCommentEvent.getOldCaseComment();
        comment = objectCommentEvent.getCaseComment();
        addedAttachments.addAll(objectCommentEvent.getAddedAttachments());
        removedAttachments.addAll(objectCommentEvent.getRemovedAttachments());
        removedComment = objectCommentEvent.getRemovedCaseComment();
    }

    public AssembledCaseEvent(CaseAttachmentEvent attachmentEvent) {
        this(attachmentEvent.getSource(), attachmentEvent.getCaseObject(), attachmentEvent.getPerson());
        addedAttachments.addAll(attachmentEvent.getAddedAttachments());
        removedAttachments.addAll(attachmentEvent.getRemovedAttachments());
        serviceModule = attachmentEvent.getServiceModule();
    }

    public AssembledCaseEvent(ServiceModule module, Object source,
                              CaseObject state, CaseObject lastState, Person currentPerson) {
        super(source);
        this.initState = state;
        this.lastState = lastState;
        this.initiator = currentPerson;
        this.serviceModule = module;
        this.timeCreated = currentTimeMillis();
        this.lastUpdated = timeCreated;
        this.addedAttachments = new ArrayList<>();
        this.removedAttachments = new ArrayList<>();

    }

    public CaseComment getCaseComment() {
        return comment;
    }

    public void setComment(CaseComment comment) {
        this.comment = comment;
    }

    public boolean isLastStateSet() {
        return lastState != null;
    }

    public boolean isCreateEvent() {
        return this.initState == null;
    }

    public boolean isUpdateEvent() {
        return this.initState != null;
    }

    public boolean isCommentAttached() {
        return this.comment != null;
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

    public void attachCaseObject(CaseObject caseObject) {
        lastState = caseObject;
        lastUpdated = currentTimeMillis();
    }

    public void attachCaseComment(CaseComment caseComment) {
        comment = caseComment;
        lastUpdated = currentTimeMillis();
    }

    public void includeCaseComments (List<CaseComment> commentList) {
        comment = CollectionUtils.lastOrDefault(commentList, comment);
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

    public void synchronizeAttachments(Collection<Attachment> added, Collection<Attachment> removed){
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
        return comment != null && !comment.isPrivateComment();
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
}
