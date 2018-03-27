package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.service.CaseService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import static java.lang.System.currentTimeMillis;

public class AssembledCaseEvent extends ApplicationEvent {

    private CaseObject lastState;
    private CaseObject initState;
    private CaseComment comment;
    private CaseComment oldComment;
    private Collection<Attachment> addedAttachments;
    private Collection<Attachment> removedAttachments;


    private Person initiator;
    private ServiceModule serviceModule;
    // Measured in ms
    private final long timeCreated;
    private long lastUpdated;

    public AssembledCaseEvent(CaseService caseService, CaseObject lastState, Person initiator) {
        this(ServiceModule.GENERAL, caseService, lastState, lastState, initiator);
    }

    public AssembledCaseEvent(CaseService caseService, CaseObject initState, CaseObject lastState,
                              Person currentPerson) {
        this(ServiceModule.GENERAL, caseService, initState, lastState, currentPerson);
    }

    public AssembledCaseEvent(CaseObjectEvent objectEvent) {
        this(objectEvent.getServiceModule(), objectEvent.getCaseService(), objectEvent.getOldState(),
                objectEvent.getNewState(), objectEvent.getPerson());
    }

    public AssembledCaseEvent(CaseCommentEvent commentEvent) {
        this(commentEvent.getServiceModule(), commentEvent.getCaseService(), commentEvent.getCaseObject(), commentEvent.getCaseObject(),
                commentEvent.getPerson());
        oldComment = commentEvent.getOldCaseComment();
        comment = commentEvent.getCaseComment();
        addedAttachments.addAll(commentEvent.getAddedAttachments());
        removedAttachments.addAll(commentEvent.getRemovedAttachments());
    }

    public AssembledCaseEvent(CaseAttachmentEvent attachmentEvent) {
        this(attachmentEvent.getCaseService(), attachmentEvent.getCaseObject(), attachmentEvent.getPerson());
        addedAttachments.addAll(attachmentEvent.getAddedAttachments());
        removedAttachments.addAll(attachmentEvent.getRemovedAttachments());
        serviceModule = attachmentEvent.getServiceModule();
    }

    public AssembledCaseEvent(ServiceModule module, CaseService caseService,
                              CaseObject state, CaseObject lastState, Person currentPerson) {
        super(caseService);
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

    public boolean isCaseCommentAttached() {
        return this.comment != null;
    }

    public boolean isCaseStateChanged() {
        return isUpdateEvent() && lastState.getState() != initState.getState();
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

    public void attachCaseObject(CaseObject caseObject) {
        lastState = caseObject;
        lastUpdated = currentTimeMillis();
    }

    public void attachCaseComment(CaseComment caseComment) {
        comment = caseComment;
        lastUpdated = currentTimeMillis();
    }

    public CaseComment getOldComment() {
        return oldComment;
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

        for(Attachment attachment: removedAttachments){
            if(addedAttachments.contains(attachment)){ //if you add and remove an attachment in a row
                addedAttachments.remove(attachment);
                removedAttachments.remove(attachment);
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

    public CaseService getCaseService() {
        return (CaseService) getSource();
    }

    public Person getInitiator() {
        return initiator;
    }
}
