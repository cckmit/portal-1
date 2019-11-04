package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.util.DiffCollectionResult;

import java.util.Collection;
import java.util.Date;

/**
 * Created by michael on 04.05.17.
 */
public class CaseObjectEvent extends ApplicationEvent implements AbstractCaseEvent {

    private CaseObject newState;
    private CaseObject oldState;
    private Person person;
    private ServiceModule serviceModule;
    private DiffCollectionResult<CaseLink> mergeLinks;

    public CaseObjectEvent(  Object source, ServiceModule serviceModule, Person person, CaseObject oldState,  CaseObject newState ) {
        super(source);
        this.serviceModule = serviceModule;
        this.person = person;
        this.oldState = oldState;
        this.newState = newState;
    }

    public boolean isCreateEvent () {
        return this.oldState == null;
    }

    public boolean isUpdateEvent () {
        return this.oldState != null;
    }

    public boolean isCaseStateChanged () {
        return isUpdateEvent() && newState.getState() != oldState.getState();
    }

    public boolean isCaseImportanceChanged () {
        return isUpdateEvent() && !newState.getImpLevel().equals(oldState.getImpLevel());
    }

    public boolean isManagerChanged () {
        return isUpdateEvent() && !HelperFunc.equals(newState.getManagerId(),oldState.getManagerId());
    }

    public boolean isProductChanged() {
        return isUpdateEvent() && !HelperFunc.equals( newState.getProductId(), oldState.getProductId() );
    }

    public boolean isInitiatorChanged() {
        return isUpdateEvent() && !HelperFunc.equals( newState.getInitiatorId(), oldState.getInitiatorId() );
    }

    public boolean isInitiatorCompanyChanged() {
        return isUpdateEvent() && !HelperFunc.equals( newState.getInitiatorCompanyId(), oldState.getInitiatorCompanyId() );
    }

    public boolean isInfoChanged() {
        return isUpdateEvent() && !HelperFunc.equals( newState.getInfo(), oldState.getInfo() );
    }

    public boolean isNameChanged() {
        return isUpdateEvent() && !HelperFunc.equals( newState.getName(), oldState.getName() );
    }

    public boolean isPrivacyChanged(){
        return isUpdateEvent() && newState.isPrivateCase() != oldState.isPrivateCase();
    }

    public ServiceModule getServiceModule() {
        return serviceModule != null ? serviceModule : ServiceModule.GENERAL;
    }

    public Date getEventDate () {
        return new Date(getTimestamp());
    }

    public CaseObject getCaseObject () {
        return newState != null ? newState : oldState;
    }

    public CaseObject getNewState() {
        return newState;
    }

    public CaseObject getOldState() {
        return oldState;
    }

    public CaseComment getCaseComment() { return null; }

    public CaseComment getOldCaseComment() { return null; }

    public CaseComment getRemovedCaseComment() { return null; }

    public Collection<Attachment> getAddedAttachments() { return null; }

    public Collection<Attachment> getRemovedAttachments() { return null; }

    public Person getPerson() {
        return person;
    }

    @Override
    public Long getCaseObjectId() {
        CaseObject caseObject = getCaseObject();
        if(caseObject==null) return null;
        return caseObject.getId();
    }

    @Override
    public boolean isEagerEvent() {
        CaseObject caseObject = getCaseObject();
        if(caseObject==null) return false;
        return En_ExtAppType.REDMINE.equals( caseObject.getExtAppType() );
    }

    public DiffCollectionResult<CaseLink> getMergeLinks() {
        return mergeLinks;
    }



//    public CaseObjectEvent withNewState(CaseObject newState) {
//        this.newState = newState;
//        return this;
//    }
//
//    public CaseObjectEvent withOldState(CaseObject oldState) {
//        this.oldState = oldState;
//        return this;
//    }

//    public CaseObjectEvent withPerson(Person person) {
//        this.person = person;
//        return this;
//    }
    public ApplicationEvent withLinks( DiffCollectionResult<CaseLink> mergeLinks ) {
        this.mergeLinks = mergeLinks;
        return this;
    }



}
