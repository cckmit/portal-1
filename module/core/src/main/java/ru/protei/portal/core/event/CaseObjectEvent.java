package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;

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

    private CaseObjectEvent(Object source, ServiceModule module, CaseObject newState, CaseObject oldState, Person person) {
        super(source);
        this.serviceModule = module;
        this.newState = newState;
        this.oldState = oldState;
        this.person = person;
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


    public static class Builder {

        private Object source;
        private ServiceModule serviceModule;
        private CaseObject newState;
        private CaseObject oldState;
        private Person person;

        public Builder(Object source) {
            this.source = source;
            this.serviceModule = ServiceModule.GENERAL;
        }

        public Builder(Object source, ServiceModule serviceModule) {
            this.source = source;
            this.serviceModule = serviceModule;
        }

        public Builder withNewState(CaseObject newState) {
            this.newState = newState;
            return this;
        }

        public Builder withOldState(CaseObject oldState) {
            this.oldState = oldState;
            return this;
        }

        public Builder withPerson(Person person) {
            this.person = person;
            return this;
        }

        public CaseObjectEvent build() {
            return new CaseObjectEvent(
                    source,
                    serviceModule,
                    newState,
                    oldState,
                    person
            );
        }
    }
}
