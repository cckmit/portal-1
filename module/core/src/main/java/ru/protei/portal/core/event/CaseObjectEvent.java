package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.service.CaseService;

import java.util.Date;

/**
 * Created by michael on 04.05.17.
 */
public class CaseObjectEvent extends ApplicationEvent {

    private CaseObject newState;
    private CaseObject oldState;
    private Person person;
    private ServiceModule serviceModule;

    public CaseObjectEvent( Object source, CaseObject newState, Person initiator ) {
        this (ServiceModule.GENERAL, source, newState, null, initiator);
    }

    public CaseObjectEvent( Object source, CaseObject newState, CaseObject oldState, Person currentPerson ){
        this (ServiceModule.GENERAL, source, newState, oldState, currentPerson);
    }

    public CaseObjectEvent( ServiceModule module, Object source, CaseObject newState, CaseObject oldState, Person currentPerson ) {
        super( source );
        this.newState = newState;
        this.oldState = oldState;
        this.person = currentPerson;
        this.serviceModule = module;
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

    public Person getPerson() {
        return person;
    }
}
